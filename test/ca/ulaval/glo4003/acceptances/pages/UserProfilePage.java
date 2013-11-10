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
        // TODO:
        super.isAt();
    }
}
