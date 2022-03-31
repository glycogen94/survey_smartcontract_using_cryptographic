package demo.dto;

import lombok.Data;

@Data
public class AdminDto {
	int adminId;
	String id;
	String pw;
	String created;
	String name;
	String email;
	String note;
	String uuid;
    String fileName;
	String googleSub;
	String naverId;
	long kakaoId;
}
