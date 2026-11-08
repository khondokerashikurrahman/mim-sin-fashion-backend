package com.mimsinfashion.common.security.jwt;

import com.mimsinfashion.modules.access.domain.entity.Permission;
import com.mimsinfashion.modules.access.domain.entity.Role;
import com.mimsinfashion.modules.access.domain.entity.User;

import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Service
public class JwtTokenService {

    private final JwtEncoder jwtEncoder;
    private final JwtProperties jwtProperties;

    public JwtTokenService(
            JwtEncoder jwtEncoder,
            JwtProperties jwtProperties) {
        this.jwtEncoder = jwtEncoder;
        this.jwtProperties = jwtProperties;
    }

    @Transactional(readOnly = true)
    public String generateToken(User user) {
        Instant now = Instant.now();

        Set<String> authorities = new LinkedHashSet<>();

        for (Role role : user.getRoles()) {
            if (!role.isActive()) {
                continue;
            }

            authorities.add("ROLE_" + role.getCode());

            for (Permission permission : role.getPermissions()) {
                if (permission.isActive()) {
                    authorities.add(permission.getCode());
                }
            }
        }

        JwsHeader header = JwsHeader
            .with(MacAlgorithm.HS256)
            .build();

        JwtClaimsSet claims = JwtClaimsSet.builder()
            .subject(user.getId().toString())
            .issuedAt(now)
            .expiresAt(now.plusSeconds(
                jwtProperties.getExpirationMinutes() * 60
            ))
            .claim("email", user.getEmail())
            .claim("authorities", authorities)
            .build();

        return jwtEncoder.encode(
            JwtEncoderParameters.from(header, claims)
        ).getTokenValue();
    }
}
