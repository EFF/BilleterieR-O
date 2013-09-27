package ca.ulaval.glo4003.acceptances.pages;

import org.fluentlenium.core.domain.FluentList;
import org.fluentlenium.core.domain.FluentWebElement;
import org.openqa.selenium.WebDriver;

public class EventsPage extends BaseFluentPage {

    public EventsPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public String getUrl() {
        return BASE_URL + "events";
    }

    @Override
    public void isAt() {
        await().atMost(TIMEOUT).until("#events").isPresent();
    }

    public void selectSport(String value) {
        findFirst("#sportFilter").find("option[value='" + value + "']").click();
    }

    public void selectGender(String value) {
        findFirst("#genderFilter").find("option[value='" + value + "']").click();
    }

    public void selectDateStart(String value) {
        fill("#dateStartFilter").with(value);
    }

    public void selectDateEnd(String value) {
        fill("#dateEndFilter").with(value);
    }

    public void waitUntilEventsHasSize(int count) {
        await().atMost(TIMEOUT).until(".event").hasSize(count);
    }

    public FluentList<FluentWebElement> getEvents() {
        return find(".event");
    }

    public FluentWebElement getEmptyAlert() {
        return findFirst("#emptyAlert");
    }

    public boolean eventHas(int i, String expectedSport, String expectedGender, Integer expectedNumberOfTickets) {
        FluentWebElement event = getEvents().get(i);
        String sport = event.findFirst(".sport").getText();
        String gender = event.findFirst(".gender").getText();
        Integer numberOfTickets = Integer.parseInt(event.find(".numberOfTickets").getText());

        return (expectedSport.equals(sport) && expectedGender.equals(gender) && expectedNumberOfTickets.equals
                (numberOfTickets));
    }
}
