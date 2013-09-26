package ca.ulaval.glo4003.acceptances.pages;

import org.fluentlenium.core.FluentPage;
import org.openqa.selenium.WebDriver;

public abstract class BaseFluentPage extends FluentPage {

    protected static final String BASE_URL = "http://localhost:3333/#!/";
    protected static final int TIMEOUT = 5000;

    protected BaseFluentPage(WebDriver driver) {
        super(driver);
    }
}
