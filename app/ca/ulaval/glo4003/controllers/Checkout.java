package ca.ulaval.glo4003.controllers;

import ca.ulaval.glo4003.ConstantsManager;
import ca.ulaval.glo4003.Secured;
import ca.ulaval.glo4003.dataaccessobjects.EventDao;
import ca.ulaval.glo4003.dataaccessobjects.UserDao;
import ca.ulaval.glo4003.exceptions.MaximumExceededException;
import ca.ulaval.glo4003.exceptions.RecordNotFoundException;
import ca.ulaval.glo4003.models.Transaction;
import ca.ulaval.glo4003.services.CheckoutService;
import com.google.inject.Inject;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import java.util.Iterator;

public class Checkout extends Controller {

    private final EventDao eventDao;
    private final CheckoutService checkoutService;
    private final UserDao userDao;

    @Inject
    public Checkout(EventDao eventDao, CheckoutService checkoutService, UserDao userDao) {
        this.eventDao = eventDao;
        this.checkoutService = checkoutService;
        this.userDao = userDao;
    }

    @Security.Authenticated(Secured.class)
    public Result index() throws RecordNotFoundException {
        JsonNode items = request().body().asJson();
        Iterator<JsonNode> jsonNodeIterator = items.iterator();

        String userEmail = session().get(ConstantsManager.COOKIE_SESSION_FIELD_NAME);
        Transaction transaction = this.checkoutService.startNewTransaction(userDao.findByEmail(userEmail));

        try {
            while (jsonNodeIterator.hasNext()) {
                JsonNode item = jsonNodeIterator.next();

                Long eventId = item.get(ConstantsManager.EVENT_ID_FIELD_NAME).asLong();
                Long categoryId = item.get(ConstantsManager.CATEGORY_ID_FIELD_NAME).asLong();
                int quantity = item.get(ConstantsManager.QUANTITY_FIELD_NAME).asInt();

                try {
                    eventDao.decrementEventCategoryNumberOfTickets(eventId, categoryId, quantity);
                } catch (RecordNotFoundException e) {
                    transaction.fail();
                    return notFound();
                } catch (MaximumExceededException e) {
                    transaction.fail();
                    return badRequest("Il n'y a pas assez de billets disponibles dans la catégorie" + categoryId.toString());
                }
            }

            this.checkoutService.fulfillTransaction(transaction);
        } catch (Exception e) {
            transaction.fail();
            return internalServerError("Unexpected error while checking out.");
        }

        ObjectNode result = Json.newObject();

        result.put(ConstantsManager.TRANSACTION_ID_FIELD_NAME, transaction.getId());

        return ok(result);
    }
}
