package com.mimsinfashion.modules.access.application.service;

import com.mimsinfashion.modules.access.domain.entity.Role;
import com.mimsinfashion.modules.access.domain.entity.User;
import com.mimsinfashion.modules.access.infrastructure.persistence.RoleJpaRepository;
import com.mimsinfashion.modules.access.infrastructure.persistence.UserJpaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

@Component
public class SuperAdminBootstrap {

    private final UserJpaRepository userRepository;
    private final RoleJpaRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.bootstrap.super-admin.enabled:false}")
    private boolean enabled;

    @Value("${app.bootstrap.super-admin.email:}")
    private String email;

    @Value("${app.bootstrap.super-admin.password:}")
    private String password;

    @Value("${app.bootstrap.super-admin.first-name:Super}")
    private String firstName;

    @Value("${app.bootstrap.super-admin.last-name:Admin}")
    private String lastName;

    public SuperAdminBootstrap(
            UserJpaRepository userRepository,
            RoleJpaRepository roleRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void createSuperAdminIfEnabled() {
        if (!enabled) {
            return;
        }

        String normalizedEmail = email == null
                ? ""
                : email.trim().toLowerCase(Locale.ROOT);

        if (normalizedEmail.isBlank() || password == null || password.isBlank()) {
            throw new IllegalStateException(
                    "Super Admin bootstrap is enabled, but email or password is missing."
            );
        }

        if (password.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new IllegalStateException(
                    "Super Admin bootstrap password must not exceed 72 UTF-8 bytes."
            );
        }

        Role superAdminRole = roleRepository.findByCode("SUPER_ADMIN")
                .orElseThrow(() -> new IllegalStateException(
                        "SUPER_ADMIN role not found. Check RolePermissionBootstrap."
                ));

        var existingUser = userRepository.findByEmail(normalizedEmail);

        if (existingUser.isPresent()) {
            User user = existingUser.get();

            boolean alreadySuperAdmin = user.getRoles().stream()
                    .anyMatch(role -> "SUPER_ADMIN".equals(role.getCode()));

            if (alreadySuperAdmin) {
                System.out.println("Super Admin bootstrap: account already exists.");
                return;
            }

            throw new IllegalStateException(
                    "Super Admin bootstrap refused: the configured email belongs to an existing non-Super-Admin account."
            );
        }

        User superAdmin = User.create(
                normalizedEmail,
                passwordEncoder.encode(password),
                firstName,
                lastName
        );

        superAdmin.getRoles().add(superAdminRole);
        userRepository.save(superAdmin);

        System.out.println("Super Admin bootstrap: account created successfully.");
    }
}
