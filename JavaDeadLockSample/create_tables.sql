DROP DATABASE IF EXISTS test_deadrock_db;
CREATE DATABASE test_deadrock_db ENCODING=UTF8;


-- 在庫テーブル
CREATE TABLE inventory (
    inv_id       BIGSERIAL PRIMARY KEY,      -- 在庫レコード ID
    product_code VARCHAR(20) NOT NULL,      -- 製品コード
    quantity     INTEGER   NOT NULL CHECK (quantity >= 0), -- 在庫数
    created_at   TIMESTAMP WITH TIME ZONE DEFAULT now(),
    updated_at   TIMESTAMP WITH TIME ZONE DEFAULT now()
);

-- 引当テーブル
CREATE TABLE allocation (
    alloc_id     BIGSERIAL PRIMARY KEY,
    inv_id       BIGINT NOT NULL REFERENCES inventory(inv_id) ON DELETE RESTRICT,
    allocated_qty INTEGER NOT NULL CHECK (allocated_qty > 0),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    alloc_user   VARCHAR(50) NOT NULL,
    created_at   TIMESTAMP WITH TIME ZONE DEFAULT now()
);

-- 出庫テーブル
CREATE TABLE outbound (
    out_id       BIGSERIAL PRIMARY KEY,
    alloc_id     BIGINT NOT NULL REFERENCES allocation(alloc_id) ON DELETE RESTRICT,
    shipped_qty  INTEGER NOT NULL CHECK (shipped_qty > 0),
    ship_date    DATE NOT NULL,
    created_at   TIMESTAMP WITH TIME ZONE DEFAULT now()
);

-- 在庫テーブルロック管理テーブル
CREATE TABLE inventory_lock (
    lock_id      BIGSERIAL PRIMARY KEY,      -- ロックレコード ID
    inv_id       BIGINT NOT NULL REFERENCES inventory(inv_id) ON DELETE RESTRICT -- 在庫レコード参照
);
