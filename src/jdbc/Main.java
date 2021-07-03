package jdbc;

import com.sun.rowset.CachedRowSetImpl;

import javax.sql.*;
import javax.sql.rowset.*;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class Main {

    public static void main(String[] args) throws Exception {
        ResultSet resultSet = getData(); // получаем результат
        while (resultSet.next()){
            System.out.println(resultSet.getString("name")); // выводим имя
        }

        /*
        CachedRowSet cachedRowSet = (CachedRowSet) resultSet; // создаем кеш результат
        cachedRowSet.setURL("jdbc:mysql://localhost:3306/Book?relaxAutoCommit=true"); // доступ к бд
        cachedRowSet.setUsername("root");
        cachedRowSet.setPassword("");
        cachedRowSet.setCommand("SELECT * from WHERE id=?"); // выборка
        cachedRowSet.setInt(1,1);
        cachedRowSet.setPageSize(20); // страницы выбора
        cachedRowSet.execute();
        do {
            while (cachedRowSet.next()){
                System.out.println(cachedRowSet.getInt("age")); // выводим возраст
            }
        }while (cachedRowSet.nextPage());

         */

        CachedRowSet cachedRowSet2 = (CachedRowSet) resultSet; // создаем кеш результат
        cachedRowSet2.setTableName("Book"); // наша бд
        cachedRowSet2.absolute(1); // выбраная сточка данных
        cachedRowSet2.deleteRow(); // удаление
        cachedRowSet2.beforeFirst(); // возвращаем каретку на начало
        while (cachedRowSet2.next()){
            System.out.println(cachedRowSet2.getInt("age"));// выводим возраст
        }

    }


    static ResultSet getData() throws Exception {
        //-------------------------------------------------- Подключение к БД
        String url = "jdbc:mysql://localhost:3306/Book"; // наша бд
        String userName = "root"; // доступ
        String password = "";
        Class.forName("com.mysql.cj.jdbc.Driver"); // драйвер соединения
        try (Connection c = DriverManager.getConnection(url, userName, password); // само соединение
             Statement stmt = c.createStatement()) {

            //-------------------------------------------------- Создание таблицы
            stmt.execute("DROP TABLE IF EXISTS Book");
            String sql = "CREATE TABLE IF NOT EXISTS Book " +
                    "(ID INT PRIMARY KEY     NOT NULL," +
                    " NAME           TEXT    NOT NULL, " +
                    " AGE            INT     NOT NULL, " +
                    " ADDRESS        CHAR(50), " +
                    " PRICE         REAL)"; // создание таблицы в sql
            stmt.executeUpdate(sql); // обновить бд
            //-------------------------------------------------- Заполнение
            sql = "INSERT INTO Book (ID,NAME,AGE,ADDRESS,PRICE) " +
                    "VALUES (1, 'OOP', 45, 'USA', 2000.00 );";
            stmt.executeUpdate(sql); // обновление действий по запросу sql, втавка полей в бд / INSERT(вставка)
            sql = "INSERT INTO Book (ID,NAME,AGE,ADDRESS,PRICE) " +
                    "VALUES (2, 'War and pice', 25, 'Ru', 2000.00 );";
            stmt.executeUpdate(sql); // обновление действий по запросу sql, втавка полей в бд / INSERT(вставка)
            sql = "INSERT INTO Book (ID,NAME,AGE,ADDRESS,PRICE) " +
                    "VALUES (3, 'Gold', 35, 'En', 2000.00 );";
            stmt.executeUpdate(sql); // обновление действий по запросу sql, втавка полей в бд / INSERT(вставка)
            sql = "INSERT INTO Book (ID,NAME,AGE,ADDRESS,PRICE) " +
                    "VALUES (4, 'Potato and Father', 17, 'By', 2000.00 );";
            stmt.executeUpdate(sql); // обновление действий по запросу sql, втавка полей в бд / INSERT(вставка)

            // создание закешированной бд
            RowSetFactory factory = RowSetProvider.newFactory();
            CachedRowSet cachedRowSet = factory.createCachedRowSet();

            // возможность пробегаться по результату
            Statement statement = c.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            ResultSet resultSet = statement.executeQuery("SELECT * from Book"); // выборка
            cachedRowSet.populate(resultSet); // сохраняем её в закешированной бд
            return cachedRowSet; // и возвращаем её
        }
    }
}

