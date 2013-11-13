package ca.ulaval.glo4003.acceptances.pages;

import org.openqa.selenium.WebDriver;

import static org.fluentlenium.core.filter.FilterConstructor.withId;

public class LoginPage extends BaseFluentPage {

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public String getUrl() {
        return BASE_URL + "login/";
    }

    @Override
    public void isAt() {
        await().atMost(TIMEOUT).until("#login-button").isPresent();
    }

    public void performLogin(String email, String password) {
        fillUsername(email);
        fillPassword(password);
        login();
        await().atMost(TIMEOUT).until(".login-status-in").isPresent();
    }

    public void fillUsername(String value) {
        fill("input", withId().equalTo("username-input")).with(value);
    }

    public void fillPassword(String value) {
        fill("input", withId().equalTo("password-input")).with(value);
    }

    public void isLoggedOut() {
        await().atMost(TIMEOUT).until(".login-status-out").isPresent();
    }

    public void login() {
        find("button", withId().equalTo("login-button")).click();
    }

    public String getDisplayedUsername() {
        return find("#username").getText();
    }
}
