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
// This class is a Spring Security component responsible for handling authentication failure during login attempts.
// It customizes error messages based on the type of authentication exception encountered and redirects the user back to the login page with the appropriate error message stored in the session.
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    // This method is called when an authentication attempt fails.
    // It retrieves the error message based on the exception type, stores it in the session,
    // and redirects the user to the login page with an error flag.
    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception)
            throws IOException, ServletException {
        // Declare errorMessage without an initial redundant assignment.
        String errorMessage;

        // Customize error messages based on exception type.
        if (exception instanceof BadCredentialsException) {
            errorMessage = "Incorrect password";
        } else {
            // Use the exception's message for any other type of authentication error.
            errorMessage = exception != null ? exception.getMessage() : "Unknown error";
        }

        // Store the error message in session so the login page can display it.
        request.getSession().setAttribute("errorMessage", errorMessage);

        // Redirect to the login page with an error parameter.
        response.sendRedirect(request.getContextPath() + "/login?error");
    }
}

