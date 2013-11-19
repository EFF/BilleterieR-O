package ca.ulaval.glo4003.controllers;

import ca.ulaval.glo4003.ConstantsManager;
import ca.ulaval.glo4003.actions.SecureAction;
import ca.ulaval.glo4003.dataaccessobjects.UserDao;
import ca.ulaval.glo4003.exceptions.RecordNotFoundException;
import ca.ulaval.glo4003.interactors.TicketsInteractor;
import ca.ulaval.glo4003.models.Transaction;
import ca.ulaval.glo4003.models.User;
import ca.ulaval.glo4003.services.CheckoutService;
import com.google.inject.Inject;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import java.util.ArrayList;
import java.util.List;

public class CheckoutController extends Controller {

    private final CheckoutService checkoutService;
    private final UserDao userDao;
    private final TicketsInteractor ticketsInteractor;

    @Inject
    public CheckoutController(CheckoutService checkoutService, UserDao userDao, TicketsInteractor ticketsInteractor) {
        this.checkoutService = checkoutService;
        this.userDao = userDao;
        this.ticketsInteractor = ticketsInteractor;
    }

    @Security.Authenticated(SecureAction.class)
    public Result index() {
        try {
            String userEmail = session().get(ConstantsManager.COOKIE_SESSION_FIELD_NAME);
            User user = userDao.findByEmail(userEmail);
            Transaction transaction = checkoutService.startNewTransaction(user);

            List<Long> ticketsIds = extractTicketsIdsFromRequest();
            for (Long ticketId : ticketsIds) {
                //TODO catch error (once verification is done) then transaction.fail on error
                ticketsInteractor.buyATicket(ticketId);
            }

            checkoutService.fulfillTransaction(transaction);

            ObjectNode result = Json.newObject();
            result.put(ConstantsManager.TRANSACTION_ID_FIELD_NAME, transaction.getId());

            return ok(result);
        } catch (RecordNotFoundException e) {
            return notFound();
        }
    }

    private List<Long> extractTicketsIdsFromRequest() {
        JsonNode json = request().body().asJson();
        JsonNode node = json.get(ConstantsManager.TICKET_IDS_FIELD_NAME);

        List<Long> ids = new ArrayList<>();
        if (node.isArray()) {
            for (JsonNode current : node) {
                if (current.isLong()) {
                    ids.add(current.asLong());
                }
            }
        }

        return ids;
    }
}

