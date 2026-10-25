TRUNCATE TABLE inventory_lock RESTART IDENTITY CASCADE;
TRUNCATE TABLE outbound RESTART IDENTITY CASCADE;
TRUNCATE TABLE allocation RESTART IDENTITY CASCADE;
TRUNCATE TABLE inventory RESTART IDENTITY CASCADE;

-- 在庫テーブルに 1,000 件挿入
INSERT INTO inventory (product_code, quantity)
SELECT
    'PROD_' || LPAD((gs % 1000)::text, 4, '0') AS product_code,
    (random() * 500)::int + 1
FROM generate_series(1, 1000) AS gs;

-- 引当テーブルに 10,000 件挿入（各在庫に対して 1〜10 件の仮引当）
WITH inv AS (
    SELECT inv_id FROM inventory
)
INSERT INTO allocation (inv_id, allocated_qty, alloc_user, status)
SELECT
    inv.inv_id,
    (random() * 10 + 1)::int,                     -- 1〜10 の仮引当数
    'user_' || LPAD((gs % 20)::text, 2, '0'),
    'PENDING'
FROM inv
JOIN generate_series(1, 10) AS gs ON true;   -- 10 件ずつ生成し合計 10,000 件

-- 出庫テーブルは初期はゼロ件（INSERT は行わない）

-- ロック取得サンプル（制御レコード）
INSERT INTO inventory_lock (inv_id) VALUES (1);
