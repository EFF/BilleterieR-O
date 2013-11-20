package ca.ulaval.glo4003.acceptances.pages;


import org.openqa.selenium.WebDriver;

import static org.fluentlenium.core.filter.FilterConstructor.withText;

public class TicketPage extends BaseFluentPage {
    private long ticketId = 0;

    public TicketPage(WebDriver driver, int id) {
        super(driver);
        ticketId = id;
    }

    @Override
    public String getUrl() {
        return BASE_URL + "tickets/" + ticketId;
    }

    @Override
    public void isAt() {
        await().atMost(TIMEOUT).until(".table").isPresent();
    }

    public boolean isTdValueExists(String value) {
        return find("td", withText().contains(value)).size() > 0;
    }

    public void setTicketId(int id){
        this.ticketId = id;
    }
}
