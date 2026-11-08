package com.mimsinfashion.modules.access.application.service;

import com.mimsinfashion.common.security.jwt.JwtProperties;
import com.mimsinfashion.common.security.jwt.JwtTokenService;
import com.mimsinfashion.modules.access.api.dto.LoginRequest;
import com.mimsinfashion.modules.access.api.dto.LoginResponse;
import com.mimsinfashion.modules.access.domain.entity.Permission;
import com.mimsinfashion.modules.access.domain.entity.Role;
import com.mimsinfashion.modules.access.domain.entity.User;
import com.mimsinfashion.modules.access.infrastructure.persistence.UserJpaRepository;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthenticationService {

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final long LOCK_DURATION_MINUTES = 15;

    private final UserJpaRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;
    private final JwtProperties jwtProperties;

    public AuthenticationService(
            UserJpaRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenService jwtTokenService,
            JwtProperties jwtProperties) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
        this.jwtProperties = jwtProperties;
    }

    @Transactional(noRollbackFor = BadCredentialsException.class)
    public LoginResponse login(LoginRequest request) {
        String normalizedEmail = request.email()
            .trim()
            .toLowerCase(Locale.ROOT);

        User user = userRepository.findByEmail(normalizedEmail)
            .orElseThrow(this::invalidCredentials);

        Instant now = Instant.now();

        if (!user.isActive()) {
            throw invalidCredentials();
        }

        if (isLocked(user, now)) {
            throw invalidCredentials();
        }

        if (!passwordEncoder.matches(
                request.password(),
                user.getPasswordHash())) {
            registerFailedAttempt(user, now);
            throw invalidCredentials();
        }

        user.setFailedLoginAttempts(0);
        user.setAccountNonLocked(true);
        user.setLockedUntil(null);

        String token = jwtTokenService.generateToken(user);

        Set<String> roles = new LinkedHashSet<>();
        Set<String> permissions = new LinkedHashSet<>();

        for (Role role : user.getRoles()) {
            if (!role.isActive()) {
                continue;
            }

            roles.add(role.getCode());

            for (Permission permission : role.getPermissions()) {
                if (permission.isActive()) {
                    permissions.add(permission.getCode());
                }
            }
        }

        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo(
            user.getId(),
            user.getEmail(),
            user.getFirstName(),
            user.getLastName(),
            Set.copyOf(roles),
            Set.copyOf(permissions)
        );

        return new LoginResponse(
            token,
            "Bearer",
            jwtProperties.getExpirationMinutes() * 60,
            userInfo
        );
    }

    private boolean isLocked(User user, Instant now) {
        if (user.isAccountNonLocked()) {
            return false;
        }

        Instant lockedUntil = user.getLockedUntil();

        if (lockedUntil == null || lockedUntil.isAfter(now)) {
            return true;
        }

        user.setAccountNonLocked(true);
        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);

        return false;
    }

    private void registerFailedAttempt(User user, Instant now) {
        int attempts = user.getFailedLoginAttempts() + 1;
        user.setFailedLoginAttempts(attempts);

        if (attempts >= MAX_FAILED_ATTEMPTS) {
            user.setAccountNonLocked(false);
            user.setLockedUntil(
                now.plusSeconds(LOCK_DURATION_MINUTES * 60)
            );
        }
    }

    private BadCredentialsException invalidCredentials() {
        return new BadCredentialsException(
            "Invalid email or password"
        );
    }
}
