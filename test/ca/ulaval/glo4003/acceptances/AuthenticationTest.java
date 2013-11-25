package ca.ulaval.glo4003.acceptances;

import ca.ulaval.glo4003.TestGlobal;
import ca.ulaval.glo4003.acceptances.pages.EventsPage;
import ca.ulaval.glo4003.acceptances.pages.LoginPage;
import org.junit.Test;
import play.libs.F;
import play.test.TestBrowser;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static play.test.Helpers.*;

public class AuthenticationTest {

    private static final String EMAIL = "user1@example.com";
    private static final String PASSWORD = "secret";

    @Test
     public void MenuShowsLoginWhenLoggedOut() {
        running(testServer(3333, fakeApplication(new TestGlobal())), FIREFOX, new F.Callback<TestBrowser>() {
            @Override
            public void invoke(TestBrowser browser) {

                // Arrange & Act
                EventsPage eventsPage = new EventsPage(browser.getDriver());
                eventsPage.go();
                eventsPage.isAt();

                // Assert
                assertTrue(eventsPage.isLogInDisplayed());
            }
        });
    }

    @Test
    public void MenuLoginButtonGoesToLoginForm() {
        running(testServer(3333, fakeApplication(new TestGlobal())), FIREFOX, new F.Callback<TestBrowser>() {
            @Override
            public void invoke(TestBrowser browser) {

                // Arrange
                EventsPage eventsPage = new EventsPage(browser.getDriver());
                LoginPage loginPage = new LoginPage(browser.getDriver());
                eventsPage.go();
                eventsPage.isAt();

                assertTrue(eventsPage.isLogInDisplayed());

                // Act
                eventsPage.clickLoginButton();

                // Assert
                loginPage.isAt();
            }
        });
    }

    @Test
    public void LoginWithGoodCredentialsWorks() {
        running(testServer(3333, fakeApplication(new TestGlobal())), FIREFOX, new F.Callback<TestBrowser>() {
            @Override
            public void invoke(TestBrowser browser) {
                // Arrange
                EventsPage eventsPage = new EventsPage(browser.getDriver());
                LoginPage loginPage = new LoginPage(browser.getDriver());
                loginPage.go();
                loginPage.isAt();

                // Act
                loginPage.fillUsername(EMAIL);
                loginPage.fillPassword(PASSWORD);

                assertTrue(loginPage.isLogInDisplayed());

                loginPage.login();

                // Assert
                eventsPage.isAt();
                eventsPage.waitForSuccessMessageToDisplay();
                assertFalse(loginPage.isLogInDisplayed());
                assertTrue(loginPage.isLogOutDisplayed());
            }
        });
    }

    @Test
    public void LogoutWorks() {
        running(testServer(3333, fakeApplication(new TestGlobal())), FIREFOX, new F.Callback<TestBrowser>() {
            @Override
            public void invoke(TestBrowser browser) {
                // Arrange
                EventsPage eventsPage = new EventsPage(browser.getDriver());
                LoginPage loginPage = new LoginPage(browser.getDriver());
                loginPage.go();
                loginPage.isAt();


                loginPage.fillUsername(EMAIL);
                loginPage.fillPassword(PASSWORD);

                assertTrue(loginPage.isLogInDisplayed());

                loginPage.login();

                eventsPage.isAt();
                eventsPage.waitForSuccessMessageToDisplay();
                assertFalse(loginPage.isLogInDisplayed());
                assertTrue(loginPage.isLogOutDisplayed());

                // Act
                eventsPage.clickLogoutButton();

                // Assert
                loginPage.isAt();
                loginPage.isLoggedOut();
            }
        });
    }

    @Test
    public void LoginWithBadCredentialsFails() {
        running(testServer(3333, fakeApplication(new TestGlobal())), FIREFOX, new F.Callback<TestBrowser>() {
            @Override
            public void invoke(TestBrowser browser) throws InterruptedException {
                // Arrange
                EventsPage eventsPage = new EventsPage(browser.getDriver());
                LoginPage loginPage = new LoginPage(browser.getDriver());
                loginPage.go();
                loginPage.isAt();

                // Act
                loginPage.fillUsername("bad");
                loginPage.fillPassword("credentials");

                assertTrue(loginPage.isLogInDisplayed());

                loginPage.login();

                // Assert
                eventsPage.go();
                eventsPage.isAt();
                eventsPage.waitForErrorMessageToDisplay();
                assertTrue(loginPage.isLogInDisplayed());
            }
        });
    }
}
