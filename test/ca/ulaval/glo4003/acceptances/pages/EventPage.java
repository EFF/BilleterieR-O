package ca.ulaval.glo4003.acceptances.pages;

import org.fluentlenium.core.domain.FluentList;
import org.fluentlenium.core.domain.FluentWebElement;
import org.openqa.selenium.WebDriver;

import static org.fluentlenium.core.filter.FilterConstructor.withId;
import static org.fluentlenium.core.filter.FilterConstructor.withText;

public class EventPage extends BaseFluentPage {

    private final int id;

    public EventPage(WebDriver driver, int id) {
        super(driver);
        this.id = id;
    }

    @Override
    public String getUrl() {
        return BASE_URL + "events/" + id;
    }

    @Override
    public void isAt() {
        await().atMost(TIMEOUT).until(".table").isPresent();
    }

    public void waitUntilCategoriesHasSize(int count) {
        await().atMost(TIMEOUT).until(".category").hasSize(count);
    }

    public boolean categoryHas(int i, String expectedPrice, Integer expectedNumberOfTickets) {
        FluentWebElement category = getCategories().get(i);
        String price = category.findFirst(".price").getText();
        Integer numberOfTickets = Integer.parseInt(category.find(".numberOfTickets").getText());

        return (expectedPrice.equals(price) && expectedNumberOfTickets.equals(numberOfTickets));
    }

    public FluentList<FluentWebElement> getCategories() {
        return find(".category");
    }

    public void addTicketsToCartForCategory(Integer categoryIndex, Integer quantity) {
        FluentWebElement categoryLine = getCategories().get(categoryIndex);
        categoryLine.find(".category-quantity").text(quantity.toString());
        categoryLine.find(".category-add").click();
    }

    public int getTicketNumberForCategory(int categoryIndex) {
        String text = find(".numberOfTickets", categoryIndex).getText();
        return Integer.parseInt(text);
    }

    public void selectCombo(String comboName, String value) {
        find("select", withId().equalTo(comboName)).find("option", withText().equalTo(value)).click();
        await().atMost(TIMEOUT).until('#' + comboName).with(value);
    }

    public String getComboOptionValue(String comboName, String value){
        return find("select", withId().equalTo(comboName)).find("option", withText().equalTo(value)).getValue();
    }

    public void clickOnButton(String clazz) {
        find(clazz).click();
    }
}
