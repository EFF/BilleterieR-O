package ca.ulaval.glo4003.acceptances;

import ca.ulaval.glo4003.TestGlobal;
import ca.ulaval.glo4003.acceptances.pages.EventPage;
import ca.ulaval.glo4003.acceptances.pages.TicketPage;
import ca.ulaval.glo4003.api.user.ApiUserConstantsManager;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.fluentlenium.adapter.FluentTest;
import org.junit.Test;
import play.libs.F;
import play.libs.WS;
import play.test.TestBrowser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static play.test.Helpers.*;

public class TicketTest extends FluentTest {

    private static final String PLAY_FRAMEWORK_COOKIE = "Cookie";
    private static final String EMPTY_BODY = "";
    private static final String A_USER_EMAIL = "user1@example.com";
    private static final String AN_ADMIN_EMAIL = "admin@example.com";
    private static final String PASSWORD = "secret";
    private static final int PORT = 3333;
    private static final int A_TICKET_TYPE_SEAT_ID = 1450;
    private static final int ANOTHER_TICKET_TYPE_SEAT_ID = 1445;
    private static final int AN_EVENT_ID = 2;

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

    @Test
    public void requiredInfosAreDisplayed() {
        running(testServer(PORT, fakeApplication(new TestGlobal())), FIREFOX, new F.Callback<TestBrowser>() {
            public void invoke(TestBrowser browser) {
                TicketPage ticketPage = new TicketPage(browser.getDriver(), A_TICKET_TYPE_SEAT_ID, AN_EVENT_ID);
                ticketPage.go();
                ticketPage.isAt();

                assertTrue(ticketPage.requiredInfoAreDisplayed());
            }
        });
    }

    @Test
    public void clickOnEventDetailsShowEventPage() {
        running(testServer(PORT, fakeApplication(new TestGlobal())), FIREFOX, new F.Callback<TestBrowser>() {
            public void invoke(TestBrowser browser) {
                EventPage eventPage = new EventPage(browser.getDriver(), AN_EVENT_ID);
                TicketPage ticketPage = new TicketPage(browser.getDriver(), ANOTHER_TICKET_TYPE_SEAT_ID, AN_EVENT_ID);
                ticketPage.go();
                ticketPage.isAt();

                ticketPage.clickEventDetailsButton();
                eventPage.isAt();
                assertEquals(eventPage.getUrl(), browser.url());
            }
        });
    }

    private String getLoginSessionCookie(String username, String password) {
        ObjectNode jsonBody = JsonNodeFactory.instance.objectNode();
        jsonBody.put(ApiUserConstantsManager.USERNAME_FIELD_NAME, username);
        jsonBody.put(ApiUserConstantsManager.PASSWORD_FIELD_NAME, password);

        WS.Response loginResponse = WS.url("http://localhost:" + PORT + "/login").post(jsonBody).get();

        assertEquals(OK, loginResponse.getStatus());

        return loginResponse.getHeader("Set-" + PLAY_FRAMEWORK_COOKIE);
    }

    private String getTicketsUrl() {
        return "http://localhost:" + PORT + "/api/tickets";
    }
}
