package com.suhan;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton pattern class for intiating SQLiteJDBC.
 */
public class SQLiteJDBC {

    private static SQLiteJDBC instance = null;
    private Connection connection = null;

    protected SQLiteJDBC() {

    }

    public static SQLiteJDBC getInstacne() {
        if (instance == null) {
            instance = new SQLiteJDBC();
        }
        return instance;
    }

    public void initDBConnection(String dbPath) {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }


}
