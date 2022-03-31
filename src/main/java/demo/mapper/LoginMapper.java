package demo.mapper;

import java.util.HashMap;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import demo.dto.AdminDto;

@Mapper
public interface LoginMapper {
	AdminDto adminSelectById(String id) throws Exception;
	void createAdmin(AdminDto admin) throws Exception;
	void createOAuthAdmin(AdminDto admin) throws Exception;
	AdminDto findAdminSelect(AdminDto admin) throws Exception;
	AdminDto findAdminSelectUsingIdAndEmail(AdminDto admin) throws Exception;
	AdminDto findSameEmailSelect(String email) throws Exception;

	void updateAdminName(@Param("id") String id, @Param("name") String name) throws Exception;
	void updateAdminPw(@Param("id") String id, @Param("pw") String pw) throws Exception;
	void updateAdminInfo(@Param("id") String id, @Param("name") String name, @Param("email") String email, @Param("pw") String pw) throws Exception;
	void updateOAuthAdmin(AdminDto admin) throws Exception;

	void candidateDeleteUsingAdminId (String admin) throws Exception;
	void candidateCountingDeleteUsingAdminId (String admin) throws Exception;
	void registerUserDeleteUsingAdminId (String admin) throws Exception;
	void duplicatedCheckDeleteUsingAdminId (String admin) throws Exception;
	void permitUserDeleteUsingAdminId (String admin) throws Exception;
	void voteDeleteUsingAdminId (String admin) throws Exception;
	void deleteAdmin(String id) throws Exception;

	AdminDto findByGoogleSub(String googleSub) throws Exception;
	AdminDto findByNaverId(String naverId) throws Exception;
	AdminDto findByKakaoId(long kakaoId) throws Exception;
	void createByGoogleSub(AdminDto admin) throws Exception;
	void createByNaverId(AdminDto admin) throws Exception;
	void createByKakaoId(AdminDto admin) throws Exception;
	void saveByGoogleSub(AdminDto admin) throws Exception;
	void saveByNaverId(AdminDto admin) throws Exception;
	void saveByKakaoId(AdminDto admin) throws Exception;

	//persistent_logins table
	void persistentLoginsInsert(@Param("series") String series, @Param("last_used") String last_used, @Param("tokenValue") String tokenValue, @Param("username") String username) throws Exception;
	void persistentLoginsUpdate(@Param("series") String series, @Param("last_used") String last_used, @Param("tokenValue") String tokenValue) throws Exception;
	HashMap<String, Object> persistentLoginsSelect(String series) throws Exception;
	void persistentLoginsDelete(String username) throws Exception;
}
