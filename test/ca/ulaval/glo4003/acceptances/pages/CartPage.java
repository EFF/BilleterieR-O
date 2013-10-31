package ca.ulaval.glo4003.acceptances.pages;

import org.fluentlenium.core.domain.FluentWebElement;
import org.openqa.selenium.Alert;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;

import static org.fluentlenium.core.filter.FilterConstructor.withId;
import static org.fluentlenium.core.filter.FilterConstructor.withText;

public class CartPage extends BaseFluentPage {

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

    public boolean itemHas(int i, int expectedQuantity) {
        FluentWebElement cartItem = find(".item").get(i);
        int quantity = Integer.parseInt(cartItem.findFirst(".quantity").getText());

        return (quantity == expectedQuantity);
    }
}
