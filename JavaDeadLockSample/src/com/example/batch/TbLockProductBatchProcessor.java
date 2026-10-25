package com.example.batch;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * バッチ処理プログラム（引当済み製品）
 *
 * 1. 事前に定義した 10 件の製品コードを順に処理する。
 * 2. 在庫テーブル (inventory) から該当レコードを取得し、在庫数を 1 減らす。
 * 3. 既に "ALLOCATED" 状態の仮引当 (allocation) が存在すれば、出庫指示 (outbound) を作成する。
 * （このバッチは在庫を減らすだけで、allocation のステータスは変更しない）
 *
 * 目的は「製品コードを指定して、引当済みの製品を在庫数を減らし、出庫指示を行う」ことです。
 */
public class TbLockProductBatchProcessor {
	public static void main(String[] args) {
		// コマンドライン引数で製品コードを 1 つ受け取る
		if (args.length < 1) {
			System.err.println("使用方法: java ProductBatchProcessor <product_code>");
			return;
		}
		String prodCode = args[0];

		try (Connection conn = ConnectionManager.getConnection()) {
			conn.setAutoCommit(false);
			try {
				// 0. inventory_lock テーブルの制御レコードをロック（テーブルロック）
				// SELECT FOR UPDATE により、同時実行バッチは此処でブロックされる
				String lockSql = "SELECT lock_id FROM inventory_lock FOR UPDATE";
				try (PreparedStatement psLock = conn.prepareStatement(lockSql)) {
					psLock.executeQuery(); // ロック取得完了
					System.out.println("inventory_lock テーブルのロックを取得しました。");
				}

				// 1. 引当テーブルから条件に合う行を取得
				String selectInventory = "SELECT alloc_id, T1.inv_id, allocated_qty, product_code, quantity FROM allocation T1 INNER JOIN inventory T2 ON T1.inv_id = T2.inv_id WHERE T2.product_code=? AND status = 'PENDING' ORDER BY alloc_id LIMIT 1";
				try (PreparedStatement psSelect = conn.prepareStatement(selectInventory,
						ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
					psSelect.setString(1, prodCode);
					ResultSet rsInv = psSelect.executeQuery();
					while (rsInv.next()) {
						long allocId = rsInv.getLong("alloc_id");
						String productCode = rsInv.getString("product_code");
						long invId = rsInv.getLong("inv_id");
						int quantity = rsInv.getInt("quantity");
						int allocQuantity = rsInv.getInt("allocated_qty");
						int newQty = quantity - allocQuantity;

						if (newQty < 0) {
							// 在庫が足りない場合は更新しない
							System.out.println("Empty quantity product code=" + productCode);
							continue;
						}

						// 2. 在庫に対する在庫数更新
						System.out.println("Target alloc_id=" + allocId + "    product code=" + productCode);
						String updateInventory = "UPDATE inventory SET quantity = ? WHERE inv_id = ?";
						try (PreparedStatement psUpdInvt = conn.prepareStatement(updateInventory)) {
							psUpdInvt.setLong(1, newQty);
							psUpdInvt.setLong(2, invId);
							psUpdInvt.executeUpdate();
						}

						// 3. 該当在庫に対する allocation を取得し status を更新
						String updateAllocation = "UPDATE allocation SET status = 'ALLOCATED' WHERE inv_id = ? AND status = 'PENDING'";
						try (PreparedStatement psUpdAlloc = conn.prepareStatement(updateAllocation)) {
							psUpdAlloc.setLong(1, invId);
							psUpdAlloc.executeUpdate();
						}

						// 4. 出庫指示を作成（shipped_qty = 1, ship_date = today）
						String insertOutbound = "INSERT INTO outbound (alloc_id, shipped_qty, ship_date) " +
								"SELECT alloc_id, ?, CURRENT_DATE FROM allocation WHERE inv_id = ? AND status = 'ALLOCATED'";
						try (PreparedStatement psInsOut = conn.prepareStatement(insertOutbound)) {
							psInsOut.setLong(1, allocQuantity);
							psInsOut.setLong(2, invId);
							psInsOut.executeUpdate();
						}
					}
				}
				conn.commit();
				System.out.println("AllocatedProductBatchProcessor が正常に完了しました。");
			} catch (SQLException e) {
				conn.rollback();
				System.err.println("エラーが発生したためロールバックしました: " + e.getMessage());
				throw e;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
