package demo.service;

import java.util.Collections;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import demo.dto.AdminDto;
import demo.mapper.LoginMapper;


@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User>{

    @Autowired
    private LoginMapper loginMapper;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("Enter CustomOAuth2UserService");

        OAuth2UserService delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        boolean isValid = false;
        AdminDto findAdmin = null;
        String email=null;
        HttpServletRequest req = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
        HttpSession session = req.getSession();

        Map<String, Object> attributes = oAuth2User.getAttributes();
        try {
            if (registrationId.matches("google")){
                String id = (String) attributes.get("sub");
                isValid = (Boolean) attributes.get("email_verified");
                email = (String) attributes.get("email");
                findAdmin = loginMapper.findByGoogleSub(id);
            } else if(registrationId.matches("kakao")){
                long id = (Long) attributes.get("id");
                Map<String, Object> kakao_account = (Map<String, Object>) attributes.get("kakao_account");
                isValid = (Boolean) kakao_account.get("is_email_valid");
                email = (String) kakao_account.get("email");
                findAdmin = loginMapper.findByKakaoId(id);
            } else if(registrationId.matches("naver")){
                String result = (String) attributes.get("message");
                if (result.matches("success")) isValid=true;
                else isValid=false;

                Map<String, Object> response = (Map<String, Object>) attributes.get("response");
                String id = (String) response.get("id");
                email = (String) response.get("email");
                findAdmin = loginMapper.findByNaverId(id);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new OAuth2AuthenticationException("sql error");
        }

        if (!isValid) throw new OAuth2AuthenticationException("Email is not valid. Please verify your email in google.");
        else if (findAdmin != null){
            // myAttributes.put("admin_id", findAdmin.getId());
            session.setAttribute("member_id", findAdmin.getId());

            return new DefaultOAuth2User(
            Collections.singleton(new SimpleGrantedAuthority("ROLE_ADMIN")),
            attributes, 
            userNameAttributeName);
        } else {
            try {
                findAdmin = loginMapper.findSameEmailSelect(email);
                session.setAttribute("member_id", findAdmin.getId());
            } catch (Exception e) {
                session.setAttribute("OAuth2UserRequest", userRequest);
                e.printStackTrace();
                throw new OAuth2AuthenticationException("sql error");
            }
            session.setAttribute("OAuth2UserRequest", userRequest);

            throw new OAuth2AuthenticationException("not member");
        }
    }
}
