package demo.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties.Admin;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import demo.dto.AdminDto;
import demo.mapper.LoginMapper;

@Service
public class LoginService implements UserDetailsService{
	
	@Autowired
	private LoginMapper loginMapper;

	public void createAdmin(AdminDto admin) throws Exception {
		loginMapper.createAdmin(admin);
	}

	public AdminDto findAdminSelectUsingIdAndEmail(AdminDto admin) throws Exception {
		return loginMapper.findAdminSelectUsingIdAndEmail(admin);
	}

	public AdminDto findSameEmailSelect(String email) throws Exception {
		return loginMapper.findSameEmailSelect(email);
	}

	public AdminDto adminSelectById(String id) throws Exception {
		return loginMapper.adminSelectById(id);
	}

	public void updateAdminName(String admin_id, String admin_name) throws Exception {
		loginMapper.updateAdminName(admin_id, admin_name);
	}

	public void updateAdminPw(String admin_id, String admin_pw) throws Exception {
		loginMapper.updateAdminPw(admin_id, admin_pw);
	}

	public void updateAdminInfo(String admin_id, String admin_name, String admin_email, String admin_pw) throws Exception {
		loginMapper.updateAdminInfo(admin_id, admin_name, admin_email, admin_pw);
	}

	//persistent_logins table
	public void persistentLoginsInsert(String series, String last_used, String tokenValue, String username) throws Exception {
		loginMapper.persistentLoginsInsert(series, last_used, tokenValue, username);
	}
	public void persistentLoginsUpdate(String series, String last_used, String tokenValue) throws Exception {
		loginMapper.persistentLoginsUpdate(series, last_used, tokenValue);
	}
	public HashMap<String, Object> persistentLoginsSelect(String series) throws Exception {
		return loginMapper.persistentLoginsSelect(series);
	}
	public void persistentLoginsDelete(String username) throws Exception {
		loginMapper.persistentLoginsDelete(username);
	}
	

	@Transactional
	public void deleteAdmin(String admin_id) throws Exception {
		loginMapper.candidateDeleteUsingAdminId(admin_id);
		loginMapper.permitUserDeleteUsingAdminId(admin_id);
		loginMapper.candidateCountingDeleteUsingAdminId(admin_id);
		loginMapper.registerUserDeleteUsingAdminId(admin_id);
		loginMapper.duplicatedCheckDeleteUsingAdminId(admin_id);
		loginMapper.voteDeleteUsingAdminId(admin_id);
		loginMapper.deleteAdmin(admin_id);
	}

	public void createByGoogleSub(AdminDto admin) throws Exception {
		loginMapper.createByGoogleSub(admin);
	}
	public void createByNaverId(AdminDto admin) throws Exception {
		loginMapper.createByNaverId(admin);
	}
	public void createByKakaoId(AdminDto admin) throws Exception {
		loginMapper.createByKakaoId(admin);
	}

	public void saveByGoogleSub(AdminDto admin) throws Exception {
		loginMapper.saveByGoogleSub(admin);
	}
	public void saveByNaverId(AdminDto admin) throws Exception {
		loginMapper.saveByNaverId(admin);
	}
	public void saveByKakaoId(AdminDto admin) throws Exception {
		loginMapper.saveByKakaoId(admin);
	}

	public boolean isIdExist(String id) throws Exception {
		AdminDto admin = loginMapper.adminSelectById(id);

		if(admin != null)
			return true;
		return false;
	}

	@Override
	public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
		AdminDto admin;
		try {
			admin = loginMapper.adminSelectById(id);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			admin=null;
		} // 같은 이름 가진 회원있는지 확인 

		List<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));

		return new User(id, admin.getPw(), authorities);
	}

	@Transactional
	public AdminDto save(AdminDto admin) {
		// System.out.println("Enter save function");
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		admin.setPw(passwordEncoder.encode(admin.getPw()));
		return admin;
	}
}
