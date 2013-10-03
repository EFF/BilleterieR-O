package ca.ulaval.glo4003.acceptances.pages;

import org.openqa.selenium.WebDriver;

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

    public void removeItem(int itemIndex) {
        find(".item-remove", itemIndex).click();
    }

    public void removeAllItems() {
        find(".remove-all").click();
    }
}
