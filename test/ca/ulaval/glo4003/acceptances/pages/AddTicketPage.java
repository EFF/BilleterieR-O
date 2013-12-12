package ca.ulaval.glo4003.acceptances.pages;

import org.openqa.selenium.WebDriver;

import static org.fluentlenium.core.filter.FilterConstructor.withId;
import static org.fluentlenium.core.filter.FilterConstructor.withText;

public class AddTicketPage extends BaseFluentPage {
    private int eventId = 0;

    public AddTicketPage(WebDriver driver, int eventId) {
        super(driver);
        this.eventId = eventId;
    }

    @Override
    public String getUrl() {
        return BASE_URL + "events/" + eventId + "/addTicket";
    }

    @Override
    public void isAt() {
        await().atMost(TIMEOUT).until("#categoriesInput option").hasSize().greaterThan(0);
    }

    public void createGeneralTickets(int quantity) {
        find("#categoriesInput").find("option", withText().equalTo("0")).click();
        await().atMost(TIMEOUT).until("#quantityInput").isPresent();
        fill("input", withId().equalTo("quantityInput")).with(String.valueOf(quantity));
        await().atMost(TIMEOUT).until("#login-button").areEnabled();
        find("#login-button").click();
    }

    public void createSeatTickets(int seatNumber) {
        find("#categoriesInput").find("option", withText().equalTo("1")).click();
        await().atMost(TIMEOUT).until("#sectionInput option").hasSize().greaterThan(0);
        find("#sectionInput").find("option", withText().equalTo("Niveau 100")).click();
        fill("input", withId().equalTo("seatInput")).with(String.valueOf(seatNumber));
        await().atMost(TIMEOUT).until("#login-button").areEnabled();
        find("#login-button").click();
    }

    public void waitForErrorMessage() {
        await().atMost(TIMEOUT).until(".alert.alert-danger").isPresent();
    }
}
