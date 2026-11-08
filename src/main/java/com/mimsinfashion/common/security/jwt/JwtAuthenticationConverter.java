package com.mimsinfashion.common.security.jwt;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
public class JwtAuthenticationConverter
        implements Converter<Jwt, AbstractAuthenticationToken> {

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        List<String> authorities = jwt.getClaimAsStringList("authorities");

        Collection<SimpleGrantedAuthority> grantedAuthorities =
            authorities == null
                ? List.of()
                : authorities.stream()
                    .map(SimpleGrantedAuthority::new)
                    .toList();

        return new JwtAuthenticationToken(
            jwt,
            grantedAuthorities,
            jwt.getSubject()
        );
    }
}
