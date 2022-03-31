package demo.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.firewall.DefaultHttpFirewall;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.thymeleaf.extras.springsecurity5.dialect.SpringSecurityDialect;

import demo.common.RememberMeTokenRepository;
import demo.service.CustomOAuth2UserService;
import demo.service.LoginService;

@Configuration
@EnableWebSecurity
// @EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableGlobalAuthentication
public class SecurityConfiguration {

    public static final String[] SECURITY_EXCLUDE_PATTERN_ARR = {
        "/assets/**", "/js/**", "/error/**"
    };

    @Bean public HttpFirewall defaultHttpFirewall() { 
        return new DefaultHttpFirewall();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SpringSecurityDialect springSecurityDialect(){
        return new SpringSecurityDialect();
    }
    
    //sha256("glycogen")
    private String secretKey = "1dd9f7072f15732b618e81491a3c887aa072390fbddcb04c5a9590c6bbc00887";

    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        return new RememberMeTokenRepository();
    }

    @Configuration    
    public class AdminSecurityJavaConfig extends WebSecurityConfigurerAdapter {

        @Autowired
        private LoginService loginService;

        @Bean
        public PersistentTokenBasedRememberMeServices persistentTokenBasedRememberMeServices1() {
            PersistentTokenBasedRememberMeServices persistentTokenBasedRememberMeServices = new PersistentTokenBasedRememberMeServices(secretKey, loginService, persistentTokenRepository());
            persistentTokenBasedRememberMeServices.setParameter("auto_login");
            persistentTokenBasedRememberMeServices.setAlwaysRemember(false);
            persistentTokenBasedRememberMeServices.setCookieName("remember-me");
            persistentTokenBasedRememberMeServices.setTokenValiditySeconds(604800); //1 week
            return persistentTokenBasedRememberMeServices;
        }

        @Autowired
        public AuthenticationSuccessHandler successAdminHandler() {
            return new CustomAdminSuccessHandler();
        }

        @Autowired
        public AuthenticationFailureHandler failureHandler() {
            return new CustomAdminFailureHandler();
        }

        @Autowired
        public AuthenticationSuccessHandler successOAuthHandler() {
            return new OAuthSuccessHandler();
        }

        @Autowired
        public AuthenticationFailureHandler failureOAuthHandler() {
            return new OAuthFailureHandler();
        }

        @Autowired
        private CustomOAuth2UserService OAuth2UserService;

        @Override
        public void configure(WebSecurity web) throws Exception {
            web.ignoring().antMatchers(SECURITY_EXCLUDE_PATTERN_ARR);
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.antMatcher("/**");
            
            http.authorizeRequests()
                    .antMatchers("/logout").authenticated()
                    // .antMatchers("/admin_profile/**", "/board/**").hasAnyRole("ADMIN")
                    .antMatchers("/", "/login/**", "/business", "/common/**").permitAll();

            http.formLogin()
                    .loginPage("/login")
                    .defaultSuccessUrl("/")
                    .permitAll()
                    .successHandler(successAdminHandler())
                    .failureHandler(failureHandler())
                    .usernameParameter("username")
                    .passwordParameter("password");

            http.logout()
                    .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                    .logoutSuccessUrl("/login")
                    .invalidateHttpSession(true).deleteCookies("JSESSIONID");

            http.exceptionHandling()
                    .accessDeniedPage("/error");

            http.rememberMe()
                    .authenticationSuccessHandler(successAdminHandler())
                    .key(secretKey)
                    .tokenValiditySeconds(604800)
                    .rememberMeParameter("auto_login")
                    .rememberMeCookieName("remember-me")
                    .tokenRepository(persistentTokenRepository())
                    .userDetailsService(loginService)
                    .rememberMeServices(persistentTokenBasedRememberMeServices1());

            http.headers()
                    .contentSecurityPolicy("default-src 'self' ;")
                    .and()
                    .contentSecurityPolicy("connect-src 'self' ;")
                    .and()
                    .contentSecurityPolicy("style-src 'self' 'unsafe-inline';")
                    .and()
                    .contentSecurityPolicy("frame-src *;")
                    .and()
                    .contentSecurityPolicy("font-src 'self' https://fonts.googleapis.com;")
                    .and()
                    .contentSecurityPolicy("script-src 'self' https://cdn.ckeditor.com https://cdn.jsdelivr.net;")
                    .and()
                    .contentSecurityPolicy("img-src * data:;");
                        
            http.oauth2Login()
                    .loginPage("/login")
                    .failureHandler(failureOAuthHandler())
                    .successHandler(successOAuthHandler())
                    .userInfoEndpoint()
                        .userService(OAuth2UserService);
    
        }

        @Override
        public void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(loginService).passwordEncoder(passwordEncoder());
        }
    }
}