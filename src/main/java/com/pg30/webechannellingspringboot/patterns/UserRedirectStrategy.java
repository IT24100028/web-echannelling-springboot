package com.pg30.webechannellingspringboot.patterns;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

public class UserRedirectStrategy {

    public String getRedirectUrl(Authentication authentication) {
        String role = getUserRole(authentication);
        
        switch (role) {
            case "DOCTOR":
                return "/doctor/dashboard";
            case "ADMIN":
                return "/";
            case "PATIENT":
                return "/user/indexMY";
            default:
                return "/";
        }
    }
    
    private String getUserRole(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .map(auth -> auth.replace("ROLE_", ""))
                .orElse("PATIENT");
    }
}
