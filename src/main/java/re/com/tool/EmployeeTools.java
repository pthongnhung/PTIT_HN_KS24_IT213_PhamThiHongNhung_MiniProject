package re.com.tool;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import re.com.dto.EmployeeResponse;
import re.com.entity.Employee;
import re.com.repository.EmployeeRepository;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmployeeTools {

    private final EmployeeRepository employeeRepository;

    @Tool(description = "Tìm thông tin chi tiết của nhân viên theo họ tên. Chỉ sử dụng khi người dùng hỏi thông tin nhân viên.")
    public List<EmployeeResponse> searchEmployee(String fullName) {
        log.info("AI calling searchEmployee with fullName: {}", fullName);
        List<Employee> employees = employeeRepository.findByFullNameContainingIgnoreCase(fullName);
        return employees.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Tool(description = "Tìm danh sách nhân viên thuộc một phòng ban theo tên phòng ban.")
    public List<EmployeeResponse> findEmployeesByDepartment(String departmentName) {
        log.info("AI calling findEmployeesByDepartment with departmentName: {}", departmentName);
        List<Employee> employees = employeeRepository.findByDepartmentNameIgnoreCase(departmentName);
        return employees.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private EmployeeResponse mapToResponse(Employee emp) {
        return EmployeeResponse.builder()
                .id(emp.getId())
                .employeeCode(emp.getEmployeeCode())
                .fullName(emp.getFullName())
                .email(emp.getEmail())
                .phone(emp.getPhone())
                .departmentName(emp.getDepartment() != null ? emp.getDepartment().getName() : null)
                .position(emp.getPosition())
                .salary(emp.getSalary())
                .status(emp.getStatus())
                .build();
    }
}
