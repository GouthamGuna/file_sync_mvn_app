package in.dev.gmsk.service;

import com.sun.net.httpserver.HttpContext;

public interface AuthenticatorService {

    void authenticateUserRequest(HttpContext context);

    boolean validateUserCardinals(String userName, String secret);
}
