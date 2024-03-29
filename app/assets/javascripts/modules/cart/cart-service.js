define(['./module'], function (CartModule) {
    CartModule.factory('Cart', ['$cookieStore', 'FlashMessage', '$q', 'TicketService',
        function ($cookieStore, FlashMessage, $q, TicketService) {
            var exports = {
                transactionId: null
            };

            var cart = $cookieStore.get('cart');
            if (!cart) {
                cart = [];
            }

            var checkoutTickets = function (tickets, successCallback, errorCallback) {
                TicketService.checkoutTickets(tickets).then(successCallback, errorCallback);
            };

            var freeTickets = function (tickets, successCallback, errorCallback) {
                TicketService.freeTickets(tickets).then(successCallback, errorCallback);
            };

            var reserveTickets = function (tickets, successCallback, errorCallback) {
                TicketService.reserveTickets(tickets).then(successCallback, errorCallback);
            };

            var updateCartCookie = function (cart) {
                $cookieStore.put('cart', cart)
            };

            var getItemByTicketId = function (ticketId) {
                for (var index in cart) {
                    if (cart.hasOwnProperty(index)) {
                        var item = cart[index];
                        for (var ticketIndex in item.tickets) {
                            if (item.tickets.hasOwnProperty(ticketIndex)) {
                                if (item.tickets[ticketIndex].id == ticketId) {
                                    return item;
                                }
                            }
                        }
                    }
                }
                return null;
            };

            var getItemByEventIdAndCategoryId = function (eventId, categoryId) {
                for (var index in cart) {
                    if (cart.hasOwnProperty(index)) {
                        var item = cart[index];

                        if (item.event.id == eventId && item.category.id == categoryId) {
                            return item;
                        }
                    }
                }
                return null;
            };

            var getCheckoutList = function () {
                var checkoutList = [];
                for (var index in cart) {
                    if (cart.hasOwnProperty(index)) {
                        var item = cart[index];
                        if (item.selected) {
                            var checkoutItem = {
                                eventId: item.event.id,
                                categoryId: item.category.id,
                                tickets: item.tickets,
                                quantity: item.reservedQuantity
                            };
                            checkoutList.push(checkoutItem);
                        }
                    }
                }
                return checkoutList;
            };

            var removeAllSelectedItems = function (index, cart) {
                if (cart[index].selected) {
                    cart.splice(index, 1);
                } else index++;

                if (index >= cart.length) {
                    updateCartCookie(cart);
                    return 0;
                }
                return removeAllSelectedItems(index, cart);
            };

            exports.addItem = function (ticket, category, event, preSuccessCallback) {
                var item = {
                    reservedQuantity: 1,
                    desiredQuantity: 1,
                    category: category,
                    tickets: [ticket],
                    event: event,
                    selected: true
                };

                var existingItem = getItemByTicketId(ticket.id);
                if (existingItem) {
                    FlashMessage.send('error', 'Le billet existe déjà dans le panier.')
                } else {
                    var successCallback = function () {
                        preSuccessCallback();
                        cart.push(item);
                        updateCartCookie(cart);
                        FlashMessage.send("success", "L'item a été ajouté au panier");
                    };
                    var errorCallback = function () {
                        FlashMessage.send('error', "L'item n'a pu être ajouté au panier. Une erreur est survenue.");
                    };
                    reserveTickets([ticket], successCallback, errorCallback);
                }
            };

            exports.addItems = function (tickets, category, event, preSuccessCallback) {
                var item = {
                    reservedQuantity: tickets.length,
                    desiredQuantity: tickets.length,
                    category: category,
                    tickets: tickets,
                    event: event,
                    selected: true
                };

                var existingItem = getItemByEventIdAndCategoryId(event.id, category.id);

                var successCallback = function () {
                    preSuccessCallback();
                    if (existingItem) {
                        existingItem.tickets.concat(tickets);
                        existingItem.reservedQuantity += tickets.length;
                        existingItem.desiredQuantity += tickets.length;
                    } else {
                        cart.push(item);
                    }
                    updateCartCookie(cart);
                    FlashMessage.send("success", "Les billets ont été ajoutés au panier.");
                };
                var errorCallback = function () {
                    FlashMessage.send('error', "Les billets n'ont pu être ajoutés au panier. Une erreur est survenue.");
                };

                reserveTickets(tickets, successCallback, errorCallback);
            };

            exports.removeItem = function (index) {
                var successCallback = function () {
                    cart.splice(index, 1);
                    updateCartCookie(cart);
                };
                var errorCallback = function () {
                    FlashMessage.send('error', "L'item n'a pu être retiré du panier. Une erreur est survenue.");
                };

                freeTickets(cart[index].tickets, successCallback, errorCallback);
            };

            exports.removeAllItem = function () {
                var successCallback = function () {
                    cart.splice(0, cart.length);
                    updateCartCookie(cart);
                };
                var errorCallback = function () {
                    FlashMessage.send('error', "Le panier n'a pu être vidé. Une erreur est survenue.");
                };
                var tickets = [];
                for (var i in cart) {
                    tickets = tickets.concat(cart[i].tickets)
                }

                if (tickets.length > 0) {
                    freeTickets(tickets, successCallback, errorCallback);
                }
            };

            exports.getItems = function () {
                return cart;
            };

            exports.isSelectionEmpty = function () {
                for (var key in cart) {
                    if (cart.hasOwnProperty(key)) {
                        if (cart[key].selected) {
                            return false;
                        }
                    }
                }
                return true;
            };

            exports.getTotalQuantity = function () {
                return cart.reduce(function (a, item) {
                    return a + item.reservedQuantity;
                }, 0);
            };

            exports.getTotalSelectedQuantity = function () {
                return cart.reduce(function (a, item) {
                    if (item.selected) {
                        a += item.reservedQuantity;
                    }
                    return a;
                }, 0);
            };

            exports.getTotalPrice = function () {
                return cart.reduce(function (a, item) {
                    if (item.selected) {
                        a += item.reservedQuantity * item.category.price;
                    }
                    return a;
                }, 0);
            };

            exports.setItemSelected = function (key, value) {
                cart[key].selected = value;
                updateCartCookie(cart);
            };

            exports.checkout = function (successCallback, errorCallback) {
                var overrideSuccessCallback = function (data) {
                    removeAllSelectedItems(0, cart);
                    successCallback(data);
                };
                var itemsToCheckout = getCheckoutList();
                var tickets = [];
                for (var i in itemsToCheckout) {
                    tickets = tickets.concat(itemsToCheckout[i].tickets);
                }
                checkoutTickets(tickets, overrideSuccessCallback, errorCallback);
            };

            var reserveTicketsToItem = function (quantity, item) {
                var options = {
                    eventId: item.event.id,
                    categoryId: item.category.id,
                    states: 'AVAILABLE',
                    quantity: quantity
                };

                var onGetTicketsSuccess = function (tickets) {
                    var successCallback = function () {
                        item.tickets.concat(tickets);
                        item.reservedQuantity += tickets.length;
                        item.desiredQuantity = item.reservedQuantity;
                        updateCartCookie(cart);
                    };
                    var errorCallback = function () {
                        FlashMessage.send('error', 'Le nombre de billets ajoutés au panier excède le nombre de billets restants.');
                    };
                    reserveTickets(tickets, successCallback, errorCallback);
                };

                var onGetTicketsError = function () {
                    FlashMessage.send('error', 'Le nombre de billets ajoutés au panier excède le nombre de billets restants.');
                };

                TicketService.getTickets(options).then(onGetTicketsSuccess, onGetTicketsError);
            };

            var freeTicketsFromItem = function (quantity, item) {
                var tickets = [];
                var i = 0;

                while (i < quantity) {
                    tickets.push(item.tickets[i]);
                    i++;
                }

                var successCallback = function () {
                    item.tickets.splice(0, quantity);
                    item.reservedQuantity -= quantity;
                    item.desiredQuantity = item.reservedQuantity;
                    updateCartCookie(cart);
                };
                var errorCallback = function () {
                    FlashMessage.send('error', "Les billets n'ont pu être libérés.");
                };

                freeTickets(tickets, successCallback, errorCallback);
            };

            exports.updateItemQuantity = function (index, deltaQuantity) {
                var item = cart[index];
                if (deltaQuantity == 0) {
                    item.desiredQuantity = item.reservedQuantity;
                } else if (deltaQuantity > 0) {
                    reserveTicketsToItem(deltaQuantity, item);
                } else {
                    freeTicketsFromItem(-deltaQuantity, item);
                }
            };

            return exports;
        }]);
});
