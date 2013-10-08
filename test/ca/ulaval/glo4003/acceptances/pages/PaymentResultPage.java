package ca.ulaval.glo4003.acceptances.pages;

import org.openqa.selenium.WebDriver;

public class PaymentResultPage extends BaseFluentPage{

    public PaymentResultPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public String getUrl() {
        return BASE_URL + "thanks/";
    }

    @Override
    public void isAt(){
        await().atMost(TIMEOUT).until("div").withId().equalTo("result").isPresent();
    }

}
