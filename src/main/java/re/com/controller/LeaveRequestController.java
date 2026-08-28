package re.com.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import re.com.dto.LeaveRequestResponse;
import re.com.entity.LeaveRequest;
import re.com.repository.LeaveRequestRepository;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/leave-requests")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
public class LeaveRequestController {

    private final LeaveRequestRepository leaveRequestRepository;

    @GetMapping
    public ResponseEntity<List<LeaveRequestResponse>> getAllLeaveRequests() {
        log.info("REST request to get all leave requests");
        List<LeaveRequest> list = leaveRequestRepository.findAll();
        return ResponseEntity.ok(list.stream().map(this::mapToResponse).collect(Collectors.toList()));
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
