package com.cinema.cinemate.service;

import com.cinema.cinemate.request.UserLoginRequest;
import com.cinema.cinemate.request.UserRegisterRequest;
import com.cinema.cinemate.response.UserResponse;
import com.cinema.cinemate.entity.User;
import com.cinema.cinemate.entity.Role;
import com.cinema.cinemate.entity.UserRole;
import com.cinema.cinemate.repository.UserRepository;
import com.cinema.cinemate.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.cinema.cinemate.enums.ErrorCode;
import com.cinema.cinemate.exception.AppException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service xử lý nghiệp vụ liên quan đến User:
 * - Đăng ký tài khoản mới (AC-01 → AC-06 của User Story Registration)
 * - Xác thực đăng nhập (AC-01 → AC-03 của User Story Login)
 * - CRUD user (cho Admin)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    // ========================
    // REGISTRATION (User Story 1)
    // ========================

    /**
     * Đăng ký tài khoản mới.
     *
     * Luồng xử lý:
     * 1. Kiểm tra password và confirmPassword có khớp không (AC-03)
     * 2. Kiểm tra email đã tồn tại chưa
     * 3. Hash password trước khi lưu (AC-06)
     * 4. Gán role mặc định MEMBER cho user mới
     * 5. Log sự kiện đăng ký kèm metadata (AC-05)
     *
     * @param request DTO chứa thông tin đăng ký từ client
     * @return UserResponse chứa thông tin user đã tạo (không bao gồm password)
     * @throws AppException nếu password không khớp hoặc email đã tồn tại
     */
    public UserResponse createUser(UserRegisterRequest request) {
        // AC-03: Kiểm tra Password và Confirm Password phải khớp nhau
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new AppException(ErrorCode.PASSWORD_MISMATCH);
        }

        // Kiểm tra email đã tồn tại trong hệ thống chưa
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        // Lấy role MEMBER mặc định (tạo mới nếu chưa có)
        Role memberRole = roleRepository.findByName("MEMBER")
                .orElseGet(() -> roleRepository.save(
                        Role.builder()
                                .name("MEMBER")
                                .description("Default Member Role")
                                .build()
                ));

        // Tạo entity User từ request
        User user = new User();
        user.setEmail(request.getEmail().trim());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // AC-06: Hash password
        user.setFullName(request.getFullName().trim());
        user.setUsername(request.getUsername().trim());
        user.setDayOfBirth(request.getDayOfBirth());
        user.setGender(request.getGender().trim());
        user.setIdentityCard(request.getIdentityCard().trim());
        user.setPhoneNumber(request.getPhoneNumber().trim());
        user.setAddress(request.getAddress().trim());
        user.setScore(0);
        user.setStatus("ACTIVE");

        // Gán role MEMBER cho user mới
        UserRole userRole = UserRole.builder()
                .user(user)
                .role(memberRole)
                .build();
        user.getUserRoles().add(userRole);

        // Lưu user vào database
        User savedUser = userRepository.save(user);

        // AC-05: Log sự kiện đăng ký kèm metadata (timestamp, account name, status)
        log.info("REGISTRATION_EVENT | account={} | email={} | status=SUCCESS | timestamp={}",
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getCreatedAt());

        return toUserResponse(savedUser);
    }

    // ========================
    // LOGIN / AUTHENTICATION (User Story 2)
    // ========================

    /**
     * Xác thực đăng nhập bằng email + password.
     *
     * Luồng xử lý:
     * 1. Tìm user theo email — nếu không tìm thấy → lỗi (AC-02)
     * 2. So sánh password đã hash — nếu sai → lỗi (AC-02)
     * 3. Kiểm tra trạng thái tài khoản — nếu LOCKED → lỗi (AC-03)
     *
     * Lưu ý bảo mật: Khi email không tồn tại hoặc password sai,
     * đều trả cùng 1 message "Email/password is invalid" để tránh lộ thông tin.
     *
     * @param request DTO chứa email và password
     * @return User entity nếu xác thực thành công
     * @throws AppException nếu sai credentials hoặc tài khoản bị khóa
     */
    public User authenticate(UserLoginRequest request) {
        // AC-02: Tìm user theo email — không tìm thấy thì trả lỗi chung
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));

        // AC-02: Kiểm tra password — sai thì trả lỗi chung (cùng message để bảo mật)
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        // AC-03: Kiểm tra tài khoản có bị khóa không
        if ("LOCKED".equals(user.getStatus())) {
            throw new AppException(ErrorCode.ACCOUNT_LOCKED);
        }

        return user;
    }

    // ========================
    // CRUD OPERATIONS (Admin)
    // ========================

    /** Lấy danh sách tất cả user */
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toUserResponse)
                .collect(Collectors.toList());
    }

    /** Lấy thông tin user theo UUID */
    public UserResponse getUserById(UUID id) {
        return toUserResponse(userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)));
    }

    /** Lấy thông tin user theo email */
    public UserResponse getUserByEmail(String email) {
        return toUserResponse(userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)));
    }

    /** Xóa user theo UUID */
    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        userRepository.deleteById(id);
    }

    /**
     * Lấy User entity theo UUID (dùng nội bộ giữa các service)
     */
    public User getUserEntityById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }

    /**
     * Lấy User entity theo email (dùng cho JWT-based controller)
     */
    public User getUserEntityByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }

    // ========================
    // HELPER / MAPPER
    // ========================

    /**
     * Chuyển đổi User entity → UserResponse DTO.
     * Map roles từ Set<UserRole> thành Set<String> (tên role).
     */
    private UserResponse toUserResponse(User user) {
        Set<String> roles = user.getUserRoles().stream()
                .map(ur -> ur.getRole().getName())
                .collect(Collectors.toSet());

        return UserResponse.builder()
                .uuid(user.getUuid())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .username(user.getUsername())
                .dayOfBirth(user.getDayOfBirth())
                .gender(user.getGender())
                .identityCard(user.getIdentityCard())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
                .image(user.getImage())
                .score(user.getScore())
                .status(user.getStatus())
                .roles(roles)
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
