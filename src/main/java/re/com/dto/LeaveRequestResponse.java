package re.com.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveRequestResponse {
    private Long id;
    private String employeeCode;
    private String employeeName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;
    private String status;
}
