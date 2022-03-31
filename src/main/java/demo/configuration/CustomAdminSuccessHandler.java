package demo.configuration;

import java.io.IOException;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

public class CustomAdminSuccessHandler implements AuthenticationSuccessHandler {
    private RequestCache requestCache = new HttpSessionRequestCache();
    private RedirectStrategy redirectStratgy = new DefaultRedirectStrategy();
    private final String DEFAULT_LOGIN_SUCCESS_URL = "/";
    
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        
        clearAuthenticationAttributes(request, authentication);

        redirectStratgy(request, response, authentication);
    }

    private void clearAuthenticationAttributes(HttpServletRequest request, Authentication authentication) {
        // HttpSession session = request.getSession(false); 

        HttpSession session = request.getSession(true);

        if(session != null) session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);

        User user = (User) authentication.getPrincipal();
		String admin = user.getUsername();
		session.setAttribute("admin_id", admin);
    }

    private void redirectStratgy(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        SavedRequest savedRequest = requestCache.getRequest(request, response);
        if(savedRequest == null) redirectStratgy.sendRedirect(request, response, DEFAULT_LOGIN_SUCCESS_URL);
        else {
            Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
            System.out.println(roles);
            if (roles.contains("ROLE_ADMIN")) redirectStratgy.sendRedirect(request, response, "/");
            // else if (roles.contains("ROLE_CLIENT")) redirectStratgy.sendRedirect(request, response, "/client/");
        }
        
    }
    
}
