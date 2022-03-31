package demo.service;

import java.util.Random;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import demo.configuration.ExpiringMapConfig;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@EnableAsync
public class EmailService {
    private final JavaMailSender emailSender;
    private final SpringTemplateEngine templateEngine;
    private final ExpiringMapConfig valueOperations;

    public boolean checkCode(String code, String email) {
        String GetEmail = getData(code); //key(code)를 통해 value(email)를 꺼낸다.
        if (GetEmail == null) {
            System.out.println("Invalid Input Value");
            return false;
        }else if(email.equals(GetEmail))
            return true;
        else
            return false;

    }

    public boolean sendEmailMessage(String email) throws Exception{
        System.out.println("emailExpiringMap().size(): "+valueOperations.emailExpiringMap().size());
        try {
            String code = createCode();
            setData(code, email);   //duration 5minute
            MimeMessage message = emailSender.createMimeMessage();
            message.addRecipients(MimeMessage.RecipientType.TO, email);
            message.setSubject("[Authentic code] " + code);
            message.setText(setContext(code),"utf-8", "html");
            // 보내는 이름 설정
            message.setFrom(new InternetAddress("zkVoting@mail.zkrypto.com", "zkVoting"));

            emailSender.send(message);
            return true;
        } catch (Exception e) {
            System.out.println("Email Send Error");
            return false;
        }
    }

    @Async
    public void sendTempPwEmailMessage(String email, String id, String pw) throws Exception{
        MimeMessage message = emailSender.createMimeMessage();
        message.addRecipients(MimeMessage.RecipientType.TO, email);
        message.setSubject("[zkVoting] Temporary password");
        Context context = new Context();
        context.setVariable("id", id);
        context.setVariable("pw", pw);
        message.setText(templateEngine.process("mail_for_temporary_password", context),"utf-8", "html");
        // 보내는 이름 설정
        message.setFrom(new InternetAddress("zkVoting@mail.zkrypto.com", "zkVoting"));

        emailSender.send(message);
    }
    
    private String setContext(String code) {
        Context context = new Context();
        context.setVariable("code", code);
        return templateEngine.process("mail", context);
    }

    private String createCode(){
        StringBuffer key = new StringBuffer();
        Random rnd = new Random();

        for (int i=0; i<6; i++) {
            key.append(rnd.nextInt(10));
        }
        return key.toString();
    }

    public String getData(String key) {
        return valueOperations.emailExpiringMap().get(key);
    }

    public void setData(String key, String value) {
        valueOperations.emailExpiringMap().put(key, value);
    }

    public void deleteData(String key) {
        valueOperations.emailExpiringMap().remove(key);
    }
}
