package re.com;

import org.springframework.ai.vectorstore.SearchRequest;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class InspectSearchRequest {
    public static void main(String[] args) {
        System.out.println("=== CONSTRUCTORS ===");
        for (Constructor<?> c : SearchRequest.class.getConstructors()) {
            System.out.println(c.toString());
        }
        System.out.println("=== METHODS ===");
        for (Method m : SearchRequest.class.getMethods()) {
            if (m.getName().contains("query") || m.getName().contains("builder") || m.getName().contains("with") || m.getName().contains("defaults")) {
                System.out.println(m.toString());
            }
        }
    }
}
