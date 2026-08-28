package re.com.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import re.com.dto.DepartmentResponse;
import re.com.entity.Department;
import re.com.repository.DepartmentRepository;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
public class DepartmentController {

    private final DepartmentRepository departmentRepository;

    @GetMapping
    public ResponseEntity<List<DepartmentResponse>> getAllDepartments() {
        log.info("REST request to get all departments");
        List<Department> list = departmentRepository.findAll();
        return ResponseEntity.ok(list.stream().map(this::mapToResponse).collect(Collectors.toList()));
    }

    private DepartmentResponse mapToResponse(Department dept) {
        return DepartmentResponse.builder()
                .id(dept.getId())
                .name(dept.getName())
                .description(dept.getDescription())
                .build();
    }
}
