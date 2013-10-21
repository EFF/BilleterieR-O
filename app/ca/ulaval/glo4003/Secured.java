package ca.ulaval.glo4003;

import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

public class Secured extends Security.Authenticator {

    @Override
    public String getUsername(Http.Context ctx) {
        return ctx.session().get(ConstantsManager.COOKIE_SESSION_FIELD_NAME);
    }

    @Override
    public Result onUnauthorized(Http.Context ctx) {
       return unauthorized();
    }

}
