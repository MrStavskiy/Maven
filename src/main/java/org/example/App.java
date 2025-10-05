package org.example;

import org.flywaydb.core.Flyway;

import javax.sql.rowset.JdbcRowSet;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.*;
import java.util.Properties;

import static java.sql.DriverManager.getConnection;

public class App {
    public static void main(String[] args) {
        Properties properties = loadProperties();
        String url = properties.getProperty("db.url");
        String user = properties.getProperty("db.user");
        String password = properties.getProperty("db.password");

        try (Connection conn = getConnection(url, user, password)) {
            conn.setAutoCommit(false);  // Отключение автокоммита для транзакций

            // Выполнение миграций, если используется Flyway
            migrateDatabase(url, user, password);

            // CRUD
            insertProductAndCustomer(conn);
            createOrder(conn);
            readLastFiveOrders(conn);
            updateProductPriceAndQuantity(conn);
            deleteTestRecords(conn);

            // Завершение транзакции
            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
            // В случае ошибки откатить изменения
            try {
                JdbcRowSet conn = null;
                conn.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
        }
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();
        try (InputStream input = App.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find application.properties");
                return properties;
            }
            properties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return properties;
    }

    private static void migrateDatabase(String url, String user, String password) {
        Flyway flyway = Flyway.configure()
                .dataSource(url, user, password)
                .load();
        flyway.migrate();
    }

    private static void insertProductAndCustomer(Connection conn) throws SQLException {
        // Вставка нового товара
        String insertProductSql = "INSERT INTO product (description, price, quantity, category) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(insertProductSql)) {
            stmt.setString(1, "Smart TV");
            stmt.setBigDecimal(2, new BigDecimal("799.99"));
            stmt.setInt(3, 10);
            stmt.setString(4, "Electronics");
            stmt.executeUpdate();
        }

        // Вставка нового покупателя
        String insertCustomerSql = "INSERT INTO customer (first_name, last_name, phone, email) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(insertCustomerSql)) {
            stmt.setString(1, "Ivan");
            stmt.setString(2, "Smirnov");
            stmt.setString(3, "123-456-7890");
            stmt.setString(4, "ivan.smirnov@example.com");
            stmt.executeUpdate();
        }
    }

    private static void createOrder(Connection conn) throws SQLException {
        // Создание нового заказа
        String createOrderSql = "INSERT INTO \"order\" (product_id, customer_id, quantity, status) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(createOrderSql)) {
            stmt.setInt(1, 1); // product_id (предположим, что товар с ID 1 уже существует)
            stmt.setInt(2, 1); // customer_id (предположим, что покупатель с ID 1 уже существует)
            stmt.setInt(3, 1); // quantity
            stmt.setInt(4, 1); // статус (предположим, что статус с ID 1 существует)
            stmt.executeUpdate();
        }
    }

    private static void readLastFiveOrders(Connection conn) throws SQLException {
        // Чтение последних 5 заказов с JOIN на товары и покупателей
        String query = "SELECT o.id AS order_id, o.order_date, c.first_name, c.last_name, p.description, o.quantity " +
                "FROM \"order\" o " +
                "JOIN customer c ON o.customer_id = c.id " +
                "JOIN product p ON o.product_id = p.id " +
                "ORDER BY o.order_date DESC LIMIT 5";

        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                System.out.println("Order ID: " + rs.getInt("order_id"));
                System.out.println("Date: " + rs.getTimestamp("order_date"));
                System.out.println("Customer: " + rs.getString("first_name") + " " + rs.getString("last_name"));
                System.out.println("Product: " + rs.getString("description"));
                System.out.println("Quantity: " + rs.getInt("quantity"));
                System.out.println("-------------------------------");
            }
        }
    }

    private static void updateProductPriceAndQuantity(Connection conn) throws SQLException {
        // Обновление цены и количества товара
        String updateProductSql = "UPDATE product SET price = ?, quantity = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(updateProductSql)) {
            stmt.setBigDecimal(1, new BigDecimal("849.99"));
            stmt.setInt(2, 8);
            stmt.setInt(3, 1); // product_id
            stmt.executeUpdate();
        }
    }

    private static void deleteTestRecords(Connection conn) throws SQLException {
        // Удаление тестовых записей
        String deleteOrderSql = "DELETE FROM \"order\" WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(deleteOrderSql)) {
            stmt.setInt(1, 1); // Удаляем заказ с id = 1
            stmt.executeUpdate();
        }

        String deleteCustomerSql = "DELETE FROM customer WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(deleteCustomerSql)) {
            stmt.setInt(1, 1); // Удаляем клиента с id = 1
            stmt.executeUpdate();
        }
    }
}
