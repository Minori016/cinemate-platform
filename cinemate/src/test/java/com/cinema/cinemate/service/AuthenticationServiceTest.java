package com.cinema.cinemate.service;

import com.cinema.cinemate.entity.Role;
import com.cinema.cinemate.entity.Staff;
import com.cinema.cinemate.entity.User;
import com.cinema.cinemate.entity.UserRole;
import com.nimbusds.jwt.SignedJWT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @InjectMocks
    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authenticationService, "SIGNER_KEY", "1234567890123456789012345678901234567890123456789012345678901234");
        ReflectionTestUtils.setField(authenticationService, "EXPIRATION_TIME", 3600L);
    }

    @Test
    void generateToken_ShouldIncludeIsFirstLoginClaim_WhenUserIsStaffAndIsFirstLogin() throws ParseException {
        // Arrange
        User user = new User();
        user.setUuid(UUID.randomUUID());
        user.setEmail("staff@example.com");

        Role role = new Role();
        role.setName("STAFF");
        UserRole userRole = new UserRole();
        userRole.setRole(role);
        Set<UserRole> roles = new HashSet<>();
        roles.add(userRole);
        user.setUserRoles(roles);

        Staff staff = new Staff();
        staff.setIsFirstLogin(true);
        user.setStaff(staff);

        // Act
        String token = authenticationService.generateToken(user);

        // Assert
        assertNotNull(token);
        SignedJWT signedJWT = SignedJWT.parse(token);
        assertTrue(signedJWT.getJWTClaimsSet().getBooleanClaim("isFirstLogin"));
    }
}
