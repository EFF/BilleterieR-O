package ca.ulaval.glo4003.acceptances.pages;

import org.fluentlenium.core.FluentPage;
import org.fluentlenium.core.domain.FluentList;
import org.openqa.selenium.WebDriver;

public abstract class BaseFluentPage extends FluentPage {

    protected static final String BASE_URL = "http://localhost:3333/#!/";
    protected static final int TIMEOUT = 5000;

    protected BaseFluentPage(WebDriver driver) {
        super(driver);
    }

    public int getCartSize() {
        return Integer.parseInt(find(".navbar").find(".cart-size").getText());
    }

    public boolean isLogInDisplayed() {
        return find(".navbar").find(".login-status").getAttribute("class").contains("login-status-out");
    }

    public boolean isLogOutDisplayed() {
        return find(".navbar").find(".login-status").getAttribute("class").contains("login-status-in");
    }

    public void clickLoginButon() {
        find(".navbar").find(".login-status").click();
    }

    public void waitForErrorMessageToDisplay() {
        await().atMost(TIMEOUT).until(".alertContainer .alert-danger").isPresent();
    }

    public void waitForSuccessMessageToDisplay() {
        await().atMost(TIMEOUT).until(".alertContainer .alert-success").isPresent();
    }
}
