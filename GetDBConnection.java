package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class GetDBConnection {
    public static Connection connectionDB(String DBName, String id, String p) {
        Connection con = null;
        String uri = "jdbc:mysql://localhost:3306/" + DBName + "?useSSL=true&ServerTimezone=GTM&characterEncoding=utf-8";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        try {
            con = DriverManager.getConnection(uri, id, p);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return con;
    }
}
