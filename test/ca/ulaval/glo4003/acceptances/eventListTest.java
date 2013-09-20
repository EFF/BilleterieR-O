package ca.ulaval.glo4003.acceptances;

import ca.ulaval.glo4003.TestGlobal;
import org.fluentlenium.core.domain.FluentList;
import org.fluentlenium.core.domain.FluentWebElement;
import org.junit.Test;
import play.libs.F;
import play.test.TestBrowser;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.*;

public class EventListTest {
    @Test
    public void displayEventListWithPricesAndCategories() {
        running(testServer(3333, fakeApplication(new TestGlobal())), FIREFOX, new F.Callback<TestBrowser>() {
            public void invoke(TestBrowser browser) {
                browser.goTo("http://localhost:3333/#!/events");
                browser.await().atMost(5000).until(".event").isPresent();
                FluentList<FluentWebElement> events = browser.find(".event");

                assertThat(events.size()).isEqualTo(2);
                assertThat(events.get(0).getText()).contains("Soccer");
                assertThat(events.get(0).getText()).contains("Masculin");
                assertThat(events.get(1).getText()).contains("Soccer");
                assertThat(events.get(1).getText()).contains("Féminin");

                assertThat(events.get(0).findFirst(".numberOfTickets").getText()).isEqualTo("1320");
                assertThat(events.get(1).findFirst(".numberOfTickets").getText()).isEqualTo("1320");
            }
        });
    }
}
