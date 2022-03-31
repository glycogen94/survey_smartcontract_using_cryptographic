package demo.controller;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import demo.dto.AdminDto;
import demo.service.EmailService;
import demo.service.LoginService;

@Controller
public class LoginController {
	
	@Autowired
	private LoginService loginService;

	@Autowired
	private EmailService emailService;

	@RequestMapping(value="/", method=RequestMethod.GET)
	public String index() throws Exception {
		return "index";
	}

	@RequestMapping(value="/login", method=RequestMethod.GET)
	public void login(HttpSession session) throws Exception {
		session.setAttribute("signUpOrNo", false);
		session.setAttribute("adminOrNot", true);
	}
	
	// @RequestMapping(value="/create/admin", method=RequestMethod.GET)
	// public String openCreate(HttpSession session) throws Exception {
	// 	session.setAttribute("signUpOrNo", true);
	// 	session.setAttribute("adminOrNot", true);
	// 	return "/login/admin_create";
	// }
	
	// @RequestMapping(value="/signup", method=RequestMethod.POST)
	// public String createAdmin(HttpSession session, AdminDto admin, @RequestParam("hash_pw") String hash_pw) throws Exception {
	// 	// System.out.println("[/create/admin] Client : " + hash_pw);
	// 	admin.setPw(hash_pw);
	// 	if(!loginService.isIdExist(admin.getId())){
	// 		admin = loginService.save(admin);
	// 		// admin.setPw(passwordEncoder.encode(hash_pw));
	// 		session.setAttribute("create_admin", admin);
	// 		return "redirect:/login/email";
	// 	}
	// 	return "/login/problem_same_admin";
	// }
	
	// @RequestMapping(value="/login/email", method=RequestMethod.GET)
	// public ModelAndView openAuthPage(HttpSession session) throws Exception {
	// 	// if(session.getAttribute("create_admin") == null) return new ModelAndView("/login/problem_admin_create_access_denied");
	// 	ModelAndView mv = new ModelAndView("/login/email");
	// 	mv.addObject("disabled", true);
	// 	mv.addObject("readonly", false);
	// 	return mv;
	// }

	@RequestMapping(value="/login/signup", method=RequestMethod.GET)
	public String signupView(HttpSession session) throws Exception {
		session.setAttribute("signUpOrNo", true);
		session.setAttribute("adminOrNot", true);
		return "login/signup";
	}

	@RequestMapping(value="/login/signup", method=RequestMethod.POST)
	public String signup(AdminDto admin, @RequestParam("hash_pw") String hash_pw, @RequestParam("target_email") String email, @RequestParam("input_code") String code) throws Exception {
		admin.setPw(hash_pw);
		if(!loginService.isIdExist(admin.getId())){
			admin = loginService.save(admin);
		}
		else return "login/problem_same_admin";

		if(emailService.checkCode(code,email)){
			admin.setEmail(email);
			loginService.createAdmin(admin);
			return "redirect:/login?error=false&Type=signup";
		}

		else return "login/problem_auth_failed2";
	}

	@ResponseBody
	@RequestMapping(value="/login/email/request", method=RequestMethod.GET)
	public String trusteeCreateRequest(@RequestParam("mode") String mode, @RequestParam("input") String input) throws Exception {
		if(mode.matches("sendEmail")) {
			// AdminEntity admin = adminRepository.findByEmail(input);
			AdminDto admin = loginService.findSameEmailSelect(input); 
			if (admin != null) return "duplicated_email";
			boolean result = emailService.sendEmailMessage(input);
			if(result) return "succ";
			else return "fail_send";
		} 
		// else if(mode.matches("changePw")) {
		// 	AdminDto admin = loginService.findSameEmailSelect(input);
		// 	if (admin == null) return "fail";
		// 	boolean result = emailService.sendEmailMessage(input);
		// 	if(result) return "succ";
		// 	else return "fail_send";
		// } 
		return "fail";
	}

	@RequestMapping(value="/login/sendEmail", method=RequestMethod.GET)
	public ModelAndView emailAuth(HttpSession session, @RequestParam("email") String email) throws Exception {
		// if(session.getAttribute("create_admin") == null) return new ModelAndView("/login/problem_admin_create_access_denied");
		ModelAndView mv = new ModelAndView("login/email");
		// emailService.sendEmailMessage(email);
		boolean result = emailService.sendEmailMessage(email);
		if(result) {
			mv.addObject("disabled", false);
		}
		else {
			mv.addObject("disabled", true);
			//email 실패했음 alert 필요
		}
		AdminDto admin = (AdminDto) session.getAttribute("create_admin");
		if (admin.getEmail() != null){
			mv.addObject("trigger", true);
		} else mv.addObject("trigger", false);
		// mv.addObject("disabled", false);
		mv.addObject("email", email);
		mv.addObject("readonly", true);
		
		return mv;
	}

	@RequestMapping(value="/login/verifyCode", method=RequestMethod.POST)
	public String verifyCode(HttpServletResponse response, HttpSession session, @RequestParam("email") String email, @RequestParam("input_code") String code) throws Exception{
		// if(session.getAttribute("create_admin") == null) return "/login/problem_admin_create_access_denied";
		AdminDto tmpAdmin = loginService.findSameEmailSelect(email); // 같은 이름 가진 회원있는지 확인 
		if(tmpAdmin != null){ 
			session.removeAttribute("create_admin");
			return "login/problem_same_email";
		}
		if(emailService.checkCode(code,email)){
			AdminDto admin = (AdminDto) session.getAttribute("create_admin");
			admin.setEmail(email);
			loginService.createAdmin(admin);
			// ScriptUtils.alert(response, "인증 되었습니다.");
			session.removeAttribute("create_admin");
			return "redirect:/login?error=false&Type=signup";
		}
		session.removeAttribute("create_admin");
		return "login/problem_auth_failed";
	}

	// @RequestMapping(value="/login/problem_access_denied",  method=RequestMethod.GET)
    // public String deniedView() {
    //     return "login/problem_access_denied";
    // }

	@RequestMapping(value="/admin_profile",  method=RequestMethod.GET)
    public ModelAndView adminProfileView(HttpSession session) throws Exception {
		// User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		// String id = user.getUsername();
		String id = (String) session.getAttribute("admin_id");
		System.out.println("admin_profile id: " + id);
		AdminDto admin = loginService.adminSelectById(id);
		ModelAndView mv = new ModelAndView("admin_profile");
		mv.addObject("admin", admin);
        return mv;
    }

	@RequestMapping(value="/admin_profile/editInfo", method=RequestMethod.POST)
	public ModelAndView adminEditInfo(HttpSession session,AdminDto admin) throws Exception {
		// if(session.getAttribute("admin_id") == null) return "redirect:/login/problem_access_denied";
	
		session.setAttribute("create_admin", admin);
		ModelAndView mv = new ModelAndView("login/email");
		mv.addObject("disabled", true);
		mv.addObject("readonly", true);
		mv.addObject("email", admin.getEmail());
		// mv.addObject("admin_id", admin.getAdminId());
		// loginService.updateAdminInfo(admin_id, admin_name, admin_email);
		return mv;
	}

	@RequestMapping(value="/admin_profile/changePw", method=RequestMethod.POST)
	public ModelAndView profileEmailView(HttpSession session, AdminDto admin, @RequestParam("hash_pw") String hash_pw) throws Exception {
		// if(session.getAttribute("create_admin") == null) return new ModelAndView("/login/problem_admin_create_access_denied");
		admin.setPw(hash_pw);
		admin = loginService.save(admin);
		// admin.setPw(passwordEncoder.encode(hash_pw));
		session.setAttribute("create_admin", admin);
		ModelAndView mv = new ModelAndView("login/email");
		mv.addObject("disabled", true);
		mv.addObject("readonly", true);
		mv.addObject("email", admin.getEmail());
		// mv.addObject("admin_id", admin.getAdminId());
		return mv;
	}

	@RequestMapping(value="/admin_profile/signOut", method=RequestMethod.GET)
	public ModelAndView signOutPage(HttpSession session) throws Exception {
		// if(session.getAttribute("admin_id") == null) return new ModelAndView("redirect:/login/problem_access_denied");
		ModelAndView mv = new ModelAndView("sign_out");
		// String admin_id = (String) session.getAttribute("admin_id");
		
		// User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		// String admin = user.getUsername();
		String admin = (String) session.getAttribute("admin_id");
		mv.addObject("admin_id", admin);

		return mv;
	}

	@RequestMapping(value="/admin_profile/signOut", method=RequestMethod.POST)
	public String signOut(HttpSession session) throws Exception {
		// if(session.getAttribute("admin_id") == null) return "redirect:/login/problem_access_denied";
		// String admin_id = (String) session.getAttribute("admin_id");
		
		// User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		// String admin = user.getUsername();

		String admin = (String) session.getAttribute("admin_id");
		System.out.println("admin" + admin);
		loginService.deleteAdmin(admin);
		session.invalidate();
		return "redirect:/";
	}

	@RequestMapping(value="/admin_profile/verifyCodeAndUpdate", method=RequestMethod.POST)
	public String verifyCodeAndUpdate(HttpServletResponse response, HttpSession session, @RequestParam("email") String email, @RequestParam("input_code") String code) throws Exception{
		// if(session.getAttribute("create_admin") == null) return "/login/problem_admin_create_access_denied";
		AdminDto admin = (AdminDto) session.getAttribute("create_admin");
		AdminDto tmpAdmin = loginService.adminSelectById(admin.getId());
		if (!tmpAdmin.getEmail().equals(email)){
			tmpAdmin = loginService.findSameEmailSelect(email); // 같은 이름 가진 회원있는지 확인 
			if(tmpAdmin != null){ 
				session.removeAttribute("create_admin");
				return "login/problem_same_email";
			}
		}
		if(emailService.checkCode(code,email)){
			loginService.updateAdminInfo(admin.getId(), admin.getName(), admin.getEmail(), admin.getPw());
			session.removeAttribute("create_admin");
			return "redirect:/";
		}
		session.removeAttribute("create_admin");
		return "login/problem_auth_failed";
	}

	@RequestMapping(value="/business", method=RequestMethod.GET)
	public String businessView() throws Exception {
		return "business";
	}

	@RequestMapping(value="/login/forgot", method=RequestMethod.GET)
	public ModelAndView passwordForgotView() throws Exception {
		ModelAndView mv = new ModelAndView("login/forgot");

		return mv;
	}

	@ResponseBody
	@RequestMapping(value="/login/forgot/request", method=RequestMethod.POST)
	public String passwordForgotReq(AdminDto adminDto) throws Exception {
		AdminDto admin = loginService.findAdminSelectUsingIdAndEmail(adminDto);
		if(admin == null) {
			System.out.println("Enter if block");
			return "not_regist_admin";
		} else {
			System.out.println("Enter else block");
			String pw = "";
			for (int i =0; i<12; i++) {
				pw += (char) ((Math.random() * 26) + 97);
			}
			MessageDigest md = MessageDigest.getInstance("SHA-512");
			md.update(pw.getBytes());
			String hash_pw = String.format("%0128x", new BigInteger(1, md.digest()));
			admin.setPw(hash_pw);
			admin = loginService.save(admin);
			loginService.updateAdminPw(admin.getId(), admin.getPw());
			emailService.sendTempPwEmailMessage(adminDto.getEmail(), adminDto.getId(), pw);
			return "sucess";
		}
	}

	@RequestMapping(value="/login/OAuth/create", method=RequestMethod.GET)
	public ModelAndView CreateOAuthView(HttpSession session) throws Exception {
		ModelAndView mv = new ModelAndView("login/OAuth_create");

		OAuth2UserRequest userRequest = (OAuth2UserRequest) session.getAttribute("OAuth2UserRequest");
        session.removeAttribute("OAuth2UserRequest");

        OAuth2UserService delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        
        String id = null;
        String email=null;
        Map<String, Object> attributes = oAuth2User.getAttributes();

        if (registrationId.matches("google")){
            id = (String) attributes.get("sub");
            email = (String) oAuth2User.getAttributes().get("email");
        } else if(registrationId.matches("kakao")){
            id = String.valueOf((Long) attributes.get("id"));
            Map<String, Object> kakao_account = (Map<String, Object>) attributes.get("kakao_account");
            email = (String) kakao_account.get("email");
        } else if(registrationId.matches("naver")){
            Map<String, Object> attributes_response = (Map<String, Object>) attributes.get("response");
            id = (String) attributes_response.get("id");
            email = (String) attributes_response.get("email");
        }

        session.removeAttribute("member_id");

		mv.addObject("oauthId", id);
		mv.addObject("email", email);
		mv.addObject("registrationId", registrationId);

		return mv; 
	}

	@RequestMapping(value="/login/OAuth/signup", method=RequestMethod.POST)
	public String CreateOAuth(HttpSession session, AdminDto admin, @RequestParam("hash_pw") String hash_pw, String oauthId, String registrationId) throws Exception {
		System.out.println("Enter login/OAuth_signup");

		admin.setPw(hash_pw);
		if(!loginService.isIdExist(admin.getId())){
			admin = loginService.save(admin);
			// admin.setEmail(email);
			System.out.println("registration id: " +registrationId);

			if(registrationId.matches("google")){
				admin.setGoogleSub(oauthId);
				loginService.createByGoogleSub(admin);
			}
			else if (registrationId.matches("kakao")){
				admin.setKakaoId(Long.parseLong(oauthId));
				loginService.createByKakaoId(admin);
			}
			else if (registrationId.matches("naver")){
				admin.setNaverId(oauthId);
				loginService.createByNaverId(admin);
			}

			return "redirect:/login?error=false&Type=signup";
		}

		return "login/problem_same_admin";
	}

	@RequestMapping(value="/login/OAuth/linkMember", method=RequestMethod.GET)
	public ModelAndView LinkMemberView(HttpSession session) throws Exception {
		ModelAndView mv = new ModelAndView("login/link_member");

		OAuth2UserRequest userRequest = (OAuth2UserRequest) session.getAttribute("OAuth2UserRequest");
        session.removeAttribute("OAuth2UserRequest");

        OAuth2UserService delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        
        String id = null;
        Map<String, Object> attributes = oAuth2User.getAttributes();

        if (registrationId.matches("google")){
            id = (String) attributes.get("sub");
        } else if(registrationId.matches("kakao")){
            id = String.valueOf((Long) attributes.get("id"));
        } else if(registrationId.matches("naver")){
            Map<String, Object> attributes_response = (Map<String, Object>) attributes.get("response");
            id = (String) attributes_response.get("id");
        }

        String existing_id = (String) session.getAttribute("member_id");
        session.removeAttribute("member_id");
		
		mv.addObject("adminId", existing_id);
		mv.addObject("oauthId", id);
		mv.addObject("registrationId", registrationId);
		mv.addObject("Type", "linkMember");

		return mv; 
	}

	@RequestMapping(value="/login/OAuth/linkMember", method=RequestMethod.POST)
	public String LinkMember(HttpSession session, String adminId, String oauthId, String registrationId) throws Exception {
		System.out.println("Enter login/OAuth/linkMember post");
		AdminDto admin = new AdminDto();
		admin.setId(adminId);

		if(registrationId.matches("google")){
			admin.setGoogleSub(oauthId);
			loginService.saveByGoogleSub(admin);
		}
		else if (registrationId.matches("kakao")){
			admin.setKakaoId(Long.parseLong(oauthId));
			loginService.saveByKakaoId(admin);
		}
		else if (registrationId.matches("naver")){
			admin.setNaverId(oauthId);
			loginService.saveByNaverId(admin);
		}

		return "redirect:/login?error=false&Type=linkMember";

	}

	// @ResponseBody
	// @RequestMapping(value="/login/OAuth/request", method=RequestMethod.POST)
	// public String CreateOAuthResponse(HttpSession session)  throws Exception {
	// 	System.out.println("Enter /login/OAuth/request");
		
	// 	Authentication authentication = (Authentication) session.getAttribute("authentication");
	// 	authentication.setAuthenticated(false);
	// 	session.removeAttribute("authentication");

	// 	return "success";
	// }
}