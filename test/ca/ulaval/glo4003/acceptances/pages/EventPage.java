package ca.ulaval.glo4003.acceptances.pages;

import org.fluentlenium.core.domain.FluentList;
import org.fluentlenium.core.domain.FluentWebElement;
import org.openqa.selenium.WebDriver;

import static org.fluentlenium.core.filter.FilterConstructor.withText;

public class EventPage extends BaseFluentPage {

    public static final String TICKET_SELECT_ID = "#ticketList";
    public static final String SECTION_SELECT_ID = "#sectionList";
    public static final int QUANTITY_OF_DEFAULT_VALUES_IN_TICKETS_SELECT = 1;
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
        await().atMost(TIMEOUT).until(".category").hasSize().greaterThan(0);
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

    public void addGeneralAdmissionsToCartForCategory(Integer categoryIndex, Integer quantity) {
        getCategoryQuantityElement(categoryIndex).text(quantity.toString());
        getCategoryAddButtonElement(categoryIndex).click();
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

    public FluentWebElement getCategoryQuantityElement(Integer categoryIndex) {
        return find(".category-quantity").get(categoryIndex);
    }

    public FluentWebElement getCategoryAddButtonElement(Integer categoryIndex) {
        return find(".category-add").get(categoryIndex);
    }

    public void selectSectionInSectionListForCategory(int categoryId, String value) {
        selectClickOnValue(SECTION_SELECT_ID + categoryId, value);
    }

    public void selectSeatInTicketsListForCategory(int categoryId, String seatToSelect) {
        selectClickOnValue(TICKET_SELECT_ID + categoryId, seatToSelect);
    }

    public String getTicketIdOfSeatInTicketsListForCategory(int categoryId, String seat) {
        return getSelectOptionValue(TICKET_SELECT_ID + categoryId, seat);
    }

    public void waitUntilTicketsListIsPopulated(int categoryId) {
        waitUntilSelectIsPopulated(TICKET_SELECT_ID + categoryId);
    }

    public int getTicketsListSizeForCategory(int categoryId) {
        return getSelectSize(TICKET_SELECT_ID + categoryId) - QUANTITY_OF_DEFAULT_VALUES_IN_TICKETS_SELECT;
    }

    public void clickOnFirstIndexValueOfTicketsListForCategory(int categoryId) {
        selectClickOnFirstIndexValue(TICKET_SELECT_ID + categoryId);
    }

    public void waitUntilTicketsListForCategoryHasSize(int categoryId, int size) {
        waitUntilSelectSizeIs(TICKET_SELECT_ID + categoryId, size + QUANTITY_OF_DEFAULT_VALUES_IN_TICKETS_SELECT);
    }

    private String getSelectOptionValue(String selectId, String value) {
        return find(selectId).findFirst("option", withText().equalTo(value)).getValue();
    }

    private int getSelectSize(String selectId) {
        return find(selectId).find("option").size();
    }

    private void selectClickOnFirstIndexValue(String selectId) {
        find(selectId).find("option", 1).click();
    }

    private void selectClickOnValue(String selectId, String value) {
        find(selectId).findFirst("option", withText().equalTo(value)).click();
    }

    private void waitUntilSelectIsPopulated(String selectId) {
        await().atMost(TIMEOUT).until(selectId + " option").hasSize().greaterThan(QUANTITY_OF_DEFAULT_VALUES_IN_TICKETS_SELECT);
    }

    private void waitUntilSelectSizeIs(String selectId, int newSize) {
        await().atMost(TIMEOUT).until(selectId + " option").hasSize(newSize);
    }
}
