package com.example.studentportal.service;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.logging.Logger;

@Component
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger logger = Logger.getLogger(CustomLoginSuccessHandler.class.getName());

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        String redirectUrl = null;

        for (GrantedAuthority authority : authorities) {
            String role = authority.getAuthority();

            if ("ROLE_ADMIN".equals(role)) {
                redirectUrl = "/admin/dashboard";
                break;
            } else if ("ROLE_STUDENT".equals(role)) {
                redirectUrl = "/student/dashboard";
                break;
            }
        }

        if (redirectUrl == null) {
            logger.warning("No known role found for user: " + authentication.getName());
            redirectUrl = "/dashboard"; // fallback route
        }

        logger.info("Redirecting '" + authentication.getName() + "' to " + redirectUrl);
        response.sendRedirect(redirectUrl);
    }
}
