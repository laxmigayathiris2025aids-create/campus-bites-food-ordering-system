import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class OrderService {

    public static int createOrder(
            String registerNumber,
            int shopId,
            String referenceCode,
            int totalAmount) {

        String sql = "INSERT INTO orders "
                   + "(register_number, shop_id, reference_code, total_amount) "
                   + "VALUES (?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                 sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, registerNumber);
            ps.setInt(2, shopId);
            ps.setString(3, referenceCode);
            ps.setInt(4, totalAmount);

            int result = ps.executeUpdate();

            if (result > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }
}