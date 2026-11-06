import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class StudentLogin {

    public static boolean loginStudent(String email, String password) {

        String sql = "SELECT password_hash FROM students WHERE email = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password_hash");

                return PasswordUtil.verifyPassword(password, storedHash);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}