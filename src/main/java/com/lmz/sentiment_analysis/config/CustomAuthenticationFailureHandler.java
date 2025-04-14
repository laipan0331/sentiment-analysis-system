package com.lmz.sentiment_analysis.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception)
            throws IOException, ServletException {
        String errorMessage = "Invalid username or password";

        // Customize error messages based on exception type
        if (exception instanceof BadCredentialsException) {
            errorMessage = "Incorrect password";
        } else {
            // Optionally, you can check for UsernameNotFoundException, etc.
            errorMessage = exception.getMessage();
        }
        // Store the error message in session (or request) so the login page can display it
        request.getSession().setAttribute("errorMessage", errorMessage);
        // Redirect to the login page with an error parameter
        response.sendRedirect(request.getContextPath() + "/login?error");
    }
}

