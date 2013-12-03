package ca.ulaval.glo4003.api;

import ca.ulaval.glo4003.ConstantsManager;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import play.mvc.Http;
import play.mvc.Results;

public class SecureBlocker implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        if(!isConnected() || (requiredAdmin(methodInvocation) && !isAdmin())) {
            return Results.unauthorized();
        }

        setUsernameInRequest();

        return methodInvocation.proceed();
    }

    private boolean requiredAdmin(MethodInvocation methodInvocation) {
        SecureAction annotation = methodInvocation.getMethod().getAnnotation(SecureAction.class);
        return annotation.admin();
    }

    private void setUsernameInRequest() {
        Http.Session session = Http.Context.current().session();
        String email = session.get(ConstantsManager.COOKIE_EMAIL_FIELD_NAME);
        Http.Context.current().request().setUsername(email);
    }

    private boolean isConnected() {
        Http.Session session = Http.Context.current().session();
        String email = session.get(ConstantsManager.COOKIE_EMAIL_FIELD_NAME);
        return (email != null && !email.isEmpty());
    }

    private boolean isAdmin() {
        Http.Session session = Http.Context.current().session();
        String admin = session.get(ConstantsManager.COOKIE_ADMIN_FIELD_NAME);
        return (admin != null && admin.equals(Boolean.toString(true)));
    }
}
