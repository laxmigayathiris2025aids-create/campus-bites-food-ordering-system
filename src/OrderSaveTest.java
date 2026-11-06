
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class OrderSaveTest {

    public static void main(String[] args) {

        String registerNumber = "REC001";
        String studentName = "Student One";
        String shopName = "REC_MART";
        String referenceCode = "REC-12345";

        int totalAmount = 150;

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {
                System.out.println("Database connection failed!");
                return;
            }

            // 1. Insert student if not already present
            String studentQuery =
                "INSERT INTO students (register_number, student_name) " +
                "VALUES (?, ?) " +
                "ON DUPLICATE KEY UPDATE student_name = VALUES(student_name)";

            try (PreparedStatement ps =
                     con.prepareStatement(studentQuery)) {

                ps.setString(1, registerNumber);
                ps.setString(2, studentName);
                ps.executeUpdate();
            }

            // 2. Get shop ID
            int shopId = 0;

            String shopQuery =
                "SELECT shop_id FROM shops WHERE shop_name = ?";

            try (PreparedStatement ps =
                     con.prepareStatement(shopQuery)) {

                ps.setString(1, shopName);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        shopId = rs.getInt("shop_id");
                    }
                }
            }

            if (shopId == 0) {
                System.out.println("Shop not found!");
                return;
            }

            // 3. Save order
            String orderQuery =
                "INSERT INTO orders " +
                "(register_number, shop_id, reference_code, total_amount) " +
                "VALUES (?, ?, ?, ?)";

            int orderId;

            try (PreparedStatement ps = con.prepareStatement(
                    orderQuery, Statement.RETURN_GENERATED_KEYS)) {

                ps.setString(1, registerNumber);
                ps.setInt(2, shopId);
                ps.setString(3, referenceCode);
                ps.setInt(4, totalAmount);

                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    rs.next();
                    orderId = rs.getInt(1);
                }
            }

            System.out.println("Order saved successfully!");
            System.out.println("Order ID: " + orderId);
            System.out.println("Reference: " + referenceCode);
            System.out.println("Total: Rs." + totalAmount);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
