package ca.ulaval.glo4003.helpers;

import ca.ulaval.glo4003.exceptions.InvalidJsonException;
import org.codehaus.jackson.JsonNode;

public class JsonExpectationsHelper {

    public static void expectFilledString(JsonNode json, String... keys) throws InvalidJsonException {
        for(String key : keys) {
            String value = json.path(key).getTextValue();

            if(value == null || value.isEmpty()) {
                throw new InvalidJsonException();
            }
        }
    }

}
