package ca.ulaval.glo4003.controllers;

import ca.ulaval.glo4003.ConstantsManager;
import ca.ulaval.glo4003.actions.SecureAction;
import ca.ulaval.glo4003.exceptions.RecordNotFoundException;
import ca.ulaval.glo4003.exceptions.UpdateTicketStateUnauthorizedException;
import ca.ulaval.glo4003.interactors.CheckoutInteractor;
import ca.ulaval.glo4003.interactors.UsersInteractor;
import ca.ulaval.glo4003.models.Transaction;
import ca.ulaval.glo4003.models.TransactionState;
import ca.ulaval.glo4003.models.User;
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

    private final CheckoutInteractor checkoutInteractor;
    private final UsersInteractor usersInteractor;

    @Inject
    public CheckoutController(CheckoutInteractor checkoutInteractor, UsersInteractor usersInteractor) {
        this.checkoutInteractor = checkoutInteractor;
        this.usersInteractor = usersInteractor;
    }

    @Security.Authenticated(SecureAction.class)
    public Result index() {
        String userEmail = request().username();
        List<Long> ticketIds = extractTicketsIdsFromRequest();

        User user;
        try {
            user = usersInteractor.getByEmail(userEmail);
        } catch (RecordNotFoundException ignored) {
            return notFound();
        }

        Transaction transaction = checkoutInteractor.executeTransaction(user, ticketIds);

        if(transaction.getState() == TransactionState.Fulfilled || transaction.getState() == TransactionState.Failed){
            return ok(Json.toJson(transaction));
        }
        else{
            return internalServerError();
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

