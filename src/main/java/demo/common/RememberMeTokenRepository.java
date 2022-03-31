package demo.common;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import demo.service.LoginService;

public class RememberMeTokenRepository implements PersistentTokenRepository{
    @Autowired
    private LoginService loginService;

    // yyy-MM-dd HH:mm
    @Override
    public void createNewToken(PersistentRememberMeToken token) {
        // TODO Auto-generated method stub
        Date token_date = token.getDate();
        SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd HH:mm");
        String last_used = sdf.format(token_date);
        try {
            loginService.persistentLoginsInsert(token.getSeries(), last_used, token.getTokenValue(), token.getUsername());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void updateToken(String series, String tokenValue, Date lastUsed) {
        // TODO Auto-generated method stub
        SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd HH:mm");
        String last_used = sdf.format(lastUsed);
        try {
            loginService.persistentLoginsUpdate(series, last_used, tokenValue);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public PersistentRememberMeToken getTokenForSeries(String seriesId) {
        // TODO Auto-generated method stub
        SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd'T'HH:mm");
        HashMap<String, Object> entity;
        try {
            entity = loginService.persistentLoginsSelect(seriesId);
            if (entity != null) {
                Date token_date = sdf.parse(entity.get("last_used").toString());
                return new PersistentRememberMeToken(entity.get("username").toString(), entity.get("series").toString(), entity.get("tokenValue").toString(), token_date);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void removeUserTokens(String username) {
        // TODO Auto-generated method stub
        try {
            loginService.persistentLoginsDelete(username);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
