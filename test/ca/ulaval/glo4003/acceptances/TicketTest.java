package ca.ulaval.glo4003.acceptances;

import ca.ulaval.glo4003.ConstantsManager;
import ca.ulaval.glo4003.TestGlobal;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.junit.Test;
import play.libs.WS;

import static org.junit.Assert.assertEquals;
import static play.test.Helpers.*;

public class TicketTest {

    private static final int PORT = 3333;
    private static final String PLAY_FRAMEWORK_COOKIE = "Cookie";
    private static final String EMPTY_BODY = "";
    private static final String A_USER_EMAIL = "user1@example.com";
    private static final String AN_ADMIN_EMAIL = "admin@example.com";
    private static final String PASSWORD = "secret";

    @Test
    public void dontAuthorizeCreateTicketIfConnectedButNotAnAdmin() {
        running(testServer(PORT, fakeApplication(new TestGlobal())), new Runnable() {
            @Override
            public void run() {
                String cookie = getLoginSessionCookie(A_USER_EMAIL, PASSWORD);
                WS.WSRequestHolder ticketsRequest = WS.url(getTicketsUrl());
                ticketsRequest.setHeader(PLAY_FRAMEWORK_COOKIE, cookie);

                WS.Response createTicketResponse = ticketsRequest.post(EMPTY_BODY).get();

                assertEquals(UNAUTHORIZED, createTicketResponse.getStatus());
            }
        });
    }

    @Test
    public void authorizeCreateTicketIfUseIsAnAdmin() {
        running(testServer(PORT, fakeApplication(new TestGlobal())), new Runnable() {
            @Override
            public void run() {
                String cookie = getLoginSessionCookie(AN_ADMIN_EMAIL, PASSWORD);
                WS.WSRequestHolder ticketsRequest = WS.url(getTicketsUrl());
                ticketsRequest.setHeader(PLAY_FRAMEWORK_COOKIE, cookie);

                WS.Response createTicketResponse = ticketsRequest.post(EMPTY_BODY).get();

                assertEquals(OK, createTicketResponse.getStatus());
            }
        });
    }

    private String getLoginSessionCookie(String username, String password) {
        ObjectNode jsonBody = JsonNodeFactory.instance.objectNode();
        jsonBody.put(ConstantsManager.USERNAME_FIELD_NAME, username);
        jsonBody.put(ConstantsManager.PASSWORD_FIELD_NAME, password);

        WS.Response loginResponse = WS.url("http://localhost:" + PORT + "/login").post(jsonBody).get();

        assertEquals(OK, loginResponse.getStatus());

        return loginResponse.getHeader("Set-" + PLAY_FRAMEWORK_COOKIE);
    }

    private String getTicketsUrl() {
        return "http://localhost:" + PORT + "/api/tickets";
    }
}
