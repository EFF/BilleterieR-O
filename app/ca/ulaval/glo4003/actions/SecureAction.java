package ca.ulaval.glo4003.actions;

import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

public class SecureAction extends Security.Authenticator {

    @Override
    public Result onUnauthorized(Http.Context context) {
        return unauthorized();
    }
}
