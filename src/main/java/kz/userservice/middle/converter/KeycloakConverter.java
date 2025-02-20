package kz.userservice.middle.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class KeycloakConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    @Override
    public AbstractAuthenticationToken convert(Jwt source) {
        Collection<GrantedAuthority> authorities = getAuthorities(source);
        return new JwtAuthenticationToken(source, authorities);
    }

    private Collection<GrantedAuthority> getAuthorities(Jwt source) {
        Map<String, Object> claims = source.getClaim("resource_access");
    if(claims == null || claims.isEmpty()){
        return Collections.emptyList();
    }
    Map<String, Object> claim = (Map<String, Object>) claims.get("user-service-client");
        if(claim == null || claim.isEmpty()){
            return Collections.emptyList();
        }
    List<String> roles = (List<String>) claim.get("roles");
    if(roles == null || roles.isEmpty()){
        return Collections.emptyList();
    }
        return roles.stream()
                .map(role -> "ROLE_" + role)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
