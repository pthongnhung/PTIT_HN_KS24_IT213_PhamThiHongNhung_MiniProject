package re.com.tool;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import re.com.dto.LeaveRequestResponse;
import re.com.entity.LeaveRequest;
import re.com.repository.LeaveRequestRepository;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class LeaveTools {

    private final LeaveRequestRepository leaveRequestRepository;

    @Tool(description = "Tìm danh sách các đơn nghỉ phép của một nhân viên dựa trên họ tên nhân viên.")
    public List<LeaveRequestResponse> findLeaveRequests(String employeeName) {
        log.info("AI calling findLeaveRequests with employeeName: {}", employeeName);
        List<LeaveRequest> requests = leaveRequestRepository.findByEmployeeFullNameContainingIgnoreCase(employeeName);
        return requests.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private LeaveRequestResponse mapToResponse(LeaveRequest lr) {
        return LeaveRequestResponse.builder()
                .id(lr.getId())
                .employeeCode(lr.getEmployee() != null ? lr.getEmployee().getEmployeeCode() : null)
                .employeeName(lr.getEmployee() != null ? lr.getEmployee().getFullName() : null)
                .startDate(lr.getStartDate())
                .endDate(lr.getEndDate())
                .reason(lr.getReason())
                .status(lr.getStatus())
                .build();
    }
}
