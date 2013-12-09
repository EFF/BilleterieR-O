package ca.ulaval.glo4003.acceptances.pages;

import org.fluentlenium.core.FluentPage;
import org.fluentlenium.core.domain.FluentList;
import org.fluentlenium.core.domain.FluentWebElement;
import org.openqa.selenium.WebDriver;

public abstract class BaseFluentPage extends FluentPage {

    protected static final String BASE_URL = "http://localhost:3333/#!/";
    protected static final int TIMEOUT = 5000;

    protected BaseFluentPage(WebDriver driver) {
        super(driver);
    }

    public int getCartSize() {
        await().atMost(TIMEOUT).until(".cart-size").isPresent();
        return Integer.parseInt(find(".navbar").find(".cart-size").getText());
    }

    public boolean isLogInDisplayed() {
        return find(".navbar").find(".login-status").getAttribute("class").contains("login-status-out");
    }

    public boolean isLogOutDisplayed() {
        return find(".navbar").find(".login-status").getAttribute("class").contains("login-status-in");
    }

    public void clickLoginButton() {
        find(".navbar").find(".login-status-out").click();
    }

    public void clickLogoutButton() {
        find(".navbar").find(".login-status-in").click();
    }

    public FluentList<FluentWebElement> getErrorMessages() {
        return find(".alertContainer .alert-danger");
    }

    public void waitForErrorMessageToDisplay() {
        await().atMost(TIMEOUT).until(".alertContainer .alert-danger").isPresent();
    }

    public void waitForSuccessMessageToDisplay() {
        await().atMost(TIMEOUT).until(".alertContainer .alert-success").isPresent();
    }

    public void waitForInfoMessageToDisplay() {
        await().atMost(TIMEOUT).until(".alertContainer .alert-info").isPresent();
    }

    public void waitUntilCartHasSize(int size) {
        await().atMost(TIMEOUT).until(".cart-size").hasText(Integer.toString(size));
    }
}
