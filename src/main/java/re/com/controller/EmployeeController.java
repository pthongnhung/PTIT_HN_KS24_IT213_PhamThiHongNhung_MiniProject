package re.com.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import re.com.dto.EmployeeResponse;
import re.com.entity.Employee;
import re.com.repository.EmployeeRepository;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
public class EmployeeController {

    private final EmployeeRepository employeeRepository;

    @GetMapping
    public ResponseEntity<List<EmployeeResponse>> getAllEmployees() {
        log.info("REST request to get all employees");
        List<Employee> list = employeeRepository.findAll();
        return ResponseEntity.ok(list.stream().map(this::mapToResponse).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponse> getEmployeeById(@PathVariable("id") Long id) {
        log.info("REST request to get employee details by ID: {}", id);
        Employee emp = employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with ID: " + id));
        return ResponseEntity.ok(mapToResponse(emp));
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
