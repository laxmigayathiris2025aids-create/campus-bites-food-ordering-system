
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    public static Connection getConnection() {

        String url = "jdbc:mysql://localhost:3306/campus_bites";
        String username = "root";
        String password = "Laxmi@2008";

        try {
            Connection con = DriverManager.getConnection(
                url, username, password
            );

            System.out.println("MySQL Connected Successfully!");

            return con;

        } catch (SQLException e) {

            
            System.out.println("Database Connection Failed!");
            e.printStackTrace();

            return null;
        }
    }

    public static void main(String[] args) {
        getConnection();
    }
}
