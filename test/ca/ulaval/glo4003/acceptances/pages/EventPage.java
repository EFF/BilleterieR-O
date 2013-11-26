package ca.ulaval.glo4003.acceptances.pages;

import org.fluentlenium.core.domain.FluentList;
import org.fluentlenium.core.domain.FluentWebElement;
import org.openqa.selenium.WebDriver;

import static org.fluentlenium.core.filter.FilterConstructor.withText;

public class EventPage extends BaseFluentPage {

    public static final String TICKET_LIST_ID_1 = "#ticketList1";
    private final long id;

    public EventPage(WebDriver driver, long id) {
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

    public void clickOnButton(String clazz) {
        find(clazz).click();
    }

    public void clickOnAddButtonForCategory(int categoryIndex) {
        FluentWebElement categoryLine = getCategories().get(categoryIndex);
        categoryLine.find(".category-add").click();
    }

    public void clickOnDetailsButton() {
        clickOnButton(".btn-details1");
    }

    public void clickOnValueOfFirstSectionSelect(String value) {
        selectClickOnValue("#sectionList1", value);
    }

    public void selectSeatOfFirstTicketList(String seatToSelect) {
        selectClickOnValue(TICKET_LIST_ID_1, seatToSelect);
    }

    public String getTicketIdOfSeatInFirstTicketSelect(String seat) {
        return getSelectOptionValue(TICKET_LIST_ID_1, seat);
    }

    public void waitUntilTicketSelectIsPopulated() {
        waitUntilSelectIsPopulated(TICKET_LIST_ID_1);
    }

    private String getSelectOptionValue(String selectId, String value) {
        return find(selectId).findFirst("option", withText().equalTo(value)).getValue();
    }

    private int getSelectSize(String selectId) {
        String selectClass = '.' + selectId.substring(1);
        return find(selectId).find(selectClass + "_option").size();
    }

    private void selectClickOnFirstIndexValue(String selectId) {
        find(selectId).find("option", 1).click();
    }

    private void selectClickOnValue(String selectId, String value) {
        find(selectId).findFirst("option", withText().equalTo(value)).click();
    }

    private void waitUntilSelectIsPopulated(String selectId) {
        selectId = selectId.substring(1);
        await().atMost(TIMEOUT).until('.' + selectId + "_option").hasSize().greaterThan(0);
    }

    private void waitUntilSelectSizeIs(String selectId, int newSize) {
        selectId = selectId.substring(1);
        await().atMost(TIMEOUT).until('.' + selectId + "_option").hasSize(newSize);
    }

    public int getTicketSelectSize() {
        return getSelectSize(TICKET_LIST_ID_1);
    }

    public void clickOnFirstIndexValueOfFirstTicketSelect() {
        selectClickOnFirstIndexValue(TICKET_LIST_ID_1);
    }

    public void waitUntilFirstTicketSelectSizeIs(int size) {
        waitUntilSelectSizeIs(TICKET_LIST_ID_1, size);
    }
}
