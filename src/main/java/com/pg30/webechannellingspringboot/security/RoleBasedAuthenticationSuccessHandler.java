package com.pg30.webechannellingspringboot.security;

import com.pg30.webechannellingspringboot.patterns.UserRedirectStrategy;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.util.logging.Logger;

public class RoleBasedAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        UserRedirectStrategy strategy = new UserRedirectStrategy();
        String redirectUrl = strategy.getRedirectUrl(authentication);
        
        Logger.getLogger("info").info("User logged in, redirecting to: " + redirectUrl);
        response.sendRedirect(redirectUrl);
    }
}


