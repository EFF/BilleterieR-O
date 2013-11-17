package ca.ulaval.glo4003.actions;

import ca.ulaval.glo4003.ConstantsManager;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

public class SecureAction extends Security.Authenticator {

    @Override
    public String getUsername(Http.Context ctx) {
        return ctx.session().get(ConstantsManager.COOKIE_SESSION_FIELD_NAME);
    }

    @Override
    public Result onUnauthorized(Http.Context ctx) {
       return unauthorized();
    }

}
