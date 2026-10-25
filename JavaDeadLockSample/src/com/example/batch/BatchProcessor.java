package com.example.batch;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * バッチ処理プログラム
 *
 * 1. 条件に合致する在庫レコードを取得し、在庫数を減らす。
 * 2. 対応する allocation の status を 'ALLOCATED' に更新。
 * 3. 出庫指示（outbound）を作成。
 *
 * 条件: quantity が 200 より大きい在庫レコード（テストデータの約 60% が該当）
 */
public class BatchProcessor {
	public static void main(String[] args) {
		try (Connection conn = ConnectionManager.getConnection()) {
			conn.setAutoCommit(false);
			try {
				// 1. 在庫テーブルから条件に合う行を取得
				String selectInventory = "SELECT alloc_id, T1.inv_id, allocated_qty, product_code, quantity FROM allocation T1 INNER JOIN inventory T2 ON T1.inv_id = T2.inv_id WHERE status = 'PENDING'";
				try (PreparedStatement psSelect = conn.prepareStatement(selectInventory,
						ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
					ResultSet rsInv = psSelect.executeQuery();
					while (rsInv.next()) {
						String productCode = rsInv.getString("product_code");
						// 製品コードの数字部分が 100 で割り切れる場合は処理をスキップ
						int numericPart = Integer.parseInt(productCode.replaceAll("\\D", ""));
						if (numericPart % 100 == 0) {
							System.out.println("Skip product code=" + productCode);
							continue;
						}

						long invId = rsInv.getLong("inv_id");
						int quantity = rsInv.getInt("quantity");
						int allocQuantity = rsInv.getInt("allocated_qty");
						int newQty = quantity - allocQuantity;

						// 取得ロック: 在庫レコードをロック（アドバイザリロック）
						try (PreparedStatement psLock = conn.prepareStatement(
								"SELECT pg_advisory_xact_lock(?)")) {
							psLock.setLong(1, invId);
							psLock.executeQuery(); // ロック取得完了
						}

						if (newQty < 0) {
							// 在庫が足りない場合は更新しない
							System.out.println("Empty quantity product code=" + productCode);
							continue;
						}

						// 2. 在庫に対する在庫数更新
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
				System.out.println("バッチ処理が正常に完了しました。");
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
