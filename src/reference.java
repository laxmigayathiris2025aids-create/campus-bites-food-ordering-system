
import java.util.Random;

public class  reference
     {
    String referenceCode;

    void generateReferenceCode() {
        Random r = new Random();
        int code = 10000 + r.nextInt(90000);
        referenceCode = "REC-" + code;
    }

    void displayReferenceCode() {
        System.out.println("Reference Code: " + referenceCode);
    }
}
