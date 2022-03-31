package demo.configuration;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

public class OAuthSuccessHandler implements AuthenticationSuccessHandler {
    // private RequestCache requestCache = new  ();
    private RedirectStrategy redirectStratgy = new DefaultRedirectStrategy();
    // private final String DEFAULT_LOGIN_SUCCESS_URL = "/";
    
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        System.out.println("Enter onAuthenticationSuccess");

        String targetUrl = "/";

        clearAuthenticationAttributes(request, authentication, targetUrl);

        boolean signUpOrNo = (Boolean) request.getSession().getAttribute("signUpOrNo");
        if (signUpOrNo) targetUrl += "?Type=signup";

        redirectStratgy.sendRedirect(request, response, targetUrl);
    }

    private void clearAuthenticationAttributes(HttpServletRequest request, Authentication authentication, String targetUrl) {
        // HttpSession session = request.getSession(false); 

        HttpSession session = request.getSession(true);

        if(session != null) session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);

        String member_id = (String) session.getAttribute("member_id");
        session.removeAttribute("member_id");
        session.setAttribute("admin_id", member_id);
    }
    
}
