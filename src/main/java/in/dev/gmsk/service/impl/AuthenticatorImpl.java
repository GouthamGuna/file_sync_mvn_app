package in.dev.gmsk.service.impl;

import com.sun.net.httpserver.BasicAuthenticator;
import com.sun.net.httpserver.HttpContext;
import in.dev.gmsk.service.AuthenticatorService;

public class AuthenticatorImpl implements AuthenticatorService {

    @Override
    public void authenticateUserRequest(HttpContext context) {
        context.setAuthenticator(new BasicAuthenticator("get") {
            @Override
            public boolean checkCredentials(String user, String pwd) {
                return validateUserCardinals(user, pwd);
            }
        });
    }

    @Override
    public boolean validateUserCardinals(String userName, String secret) {
        return "dev_gmsk@admin".equals(userName) && "OM_Muruga".equals(secret);
    }
}
