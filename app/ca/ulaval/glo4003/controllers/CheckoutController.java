package ca.ulaval.glo4003.controllers;

import ca.ulaval.glo4003.ConstantsManager;
import ca.ulaval.glo4003.Secured;
import ca.ulaval.glo4003.dataaccessobjects.UserDao;
import ca.ulaval.glo4003.exceptions.RecordNotFoundException;
import ca.ulaval.glo4003.models.Transaction;
import ca.ulaval.glo4003.models.User;
import ca.ulaval.glo4003.services.CheckoutService;
import com.google.inject.Inject;
import org.codehaus.jackson.node.ObjectNode;
import play.api.mvc.PlainResult;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

public class CheckoutController extends Controller {

    private final CheckoutService checkoutService;
    private final UserDao userDao;
    private final TicketsController ticketsController;

    @Inject
    public CheckoutController(TicketsController ticketsController, CheckoutService checkoutService, UserDao userDao) {
        this.checkoutService = checkoutService;
        this.userDao = userDao;
        this.ticketsController = ticketsController;
    }

    @Security.Authenticated(Secured.class)
    public Result index() {
        try {
            String userEmail = session().get(ConstantsManager.COOKIE_SESSION_FIELD_NAME);
            User user = userDao.findByEmail(userEmail);
            Transaction transaction = checkoutService.startNewTransaction(user);

            Result ticketsResult = ticketsController.checkout();
            if (((PlainResult)ticketsResult.getWrappedResult()).header().status() != OK) {
                transaction.fail();
                return ticketsResult;
            }

            checkoutService.fulfillTransaction(transaction);

            ObjectNode result = Json.newObject();
            result.put(ConstantsManager.TRANSACTION_ID_FIELD_NAME, transaction.getId());

            return ok(result);
        } catch (RecordNotFoundException e) {
            return notFound();
        }
    }
}

