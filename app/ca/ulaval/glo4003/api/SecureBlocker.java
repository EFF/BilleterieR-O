package ca.ulaval.glo4003.api;

import ca.ulaval.glo4003.ConstantsManager;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import play.mvc.Controller;
import play.mvc.Http;

public class SecureBlocker implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        if(!isConnected() || (requiredAdmin(methodInvocation) && !isAdmin())) {
            return Controller.unauthorized();
        }

        setUsernameInRequest();

        return methodInvocation.proceed();
    }

    private boolean requiredAdmin(MethodInvocation methodInvocation) {
        SecureAction annotation = methodInvocation.getMethod().getAnnotation(SecureAction.class);
        return annotation.admin();
    }

    private void setUsernameInRequest() {
        Http.Session session = Controller.ctx().session();
        String email = session.get(ConstantsManager.COOKIE_EMAIL_FIELD_NAME);
        Controller.request().setUsername(email);
    }

    private boolean isConnected() {
        Http.Session session = Controller.ctx().session();
        String email = session.get(ConstantsManager.COOKIE_EMAIL_FIELD_NAME);
        return (email != null && !email.isEmpty());
    }

    private boolean isAdmin() {
        Http.Session session = Controller.ctx().session();
        String admin = session.get(ConstantsManager.COOKIE_ADMIN_FIELD_NAME);
        return (admin != null && admin.equals(Boolean.toString(true)));
    }
}
