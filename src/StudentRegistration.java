import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class StudentRegistration {

    public static boolean registerStudent(
            String registerNumber,
            String studentName,
            String email,
            String passwordHash) {

        String sql = "INSERT INTO students "
                   + "(register_number, student_name, email, password_hash) "
                   + "VALUES (?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, registerNumber);
            ps.setString(2, studentName);
            ps.setString(3, email);
            ps.setString(4, passwordHash);

            int result = ps.executeUpdate();

            return result > 0;

        } catch (SQLException e) {
            System.out.println("Registration failed!");
            e.printStackTrace();
            return false;
        }
    }
}
