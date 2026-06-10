package com.cinema.cinemate.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.cinema.cinemate.entity.User;
import com.cinema.cinemate.exception.AppException;
import com.cinema.cinemate.enums.ErrorCode;
import com.cinema.cinemate.repository.UserRepository;
import com.cinema.cinemate.request.IntrospectRequest;
import com.cinema.cinemate.response.IntrospectResponse;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    @NonFinal
    @Value("${jwt.signer-key}")
    protected String SIGNER_KEY;

    @NonFinal
    @Value("${jwt.expiration}")
    protected long EXPIRATION_TIME;

    @NonFinal
    @Value("${jwt.reset-token-expiration:900}")
    protected long RESET_TOKEN_EXPIRATION;

    private final UserRepository userRepository;

    /**
     * Generate JWT token for authenticated user
     */
    public String generateToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        List<String> roles = user.getUserRoles().stream()
                .map(ur -> ur.getRole().getName())
                .collect(Collectors.toList());

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getEmail())
                .issuer("cinemate")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(EXPIRATION_TIME, ChronoUnit.SECONDS).toEpochMilli()))
                .claim("userId", user.getUuid().toString())
                .claim("roles", roles)
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new AppException(ErrorCode.TOKEN_CREATION_FAILED);
        }
    }

    /**
     * Introspect token to check if it's valid
     */
    public IntrospectResponse introspect(IntrospectRequest request) {
        String token = request.getToken();
        boolean isValid = true;

        try {
            verifyToken(token);
        } catch (AppException | ParseException | JOSEException e) {
            isValid = false;
        }

        return IntrospectResponse.builder()
                .valid(isValid)
                .build();
    }

    /**
     * Verify token signature and expiration
     */
    private void verifyToken(String token) throws ParseException, JOSEException {
        SignedJWT signedJWT = SignedJWT.parse(token);

        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
        boolean verified = signedJWT.verify(verifier);

        if (!verified) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }

        Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        if (expirationTime.before(new Date())) {
            throw new AppException(ErrorCode.TOKEN_EXPIRED);
        }
    }

    /**
     * Tạo JWT token dùng để reset password.
     * Token có thời hạn ngắn (mặc định 15 phút).
     */
    public String generatePasswordResetToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getEmail())
                .issuer("cinemate")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(RESET_TOKEN_EXPIRATION, ChronoUnit.SECONDS).toEpochMilli()))
                .claim("userId", user.getUuid().toString())
                .claim("type", "PASSWORD_RESET")
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new AppException(ErrorCode.TOKEN_CREATION_FAILED);
        }
    }

    /**
     * Verify password reset token.
     * Kiểm tra chữ ký, thời hạn, và trả về User sở hữu token.
     */
    public User verifyPasswordResetToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);

            JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
            boolean verified = signedJWT.verify(verifier);

            if (!verified) {
                throw new AppException(ErrorCode.RESET_TOKEN_INVALID);
            }

            Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
            if (expirationTime.before(new Date())) {
                throw new AppException(ErrorCode.RESET_TOKEN_EXPIRED);
            }

            String email = signedJWT.getJWTClaimsSet().getSubject();
            return userRepository.findByEmail(email)
                    .orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_FOUND));
        } catch (ParseException e) {
            throw new AppException(ErrorCode.RESET_TOKEN_INVALID);
        } catch (JOSEException e) {
            throw new AppException(ErrorCode.RESET_TOKEN_INVALID);
        }
    }
}
