package kz.userservice.middle.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@Slf4j
public class UserUtil {
    public static Jwt getCurrentJwtUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth instanceof JwtAuthenticationToken){
            return ((JwtAuthenticationToken) auth).getToken();
        }
        return null;
    }
    public static String getCurrentUsername() {
        Jwt jwt = getCurrentJwtUser();
        if(jwt != null){
            return jwt.getClaim("preferred_username");
        }
        return "error";
    }
}
