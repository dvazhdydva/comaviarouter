package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnectionPool {
    public static Connection connection;

    static {
        try {
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "glavnyjHuy");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
