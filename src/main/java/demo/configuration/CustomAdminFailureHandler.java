package demo.configuration;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

public class CustomAdminFailureHandler  implements AuthenticationFailureHandler{
    @Autowired
    MessageSource messageSource;

    private final String DEFAULT_FAILURE_URL = "?error=true&Type=";

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {
        String errorType = null;
        
        if (exception instanceof BadCredentialsException || exception instanceof InternalAuthenticationServiceException) {
            errorType = "BadCredentialsException";
        } else if (exception instanceof DisabledException) {
            errorType = "DisabledException";
        } else if (exception instanceof CredentialsExpiredException) {
            errorType = "CredentialsExpiredException";
        } else {
            errorType = "UnknownReson";
        }

        response.sendRedirect(request.getRequestURI() + DEFAULT_FAILURE_URL + errorType);
    }
    
}
