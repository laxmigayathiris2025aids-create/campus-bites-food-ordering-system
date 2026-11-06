
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class OrderItemSaveTest {

    public static void main(String[] args) {

        String referenceCode = "REC-12345";
        String foodName = "Burger";
        int quantity = 3;

        try (Connection con = DBConnection.getConnection()) {

            // 1. Get Order ID
            int orderId = 0;

            String orderQuery =
                "SELECT order_id FROM orders WHERE reference_code = ?";

            try (PreparedStatement ps =
                     con.prepareStatement(orderQuery)) {

                ps.setString(1, referenceCode);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        orderId = rs.getInt("order_id");
                    }
                }
            }

            if (orderId == 0) {
                System.out.println("Order not found!");
                return;
            }

            // 2. Get Food ID and Price
            int foodId = 0;
            int price = 0;

            String foodQuery =
                "SELECT food_id, price FROM food_items WHERE food_name = ?";

            try (PreparedStatement ps =
                     con.prepareStatement(foodQuery)) {

                ps.setString(1, foodName);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        foodId = rs.getInt("food_id");
                        price = rs.getInt("price");
                    }
                }
            }

            if (foodId == 0) {
                System.out.println("Food item not found!");
                return;
            }

            // 3. Calculate total
            int total = price * quantity;

            // 4. Insert into order_items
            String insertQuery =
                "INSERT INTO order_items (order_id, food_id, quantity, price) " +
                "VALUES (?, ?, ?, ?)";

            try (PreparedStatement ps =
                     con.prepareStatement(insertQuery)) {

                ps.setInt(1, orderId);
                ps.setInt(2, foodId);
                ps.setInt(3, quantity);
                ps.setInt(4, price);

                ps.executeUpdate();
            }

            System.out.println("Food item saved successfully!");
            System.out.println("Food: " + foodName);
            System.out.println("Quantity: " + quantity);
            System.out.println("Price: Rs." + price);
            System.out.println("Total: Rs." + total);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
