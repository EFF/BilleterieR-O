package ca.ulaval.glo4003.acceptances;

import ca.ulaval.glo4003.TestGlobal;
import ca.ulaval.glo4003.acceptances.pages.LoginPage;
import ca.ulaval.glo4003.acceptances.pages.UserProfilePage;
import org.junit.Test;
import play.libs.F;
import play.test.TestBrowser;

import static play.test.Helpers.*;

public class UserProfileTest {

    private static final String EMAIL = "user1@example.com";
    private static final String NEW_EMAIL = "new@example.com";
    private static final String DUPLICATE_EMAIL = "user2@example.com";
    private static final String PASSWORD = "secret";
    private static final String NEW_PASSWORD = "newsecret";
    public static final String NOT_MY_ACTUAL_PASSWORD = "wrong";

    @Test
    public void updateMyEmailWithValidNewEmail() {
        running(testServer(3333, fakeApplication(new TestGlobal())), FIREFOX, new F.Callback<TestBrowser>() {
            public void invoke(TestBrowser browser) {
                UserProfilePage userProfilePage = new UserProfilePage(browser.getDriver());
                LoginPage loginPage = new LoginPage(browser.getDriver());

                loginAndGoToUserProfilePage(userProfilePage, loginPage);
                userProfilePage.updateEmail(NEW_EMAIL);

                userProfilePage.waitForSuccessMessageToDisplay();
                userProfilePage.waitUntilDisplayedUsernameIs(NEW_EMAIL);
            }
        });
    }

    @Test
    public void updateMyEmailWithDuplicateEmail() {
        running(testServer(3333, fakeApplication(new TestGlobal())), FIREFOX, new F.Callback<TestBrowser>() {
            public void invoke(TestBrowser browser) {
                UserProfilePage userProfilePage = new UserProfilePage(browser.getDriver());
                LoginPage loginPage = new LoginPage(browser.getDriver());

                loginAndGoToUserProfilePage(userProfilePage, loginPage);
                userProfilePage.updateEmail(DUPLICATE_EMAIL);

                userProfilePage.waitForErrorMessageToDisplay();
            }
        });
    }

    @Test
    public void updateMyPasswordWithGoodPasswords() {
        running(testServer(3333, fakeApplication(new TestGlobal())), FIREFOX, new F.Callback<TestBrowser>() {
            public void invoke(TestBrowser browser) {
                UserProfilePage userProfilePage = new UserProfilePage(browser.getDriver());
                LoginPage loginPage = new LoginPage(browser.getDriver());

                loginAndGoToUserProfilePage(userProfilePage, loginPage);
                userProfilePage.updatePassword(PASSWORD, NEW_PASSWORD, NEW_PASSWORD);

                userProfilePage.waitForSuccessMessageToDisplay();
            }
        });
    }

    @Test
    public void updateMyPasswordWithWrongActualPassword() {
        running(testServer(3333, fakeApplication(new TestGlobal())), FIREFOX, new F.Callback<TestBrowser>() {
            public void invoke(TestBrowser browser) {
                UserProfilePage userProfilePage = new UserProfilePage(browser.getDriver());
                LoginPage loginPage = new LoginPage(browser.getDriver());

                loginAndGoToUserProfilePage(userProfilePage, loginPage);
                userProfilePage.updatePassword(NOT_MY_ACTUAL_PASSWORD, NEW_PASSWORD, NEW_PASSWORD);

                userProfilePage.waitForErrorMessageToDisplay();
            }
        });
    }

    @Test
    public void updateMyPasswordWithConfirmationPassword() {
        running(testServer(3333, fakeApplication(new TestGlobal())), FIREFOX, new F.Callback<TestBrowser>() {
            public void invoke(TestBrowser browser) {
                UserProfilePage userProfilePage = new UserProfilePage(browser.getDriver());
                LoginPage loginPage = new LoginPage(browser.getDriver());

                loginAndGoToUserProfilePage(userProfilePage, loginPage);
                userProfilePage.updatePassword(PASSWORD, NEW_PASSWORD, PASSWORD);

                userProfilePage.waitForErrorMessageToDisplay();
            }
        });
    }

    private void loginAndGoToUserProfilePage(UserProfilePage userProfilePage, LoginPage loginPage) {
        loginPage.go();
        loginPage.isAt();
        loginPage.performLogin(EMAIL, PASSWORD);
        loginPage.waitUntilLoginIsDone();

        userProfilePage.go();
        userProfilePage.isAt();
    }
}
