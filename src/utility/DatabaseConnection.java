package utility;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    public static final String DATABASE_URL = "jdbc:postgresql://localhost:5432/FashionFrost";
    public static final String DATABASE_USERNAME = "postgres";
    public static final String DATABASE_PASSWORD = "muD9YcDz";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD);
    }
}
