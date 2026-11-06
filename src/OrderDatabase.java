
import java.sql.*;
import java.util.ArrayList;

public class OrderDatabase {

    public static boolean saveOrder(
            String studentName,
            String registerNumber,
            String shopName,
            String referenceCode,
            int total,
            ArrayList<fooditem> cart,
            ArrayList<Integer> quantity) {

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {
                return false;
            }

            con.setAutoCommit(false);

            try {
                // 1. Save student details
                String studentSQL =
                    "INSERT INTO students (register_number, student_name) " +
                    "VALUES (?, ?) ON DUPLICATE KEY UPDATE " +
                    "student_name = VALUES(student_name)";

                try (PreparedStatement ps =
                         con.prepareStatement(studentSQL)) {
                    ps.setString(1, registerNumber);
                    ps.setString(2, studentName);
                    ps.executeUpdate();
                }

                // 2. Get shop ID
                int shopId = 0;

                String shopSQL =
                    "SELECT shop_id FROM shops WHERE shop_name = ?";

                try (PreparedStatement ps =
                         con.prepareStatement(shopSQL)) {
                    ps.setString(1, shopName);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            shopId = rs.getInt("shop_id");
                        }
                    }
                }

                if (shopId == 0) {
                    throw new SQLException("Shop not found!");
                }

                // 3. Insert order
                String orderSQL =
                    "INSERT INTO orders " +
                    "(register_number, shop_id, reference_code, total_amount) " +
                    "VALUES (?, ?, ?, ?)";

                int orderId;

                try (PreparedStatement ps = con.prepareStatement(
                        orderSQL, Statement.RETURN_GENERATED_KEYS)) {

                    ps.setString(1, registerNumber);
                    ps.setInt(2, shopId);
                    ps.setString(3, referenceCode);
                    ps.setInt(4, total);

                    ps.executeUpdate();

                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (!rs.next()) {
                            throw new SQLException("Order ID not generated!");
                        }
                        orderId = rs.getInt(1);
                    }
                }

                // 4. Insert cart items
                String foodSQL =
                    "SELECT food_id FROM food_items " +
                    "WHERE food_name = ? AND shop_id = ?";

                String itemSQL =
                    "INSERT INTO order_items " +
                    "(order_id, food_id, quantity, price) " +
                    "VALUES (?, ?, ?, ?)";

                try (PreparedStatement foodPS =
                         con.prepareStatement(foodSQL);
                     PreparedStatement itemPS =
                         con.prepareStatement(itemSQL)) {

                    for (int i = 0; i < cart.size(); i++) {

                        fooditem item = cart.get(i);

                        foodPS.setString(1, item.itemname);
                        foodPS.setInt(2, shopId);

                        int foodId = 0;

                        try (ResultSet rs = foodPS.executeQuery()) {
                            if (rs.next()) {
                                foodId = rs.getInt("food_id");
                            }
                        }

                        if (foodId == 0) {
                            throw new SQLException(
                                "Food not found: " + item.itemname);
                        }

                        itemPS.setInt(1, orderId);
                        itemPS.setInt(2, foodId);
                        itemPS.setInt(3, quantity.get(i));
                        itemPS.setInt(4, item.itemprice);

                        itemPS.executeUpdate();
                    }
                }

                // 5. Save all changes
                con.commit();

                System.out.println(
                    "\nOrder and food details saved to database!");

                return true;

            } catch (Exception e) {
                con.rollback();
                throw e;
            }

        } catch (Exception e) {
            System.out.println("Order saving failed!");
            e.printStackTrace();
            return false;
        }
    }
}
