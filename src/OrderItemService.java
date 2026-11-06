import java.sql.Connection;
import java.sql.PreparedStatement;

public class OrderItemService {

    public static boolean addOrderItem(
            int orderId,
            int foodId,
            int quantity,
            int price) {

        String sql = "INSERT INTO order_items "
                   + "(order_id, food_id, quantity, price) "
                   + "VALUES (?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, orderId);
            ps.setInt(2, foodId);
            ps.setInt(3, quantity);
            ps.setInt(4, price);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}