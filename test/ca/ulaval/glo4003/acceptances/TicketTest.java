package ca.ulaval.glo4003.acceptances;

import ca.ulaval.glo4003.ConstantsManager;
import ca.ulaval.glo4003.TestGlobal;
import ca.ulaval.glo4003.acceptances.pages.*;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.fluentlenium.adapter.FluentTest;
import org.junit.Test;
import play.libs.F;
import play.libs.WS;
import play.test.TestBrowser;

import static ca.ulaval.glo4003.domain.event.CategoryType.GENERAL_ADMISSION;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static play.test.Helpers.*;

public class TicketTest extends FluentTest {

    private static final String PLAY_FRAMEWORK_COOKIE = "Cookie";
    private static final String A_USER_EMAIL = "user1@example.com";
    private static final String AN_ADMIN_EMAIL = "admin@example.com";
    private static final String AN_ADMIN_PASSWORD = "secret";
    private static final int PORT = 3333;
    private static final int A_TICKET_TYPE_SEAT_ID = 1450;
    private static final int ANOTHER_TICKET_TYPE_SEAT_ID = 1445;
    private static final int AN_EVENT_ID = 2;
    private static final String A_CATEGORY_ID = "1";
    private static final int A_QUANTITY = 3;
    private static final int A_SEAT_NUMBER = 99;

    @Test
    public void dontAuthorizeCreateTicketIfConnectedButNotAnAdmin() {
        running(testServer(PORT, fakeApplication(new TestGlobal())), new Runnable() {
            @Override
            public void run() {
                String cookie = getLoginSessionCookie(A_USER_EMAIL, AN_ADMIN_PASSWORD);
                WS.WSRequestHolder ticketsRequest = WS.url(getTicketsUrl());
                ticketsRequest.setHeader(PLAY_FRAMEWORK_COOKIE, cookie);

                WS.Response createTicketResponse = ticketsRequest.post(getATicketJsonBody()).get();

                assertEquals(UNAUTHORIZED, createTicketResponse.getStatus());
            }
        });
    }

    @Test
    public void authorizeCreateTicketIfUserIsAnAdmin() {
        running(testServer(PORT, fakeApplication(new TestGlobal())), new Runnable() {
            @Override
            public void run() {
                String cookie = getLoginSessionCookie(AN_ADMIN_EMAIL, AN_ADMIN_PASSWORD);

                WS.WSRequestHolder ticketsRequest = WS.url(getTicketsUrl());
                ticketsRequest.setHeader(PLAY_FRAMEWORK_COOKIE, cookie);

                WS.Response createTicketResponse = ticketsRequest.post(getATicketJsonBody()).get();

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

    @Test
    public void createGeneralTicketsShouldIncrementTicketCount() {
        running(testServer(PORT, fakeApplication(new TestGlobal())), FIREFOX, new F.Callback<TestBrowser>() {
            public void invoke(TestBrowser browser) {
                LoginPage loginPage = new LoginPage(browser.getDriver());
                EventsPage eventsPage = new EventsPage(browser.getDriver());

                loginPage.go();
                loginPage.isAt();
                loginPage.performLogin(AN_ADMIN_EMAIL, AN_ADMIN_PASSWORD);

                eventsPage.go();
                eventsPage.isAt();
                eventsPage.clickOnFirstEventAdminButton();
                int eventId = Integer.parseInt(browser.getDriver().getCurrentUrl().split("/")[5]);

                EventPage eventPage = new EventPage(browser.getDriver(), eventId);
                eventPage.go();
                eventPage.isAt();
                int ticketNumber = eventPage.getTicketNumberForCategory(0);
                AddTicketPage addTicketPage = new AddTicketPage(browser.getDriver(), eventId);
                addTicketPage.go();
                addTicketPage.isAt();
                addTicketPage.createGeneralTickets(A_QUANTITY);

                eventPage.go();
                eventPage.isAt();
                assertEquals(ticketNumber + A_QUANTITY, eventPage.getTicketNumberForCategory(0));
            }
        });
    }

    @Test
    public void createSeatTicketsShouldIncrementTicketCount() {
        running(testServer(PORT, fakeApplication(new TestGlobal())), FIREFOX, new F.Callback<TestBrowser>() {
            public void invoke(TestBrowser browser) {
                LoginPage loginPage = new LoginPage(browser.getDriver());
                EventsPage eventsPage = new EventsPage(browser.getDriver());

                loginPage.go();
                loginPage.isAt();
                loginPage.performLogin(AN_ADMIN_EMAIL, AN_ADMIN_PASSWORD);

                eventsPage.go();
                eventsPage.isAt();
                eventsPage.clickOnEventAdminButton(1);
                int eventId = Integer.parseInt(browser.getDriver().getCurrentUrl().split("/")[5]);

                EventPage eventPage = new EventPage(browser.getDriver(), eventId);
                eventPage.go();
                eventPage.isAt();
                int ticketNumber = eventPage.getTicketNumberForCategory(1);
                AddTicketPage addTicketPage = new AddTicketPage(browser.getDriver(), eventId);
                addTicketPage.go();
                addTicketPage.isAt();
                addTicketPage.createSeatTickets(A_SEAT_NUMBER);

                eventPage.go();
                eventPage.isAt();
                assertEquals(ticketNumber + 1, eventPage.getTicketNumberForCategory(1));
            }
        });
    }

    @Test
    public void addTicketWithoutAdminAccountShouldShowErrorMessage(){
        running(testServer(PORT, fakeApplication(new TestGlobal())), FIREFOX, new F.Callback<TestBrowser>() {
            public void invoke(TestBrowser browser) {
                EventsPage eventsPage = new EventsPage(browser.getDriver());

                eventsPage.go();
                eventsPage.isAt();
                eventsPage.clickOnFirstEventAdminButton();
                int eventId = Integer.parseInt(browser.getDriver().getCurrentUrl().split("/")[5]);

                AddTicketPage addTicketPage = new AddTicketPage(browser.getDriver(), eventId);
                addTicketPage.go();
                addTicketPage.isAt();
                addTicketPage.createGeneralTickets(A_QUANTITY);

                addTicketPage.waitForErrorMessage();
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

    private ObjectNode getATicketJsonBody() {
        ObjectNode jsonBody = JsonNodeFactory.instance.objectNode();
        jsonBody.put(ConstantsManager.EVENT_ID_FIELD_NAME, AN_EVENT_ID);
        jsonBody.put(ConstantsManager.CATEGORY_ID_FIELD_NAME, A_CATEGORY_ID);
        jsonBody.put(ConstantsManager.CATEGORY_TYPE_FIELD_NAME, GENERAL_ADMISSION.toString());
        jsonBody.put(ConstantsManager.QUANTITY_FIELD_NAME, A_QUANTITY);
        return jsonBody;
    }
}
