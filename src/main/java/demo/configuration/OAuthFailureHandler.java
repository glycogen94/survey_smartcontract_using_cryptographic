package demo.configuration;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Component
public class OAuthFailureHandler  implements AuthenticationFailureHandler{

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {

        System.out.println("Enter OAuthFailureHandler");

        
        HttpSession session = request.getSession(true);


        String existing_id = (String) session.getAttribute("member_id");

        if (existing_id != null) response.sendRedirect("/login/OAuth/linkMember");
        else response.sendRedirect("/login/OAuth/create");
    

    }
    
}
