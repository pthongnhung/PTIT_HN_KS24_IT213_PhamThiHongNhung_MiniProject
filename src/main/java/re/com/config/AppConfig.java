package re.com.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import re.com.tool.EmployeeTools;
import re.com.tool.DepartmentTools;
import re.com.tool.LeaveTools;

@Configuration
public class AppConfig {

    @Bean
    public ChatClient chatClient(
            ChatClient.Builder builder,
            EmployeeTools employeeTools,
            DepartmentTools departmentTools,
            LeaveTools leaveTools) {
        
        return builder
                .defaultTools(
                        employeeTools,
                        departmentTools,
                        leaveTools
                )
                .build();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:3000", "http://localhost:5173")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}
