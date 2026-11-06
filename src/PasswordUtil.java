import java.security.SecureRandom;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.SecretKeyFactory;
import java.util.Base64;

public class PasswordUtil {

    public static String hashPassword(String password)
            throws Exception {

        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);

        int iterations = 120000;

        PBEKeySpec spec = new PBEKeySpec(
            password.toCharArray(),
            salt,
            iterations,
            256
        );

        SecretKeyFactory factory =
            SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

        byte[] hash = factory.generateSecret(spec).getEncoded();

        spec.clearPassword();

        return iterations + ":" +
            Base64.getEncoder().encodeToString(salt) + ":" +
            Base64.getEncoder().encodeToString(hash);
    }
    public static boolean verifyPassword(String password, String storedHash)
        throws Exception {

    String[] parts = storedHash.split(":");

    int iterations = Integer.parseInt(parts[0]);

    byte[] salt = Base64.getDecoder().decode(parts[1]);

    byte[] originalHash = Base64.getDecoder().decode(parts[2]);

    PBEKeySpec spec = new PBEKeySpec(
        password.toCharArray(),
        salt,
        iterations,
        256
    );

    SecretKeyFactory factory =
        SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

    byte[] newHash = factory.generateSecret(spec).getEncoded();

    spec.clearPassword();

    return java.security.MessageDigest.isEqual(originalHash, newHash);
}
}