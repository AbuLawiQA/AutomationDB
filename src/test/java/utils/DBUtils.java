package utils;

import java.sql.*;

public class DBUtils {
    private static final String URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_NAME = "E2ETestDB";
    private static final String USER = "root";
    private static final String PASS = "";
    //private static final String PASS = "123456";

    // إنشاء قاعدة البيانات والجدول إذا لم تكن موجودة
    public static void createDBAndTableIfNotExist() {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             Statement stmt = conn.createStatement()) {

            // Create DB
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
            stmt.executeUpdate("USE " + DB_NAME);

            // Create Table
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS orders (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT," +
                    "product_name VARCHAR(255)" +
                    ")");
            System.out.println("✅ قاعدة البيانات والجدول جاهزين!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void insertOrder(String productName) {
        if (checkOrderExists(productName)) {
            System.out.println("⚠️ المنتج موجود مسبقًا في قاعدة البيانات: " + productName);
            return;
        }

        try (Connection conn = DriverManager.getConnection(URL + DB_NAME, USER, PASS)) {
            String query = "INSERT INTO orders (product_name) VALUES (?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, productName);
            stmt.executeUpdate();
            System.out.println("✅ تم إدخال المنتج: " + productName);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean checkOrderExists(String productName) {
        boolean exists = false;
        try (Connection conn = DriverManager.getConnection(URL + DB_NAME, USER, PASS)) {
            String query = "SELECT * FROM orders WHERE product_name = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, productName);
            ResultSet rs = stmt.executeQuery();
            exists = rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return exists;
    }

    public static void deleteOrderByName(String productName) {
        try (Connection conn = DriverManager.getConnection(URL + DB_NAME, USER, PASS)) {

            String query = "DELETE FROM orders WHERE TRIM(product_name) = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, productName.trim());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("🗑️ تم حذف المنتج: " + productName);
            } else {
                System.out.println("❗ لم يتم العثور على المنتج لحذفه: " + productName);
            }

        } catch (SQLException e) {
            System.out.println("🚫 خطأ أثناء محاولة الحذف من قاعدة البيانات:");
            e.printStackTrace();
        }
    }

    public static void clearOrdersTable() {
        try (Connection conn = DriverManager.getConnection(URL + DB_NAME, USER, PASS);
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate("DELETE FROM orders");
            System.out.println("🧹 تم مسح جميع البيانات من جدول orders");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
