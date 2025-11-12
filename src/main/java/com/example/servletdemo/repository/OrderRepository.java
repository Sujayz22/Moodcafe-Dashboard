package com.example.servletdemo.repository;

import com.example.servletdemo.model.OrderEntity;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


public class OrderRepository {
    private static final String DEFAULT_URL = "jdbc:h2:mem:ordersdb;DB_CLOSE_DELAY=-1";
    private static final String DEFAULT_USER = "sa";
    private static final String DEFAULT_PASS = "";

    private static final String JDBC_URL;
    private static final String JDBC_USER;
    private static final String JDBC_PASS;
    private static final boolean IS_POSTGRES;

    static {
        Properties p = new Properties();
        try (InputStream in = OrderRepository.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (in != null) p.load(in);
        } catch (IOException ignored) {
        }

        
        JDBC_URL = System.getProperty("jdbc.url", p.getProperty("jdbc.url", DEFAULT_URL));
        JDBC_USER = System.getProperty("jdbc.user", p.getProperty("jdbc.user", DEFAULT_USER));
        JDBC_PASS = System.getProperty("jdbc.pass", p.getProperty("jdbc.pass", DEFAULT_PASS));

       
        String driverClass = System.getProperty("jdbc.driver", p.getProperty("jdbc.driver"));
        String dbType = System.getProperty("db.type", p.getProperty("db.type"));

        boolean isPostgres = false;
        if (driverClass != null) {
            isPostgres = driverClass.toLowerCase().contains("postgres");
            try {
               
                Class.forName(driverClass);
            } catch (ClassNotFoundException ignored) {
            }
        }
        if (!isPostgres) {
            if (dbType != null) isPostgres = dbType.equalsIgnoreCase("postgresql") || dbType.equalsIgnoreCase("postgres");
        }
        if (!isPostgres) {
            if (JDBC_URL != null) isPostgres = JDBC_URL.toLowerCase().contains("postgresql");
        }

        IS_POSTGRES = isPostgres;
    }

    public static void updateStatus(int id, String status) throws SQLException {
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement("UPDATE ORDERS SET STATUS = ? WHERE ID = ?")) {
            ps.setString(1, status);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

   
   
    public static void initDatabase() throws SQLException {
        try (Connection c = getConnection(); Statement s = c.createStatement()) {
            
            s.execute("CREATE TABLE IF NOT EXISTS ORDERS (ID SERIAL PRIMARY KEY, NAME VARCHAR(255), AMOUNT DOUBLE PRECISION, STATUS VARCHAR(20) DEFAULT 'PENDING')");

        
            s.execute("CREATE TABLE IF NOT EXISTS MENU (ID SERIAL PRIMARY KEY, NAME VARCHAR(255) UNIQUE, PRICE DOUBLE PRECISION)");

            
            try (ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM ORDERS")) {
                rs.next();
                if (rs.getInt(1) == 0) {
                    try (PreparedStatement ps = c.prepareStatement("INSERT INTO ORDERS(NAME, AMOUNT) VALUES (?,?)")) {
                        ps.setString(1, "Latte"); ps.setDouble(2, 3.50); ps.executeUpdate();
                        ps.setString(1, "Cappuccino"); ps.setDouble(2, 4.00); ps.executeUpdate();
                        ps.setString(1, "Espresso"); ps.setDouble(2, 2.00); ps.executeUpdate();
                        ps.setString(1, "Latte"); ps.setDouble(2, 3.50); ps.executeUpdate();
                    }
                }
            }

            
            try (ResultSet rs2 = s.executeQuery("SELECT COUNT(*) FROM MENU")) {
                if (rs2.next() && rs2.getInt(1) == 0) {
                    try (PreparedStatement ps = c.prepareStatement("INSERT INTO MENU(NAME, PRICE) VALUES (?,?)")) {
                        ps.setString(1, "Latte"); ps.setDouble(2, 3.50); ps.executeUpdate();
                        ps.setString(1, "Cappuccino"); ps.setDouble(2, 4.00); ps.executeUpdate();
                        ps.setString(1, "Espresso"); ps.setDouble(2, 2.00); ps.executeUpdate();
                        ps.setString(1, "Tea"); ps.setDouble(2, 2.50); ps.executeUpdate();
                        ps.setString(1, "Muffin"); ps.setDouble(2, 2.75); ps.executeUpdate();
                    }
                }
            }
        }
    }

    public static List<OrderEntity> findAll() throws SQLException {
        List<OrderEntity> list = new ArrayList<>();
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement("SELECT ID, NAME, AMOUNT, STATUS FROM ORDERS ORDER BY ID")) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderEntity o = new OrderEntity(rs.getInt("ID"), rs.getString("NAME"), rs.getDouble("AMOUNT"));
                    String status = rs.getString("STATUS");
                    if (status != null) o.setStatus(status);
                    list.add(o);
                }
            }
        }
        return list;
    }

    
    public static java.util.Map<String, Double> getMenuMap() throws SQLException {
        java.util.Map<String, Double> map = new java.util.LinkedHashMap<>();
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement("SELECT NAME, PRICE FROM MENU ORDER BY ID")) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    map.put(rs.getString("NAME"), rs.getDouble("PRICE"));
                }
            }
        }
        return map;
    }

    public static Double getMenuPrice(String name) throws SQLException {
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement("SELECT PRICE FROM MENU WHERE NAME = ?")) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble("PRICE");
            }
        }
        return null;
    }

    public static void addMenuItem(String name, double price) throws SQLException {
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement("INSERT INTO MENU(NAME, PRICE) VALUES (?,?) ON CONFLICT (NAME) DO UPDATE SET PRICE = EXCLUDED.PRICE")) {
            ps.setString(1, name);
            ps.setDouble(2, price);
            ps.executeUpdate();
        }
    }

    public static void save(OrderEntity order) throws SQLException {
        try (Connection c = getConnection()) {
            
            if (IS_POSTGRES) {
                String sql = "INSERT INTO ORDERS(NAME, AMOUNT, STATUS) VALUES (?,?,?) RETURNING ID";
                try (PreparedStatement ps = c.prepareStatement(sql)) {
                    ps.setString(1, order.getName());
                    ps.setDouble(2, order.getAmount());
                    ps.setString(3, order.getStatus());
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) order.setId(rs.getInt(1));
                    }
                }
            } else {
                try (PreparedStatement ps = c.prepareStatement("INSERT INTO ORDERS(NAME, AMOUNT, STATUS) VALUES (?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, order.getName());
                    ps.setDouble(2, order.getAmount());
                    ps.setString(3, order.getStatus());
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (keys.next()) order.setId(keys.getInt(1));
                    }
                }
            }
        }
    }

    private static Connection getConnection() throws SQLException {
       
        return DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);
    }
}

