package ca.ulaval.glo4003.acceptances;

import ca.ulaval.glo4003.TestGlobal;
import ca.ulaval.glo4003.acceptances.pages.EventsPage;
import ca.ulaval.glo4003.acceptances.pages.LoginPage;
import org.junit.Test;
import play.libs.F;
import play.test.TestBrowser;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.*;

public class AuthenticationTest {

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
                assertThat(eventsPage.isLogInDisplayed()).isTrue();
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

                assertThat(eventsPage.isLogInDisplayed()).isTrue();

                // Act
                eventsPage.clickLoginButon();

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
                loginPage.fillUsername("user");
                loginPage.fillPassword("password");

                assertThat(loginPage.isLogInDisplayed()).isTrue();

                loginPage.login();

                // Assert
                eventsPage.isAt();
                eventsPage.waitForSuccessMessageToDisplay();
                assertThat(loginPage.isLogInDisplayed()).isFalse();
                assertThat(loginPage.isLogOutDisplayed()).isTrue();
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


                loginPage.fillUsername("user");
                loginPage.fillPassword("password");

                assertThat(loginPage.isLogInDisplayed()).isTrue();

                loginPage.login();

                eventsPage.isAt();
                eventsPage.waitForSuccessMessageToDisplay();
                assertThat(loginPage.isLogInDisplayed()).isFalse();
                assertThat(loginPage.isLogOutDisplayed()).isTrue();

                // Act
                eventsPage.clickLoginButon();

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

                assertThat(loginPage.isLogInDisplayed()).isTrue();

                loginPage.login();

                // Assert
                eventsPage.go();
                eventsPage.isAt();
                eventsPage.waitForErrorMessageToDisplay();
                assertThat(loginPage.isLogInDisplayed()).isTrue();
            }
        });
    }
}
