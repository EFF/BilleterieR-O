package ca.ulaval.glo4003;

public class ConstantsManager {

    public final static String COOKIE_SESSION_FIELD_NAME = "email";
    public final static String COOKIE_ADMIN_FIELD_NAME = "admin";

    public final static String USER_AUTHENTICATED_FIELD_NAME = "authenticated";
    public final static String USERNAME_FIELD_NAME = "username";
    public final static String ACTUAL_PASSWORD_FIELD_NAME = "actualPassword";

    public final static String PASSWORD_FIELD_NAME = "password";
    public final static String QUERY_STRING_SPORT_PARAM_NAME = "sport";
    public final static String QUERY_STRING_DATE_START_PARAM_NAME = "dateStart";
    public final static String QUERY_STRING_DATE_END_PARAM_NAME = "dateEnd";
    public final static String QUERY_STRING_TEAM_PARAM_NAME = "team";
    public final static String QUERY_STRING_GENDER_PARAM_NAME = "gender";

    public static final String QUERY_STRING_STATE_PARAM_NAME = "states";
    public final static String EVENT_ID_FIELD_NAME = "eventId";

    public final static String CATEGORY_ID_FIELD_NAME = "categoryId";
    public final static String FACET_SPORT = "sport";

    public final static String FACET_GENDER = "gender";
    public final static String PERSISTENCE_DIRECTORY = "data";

    public final static String PERSISTENCE_FILE_EXTENSION = ".ser";
    public static final String TICKET_STATE_FIELD_NAME = "states";
    public static final String TICKET_IDS_FIELD_NAME = "ticketIds";


    public static final int TICKET_INVALID_SEAT_NUMBER = -1;

    public final static String CHECKOUT_CONFIRMATION_EMAIL = "Votre transaction c'est bien effectuée sur le système." +
            " Votre numéro de confirmation est le: ";
    public final static int SERVICE_OPERATION_TIMEOUT = 5000;
            ;
}
