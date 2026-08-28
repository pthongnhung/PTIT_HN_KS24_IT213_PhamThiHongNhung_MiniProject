package re.com.tool;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import re.com.dto.DepartmentResponse;
import re.com.entity.Department;
import re.com.repository.DepartmentRepository;
import re.com.repository.EmployeeRepository;

@Component
@RequiredArgsConstructor
@Slf4j
public class DepartmentTools {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    @Tool(description = "Tìm thông tin phòng ban theo tên phòng ban (ví dụ: IT, HR, Marketing, Accounting).")
    public DepartmentResponse searchDepartment(String name) {
        log.info("AI calling searchDepartment with name: {}", name);
        Department dept = departmentRepository.findByNameIgnoreCase(name)
                .orElse(null);
        if (dept == null) {
            return null;
        }
        return DepartmentResponse.builder()
                .id(dept.getId())
                .name(dept.getName())
                .description(dept.getDescription())
                .build();
    }

    @Tool(description = "Đếm số lượng nhân viên trong một phòng ban theo tên phòng ban.")
    public long countEmployeesByDepartment(String departmentName) {
        log.info("AI calling countEmployeesByDepartment with departmentName: {}", departmentName);
        return employeeRepository.findByDepartmentNameIgnoreCase(departmentName).size();
    }
}
