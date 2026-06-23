package com.cinema.cinemate.controller.admin;

import com.cinema.cinemate.request.AddEmployeeRequest;
import com.cinema.cinemate.response.ApiResponse;
import com.cinema.cinemate.response.PageResponse;
import com.cinema.cinemate.response.UserResponse;
import com.cinema.cinemate.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controller xử lý nghiệp vụ Employee cho Admin/Manager.
 *
 * AC-06: Chỉ user có role ADMIN hoặc MANAGER mới được truy cập các endpoint trong controller này.
 */
@RestController
@RequestMapping("/api/v1/admin/employees")
@RequiredArgsConstructor
public class AdminEmployeeController {

    private final UserService userService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ApiResponse<UserResponse> addEmployee(@RequestBody @Valid AddEmployeeRequest request) {
        UserResponse employee = userService.createEmployee(request);
        return ApiResponse.<UserResponse>builder()
                .result(employee)
                .build();
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ApiResponse<PageResponse<UserResponse>> getEmployeeList(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(required = false) String role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        PageResponse<UserResponse> result = userService.searchEmployees(search, role, pageable);
        return ApiResponse.<PageResponse<UserResponse>>builder()
                .result(result)
                .build();
    }

    @GetMapping("/{employeeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ApiResponse<UserResponse> getEmployee(@PathVariable UUID employeeId) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getUserById(employeeId))
                .build();
    }

    @DeleteMapping("/{employeeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ApiResponse<String> deleteEmployee(@PathVariable UUID employeeId) {
        userService.deleteEmployee(employeeId);
        return ApiResponse.<String>builder()
                .message("Employee has been deleted successfully.")
                .build();
    }
}
