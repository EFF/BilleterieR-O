package ca.ulaval.glo4003.acceptances.pages;


import org.openqa.selenium.WebDriver;

public class TicketPage extends BaseFluentPage {
    private long ticketId = 0;

    public TicketPage(WebDriver driver, Long id) {
        super(driver);
        ticketId = id;
    }

    @Override
    public String getUrl() {
        return BASE_URL + "tickets/" + ticketId;
    }

    public void setTicketId(long id){
        this.ticketId = id;
    }

    public boolean isSeat(String seat) {
        return findFirst("#seat").getText().equals(seat);
    }
}
