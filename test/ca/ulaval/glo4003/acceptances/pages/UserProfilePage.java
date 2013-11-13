package ca.ulaval.glo4003.acceptances.pages;

import org.openqa.selenium.WebDriver;

public class UserProfilePage extends BaseFluentPage {

    public UserProfilePage(WebDriver driver) {
        super(driver);
    }

    @Override
    public String getUrl() {
        return BASE_URL + "user_profile";
    }

    @Override
    public void isAt() {
        await().atMost(TIMEOUT).until("#input-email").isPresent();
    }

    public void updateEmail(String email) {
        fill("#input-email").with(email);
        find("#submit-email").click();
    }

    public void waitUntilDisplayedUsernameIs(String email) {
        await().atMost(TIMEOUT).until("#username").hasText(email);
    }

    public void updatePassword(String password, String newPassword) {
        fill("#input-actual-password").with(password);
        fill("#input-password").with(newPassword);
        fill("#input-password-confirmation").with(newPassword);
        find("#submit-password").click();
    }
}
