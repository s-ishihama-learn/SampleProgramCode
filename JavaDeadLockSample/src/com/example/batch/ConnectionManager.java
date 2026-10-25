package com.example.batch;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * データベース接続を管理するユーティリティクラス。
 * DB の接続情報はこのクラスに一元化し、他のバッチクラスからは
 * ConnectionManager.getConnection() を呼び出すだけで取得できます。
 */
public class ConnectionManager {
    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("PostgreSQL JDBC driver not found", e);
        }
    }

    private static final String DB_URL = "jdbc:postgresql://localhost:5432/test_deadrock_db";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "password";

    private ConnectionManager() {
        // インスタンス化禁止
    }

    /**
     * データベースへの接続を取得します。
     *
     * @return Connection オブジェクト
     * @throws SQLException 接続に失敗した場合
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
}
