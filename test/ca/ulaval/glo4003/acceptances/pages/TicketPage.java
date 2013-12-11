package ca.ulaval.glo4003.acceptances.pages;


import org.openqa.selenium.WebDriver;

public class TicketPage extends BaseFluentPage {

    private long ticketId;
    private long eventId;

    public TicketPage(WebDriver driver, long ticketId, long eventId) {
        super(driver);
        this.ticketId = ticketId;
        this.eventId = eventId;
    }

    @Override
    public String getUrl() {
        return BASE_URL + "tickets/" + ticketId;
    }

    public String getEventUrl() {
        return BASE_URL + "events/" + eventId;
    }

    @Override
    public void isAt() {
        await().atMost(TIMEOUT).until("#event-" + eventId).hasAttribute("href", getEventUrl());
    }

    public void setTicketId(long id){
        this.ticketId = id;
    }

    public boolean isSeat(String seat) {
        return findFirst("#seat").getText().equals(seat);
    }

    public boolean isSection(String section) {
        return findFirst("#section").getText().equals(section);
    }

    public boolean requiredInfoAreDisplayed() {
        return findFirst("#seat").isDisplayed()
                && findFirst("#section").isDisplayed()
                && findFirst("#categoryId").isDisplayed()
                && findFirst("#homeTeamName").isDisplayed()
                && findFirst("#visitorTeamName").isDisplayed()
                && findFirst("#price").isDisplayed()
                && findFirst("#categoryType").isDisplayed();
    }

    public void clickEventDetailsButton() {
        findFirst("#event-" + eventId).click();
    }
}
