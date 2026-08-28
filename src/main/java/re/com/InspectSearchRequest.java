package re.com;

import org.springframework.ai.vectorstore.SearchRequest;
import java.lang.reflect.Method;

public class InspectSearchRequest {
    public static void main(String[] args) {
        try {
            Class<?> builderClass = Class.forName("org.springframework.ai.vectorstore.SearchRequest$Builder");
            System.out.println("=== BUILDER METHODS ===");
            for (Method m : builderClass.getMethods()) {
                System.out.println(m.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
