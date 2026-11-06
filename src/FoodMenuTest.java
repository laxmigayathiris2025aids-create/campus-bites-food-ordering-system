
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class FoodMenuTest {

    public static void main(String[] args) {

        String query = "SELECT s.shop_name, f.food_name, f.price " +
                       "FROM food_items f " +
                       "JOIN shops s ON f.shop_id = s.shop_id";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("\n======= CAMPUS BITES MENU =======");

            while (rs.next()) {
                System.out.println(
                    rs.getString("shop_name") + " | " +
                    rs.getString("food_name") + " | Rs." +
                    rs.getInt("price")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
