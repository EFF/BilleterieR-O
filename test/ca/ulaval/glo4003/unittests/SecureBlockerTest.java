package ca.ulaval.glo4003.unittests;

import ca.ulaval.glo4003.ConstantsManager;
import ca.ulaval.glo4003.api.SecureAction;
import ca.ulaval.glo4003.api.SecureBlocker;
import ca.ulaval.glo4003.unittests.api.BaseControllerTest;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.Before;
import org.junit.Test;
import play.mvc.Result;

import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static play.test.Helpers.status;

public class SecureBlockerTest extends BaseControllerTest {

    private static final int UNAUTHORIZED = 401;
    private static final boolean NOT_CONNECTED = false;
    private static final boolean NOT_ADMIN = false;
    private static final boolean CONNECTED = true;
    private static final boolean ADMIN = true;
    private static final String A_USER_EMAIL = "user@example.com";

    private SecureBlocker secureBlocker;

    @Before
    public void setup() {
        secureBlocker = new SecureBlocker();
    }

    @Test
    public void blockIfNotConnected() throws Throwable {
        MethodInvocation mockedMethodInvocation = createMockedSecureMethodInvocation(NOT_ADMIN);
        setCredentials(NOT_CONNECTED, NOT_ADMIN);

        Result result = (Result) secureBlocker.invoke(mockedMethodInvocation);

        assertEquals(UNAUTHORIZED, status(result));
    }

    @Test
    public void dontBlockIfConnectedAndAdminCredentialsIsNotNeeded() throws Throwable {
        MethodInvocation mockedMethodInvocation = createMockedSecureMethodInvocation(NOT_ADMIN);
        setCredentials(CONNECTED, NOT_ADMIN);

        secureBlocker.invoke(mockedMethodInvocation);

        verify(mockedMethodInvocation, times(1)).proceed();
    }

    @Test
    public void blockNonAdminIfAdminCredentialsNeeded() throws Throwable {
        MethodInvocation mockedMethodInvocation = createMockedSecureMethodInvocation(NOT_ADMIN);
        setCredentials(NOT_CONNECTED, NOT_ADMIN);

        Result result = (Result) secureBlocker.invoke(mockedMethodInvocation);

        assertEquals(UNAUTHORIZED, status(result));
    }

    @Test
    public void dontBlockAdminIfAdminCredentialsNeeded() throws Throwable {
        MethodInvocation mockedMethodInvocation = createMockedSecureMethodInvocation(ADMIN);
        setCredentials(CONNECTED, ADMIN);

        secureBlocker.invoke(mockedMethodInvocation);

        verify(mockedMethodInvocation, times(1)).proceed();
    }

    private MethodInvocation createMockedSecureMethodInvocation(boolean adminRequired) throws NoSuchMethodException {
        MethodInvocation mockedMethodInvocation = mock(MethodInvocation.class);
        Method securedMethod = getSecuredMethod(adminRequired);
        when(mockedMethodInvocation.getMethod()).thenReturn(securedMethod);
        return mockedMethodInvocation;
    }

    private Method getSecuredMethod(boolean adminRequired) throws NoSuchMethodException {
        // We cannot mock a Method because the class is declared as final
        SecureTest secureTest = new SecureTest();
        Class<? extends SecureTest> clazz = secureTest.getClass();

        if (adminRequired) {
            return clazz.getMethod("adminAction");
        } else {
            return clazz.getMethod("connectedAction");
        }
    }

    private void setCredentials(boolean isConnected, boolean isAdmin) {
        String email = (isConnected) ? A_USER_EMAIL : null;
        when(mockedSession.get(ConstantsManager.COOKIE_EMAIL_FIELD_NAME)).thenReturn(email);
        when(mockedSession.get(ConstantsManager.COOKIE_ADMIN_FIELD_NAME)).thenReturn(Boolean.toString(isAdmin));
    }

    private static class SecureTest {

        @SecureAction(admin = true)
        public void adminAction() {
        }

        @SecureAction
        public void connectedAction() {
        }

    }
}
