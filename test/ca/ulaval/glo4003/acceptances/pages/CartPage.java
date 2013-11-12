package ca.ulaval.glo4003.acceptances.pages;

import org.fluentlenium.core.domain.FluentWebElement;
import org.openqa.selenium.Alert;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;

import static org.fluentlenium.core.filter.FilterConstructor.withId;
import static org.fluentlenium.core.filter.FilterConstructor.withText;

public class CartPage extends BaseFluentPage {

    private static final String A_CARD_TYPE = "vasi";
    private static final String A_CARD_NUMBER = "1234567890987654";
    private static final String A_CVV = "666";
    private static final String A_MONTH = "06";
    private static final String A_YEAR = "2015";

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

    public Alert waitAndGetAlert() {
        int i = 0;
        while (i++ < 10) {
            try {
                return getDriver().switchTo().alert();
            } catch (NoAlertPresentException e) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ignored) {
                }
            }
        }
        return null;
    }

    public void removeItem(int itemIndex) {
        find(".item-remove", itemIndex).click();
    }

    public void removeAllItems() {
        find(".remove-all").click();
    }

    public void selectItem(int itemIndex) {
        find("input", withId("check-all")).click();
        find("input", itemIndex, withId().equalTo("itemCheck")).click();
    }

    public void selectComboLabel(String value) {
        find("select", withId().equalTo("credit-combo")).find("option", withText().equalTo(value)).click();
    }

    public void fillCreditCardNumber(String cardNumber) {
        fill("input", withId().equalTo("card-number")).with(cardNumber);
    }

    public void fillCvv(String value) {
        fill("input", withId().equalTo("card-cvv")).with(value);
    }

    public void selectExpirationMonth(String month) {
        find("select", withId().equalTo("month-combo")).find("option", withText().equalTo(month)).click();
    }

    public void selectExpirationYear(String year) {
        find("select", withId().equalTo("year-combo")).find("option", withText().equalTo(year)).click();
    }

    public void confirm(WebDriver driver) {
        driver.switchTo().alert().accept();
    }

    public void checkout() {
        find("button", withId().equalTo("pay-button")).click();
    }

    public void dismiss(WebDriver driver) {
        driver.switchTo().alert().dismiss();
    }

    public void modifyNumberOfTicketsForItem(int i, Integer quantity) {
        fill("input", withId("input_quantity" + i)).with(quantity.toString());
    }

    public int getQuantityForItem(int i) {
        FluentWebElement quantityInput = findFirst("input", withId("input_quantity" + i));
        return Integer.parseInt(quantityInput.getValue());
    }

    public int getNumberOfItems() {
        return find(".item").size();
    }

    public void payWithCreditCard() {
        selectComboLabel(A_CARD_TYPE);
        fillCreditCardNumber(A_CARD_NUMBER);
        fillCvv(A_CVV);
        selectExpirationMonth(A_MONTH);
        selectExpirationYear(A_YEAR);
        checkout();
    }
}
