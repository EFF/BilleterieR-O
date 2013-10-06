package ca.ulaval.glo4003.acceptances.pages;

import org.openqa.selenium.WebDriver;

import static org.fluentlenium.core.filter.FilterConstructor.withId;
import static org.fluentlenium.core.filter.FilterConstructor.withText;

public class CartPage extends BaseFluentPage {
    private static final String FALSE_CVV = "123";
    private static final String FALSE_CARD_NUMBER = "12345678901234";

    public CartPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public String getUrl() {
        return BASE_URL + "cart/";
    }

    @Override
    public void isAt() {
        await().atMost(TIMEOUT).until(".table").isPresent();
    }

    public void waitUntilItemsHasSize(int count) {
        await().atMost(TIMEOUT).until(".item").hasSize(count);
    }

    public void removeItem(int itemIndex) {
        find(".item-remove", itemIndex).click();
    }

    public void removeAllItems() {
        find(".remove-all").click();
    }

    public void selectItem(int itemIndex) {
        find("input", itemIndex, withId().equalTo("itemCheck")).click();
    }

    public void selectComboLabel(String value) {
        find("select", withId().equalTo("credit-combo")).find("option", withText().equalTo(value)).click();
    }

    public void fillCreditCardNumber() {
        fill("input", withId().equalTo("card-number")).with(FALSE_CARD_NUMBER);
    }

    public void fillCvv() {
        fill("input", withId().equalTo("card-cvv")).with(FALSE_CVV);
    }

    public void selectExpirationMonth(String month) {
        find("select", withId().equalTo("month-combo")).find("option", withText().equalTo(month)).click();
    }

    public void selectExpirationYear(String year) {
        find("select", withId().equalTo("year-combo")).find("option", withText().equalTo(year)).click();
    }

    public void sendPaymentForm() {
        find("button", withId().equalTo("pay-button")).click();
    }
}
