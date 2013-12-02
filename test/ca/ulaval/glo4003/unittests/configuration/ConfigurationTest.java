package ca.ulaval.glo4003.unittests.configuration;

import com.typesafe.config.ConfigFactory;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ConfigurationTest {

    @Test
    public void SMTPServerIsDefinedInConfiguration() {
        String host = ConfigFactory.load().getString("smtp.host");
        int port = ConfigFactory.load().getInt("smtp.port");

        assertEquals(host, "localhost");
        assertEquals(25, port);
    }

}
