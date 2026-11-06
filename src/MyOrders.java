import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class MyOrders {

    public static String getOrders(String registerNumber) {

        String sql = "SELECT o.order_id, s.shop_name, "
                   + "o.reference_code, o.total_amount, "
                   + "o.order_status, o.order_date "
                   + "FROM orders o "
                   + "JOIN shops s ON o.shop_id = s.shop_id "
                   + "WHERE o.register_number = ? "
                   + "ORDER BY o.order_date DESC";

        StringBuilder result = new StringBuilder();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, registerNumber);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                result.append("Order ID: ")
                      .append(rs.getInt("order_id"))
                      .append("\nShop: ")
                      .append(rs.getString("shop_name"))
                      .append("\nReference: ")
                      .append(rs.getString("reference_code"))
                      .append("\nTotal: Rs.")
                      .append(rs.getInt("total_amount"))
                      .append("\nStatus: ")
                      .append(rs.getString("order_status"))
                      .append("\nDate: ")
                      .append(rs.getTimestamp("order_date"))
                      .append("\n----------------------\n");
            }

            if (result.length() == 0) {
                return "No orders found!";
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Unable to fetch orders!";
        }

        return result.toString();
    }
}
