// application.properties


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateDatabase {

    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String user = "postgres"; // имя пользователя
        String password = "User1986!"; // пароль
        String dbName = "mynewdatabase"; // Новая база данных

        try {
            // 1. Загрузка драйвера и установка соединения
            Class.forName("org.postgresql.Driver"); // Загрузка драйвера
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("Соединение установлено.");

            // 2. Создание объекта Statement
            Statement stmt = conn.createStatement();
            System.out.println("Создание базы данных: " + dbName);

            // 3. Выполнение команды CREATE DATABASE
            stmt.executeUpdate("CREATE DATABASE " + dbName);
            System.out.println("База данных " + dbName + " успешно создана.");

            // Закрытие ресурсов
            stmt.close();
            conn.close();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.err.println("JDBC-драйвер PostgreSQL не найден.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Ошибка при работе с базой данных.");
        }
    }
}