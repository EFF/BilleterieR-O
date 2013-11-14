define(['app'], function (app) {
    app.factory('Cart', ['$cookieStore', '$http', 'FlashMessage', function ($cookieStore, $http, FlashMessage) {
        var exports = {};
        $cookieStore.put('cart', [])
        var cart = $cookieStore.get('cart');
        if (!cart) {
            cart = [];
        }

        var updateCartCookie = function (cart) {
            $cookieStore.put('cart', cart)
        };

        var getItemByTicketId = function (ticketId) {
            for (var index in cart) {
                var item = cart[index];

                if (item.ticket && item.ticket.id == ticketId) {
                    return item;
                }
            }
            return null;
        };

        var getItemByEventIdAndCategoryId = function (eventId, categoryId) {
            for (var index in cart) {
                var item = cart[index];

                if (item.event.id == eventId && item.category.id == categoryId) {
                    return item;
                }
            }
            return null;
        };

        var getCheckoutList = function () {
            var checkoutList = [];
            for (var index in cart) {
                var item = cart[index];
                if (item.selected) {
                    var checkoutItem = {
                        eventId: item.event.id,
                        categoryId: item.category.id,
                        tickets: item.tickets,
                        quantity: item.quantity
                    };
                    checkoutList.push(checkoutItem);
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

        exports.addItem = function (ticket, category, event) {
            var item = {
                quantity: 1,
                newQuantity: 1,
                category: category,
                tickets: [ticket],
                event: event,
                selected: true
            }

            var existingItem = getItemByTicketId(ticket.id);
            if (existingItem) {
                console.log("Throw error");
            } else {
                var url = '/api/tickets/reserve/' + ticket.id;
                $http.post(url)
                    .success(function () {
                        cart.push(item);
                        updateCartCookie(cart);
                        FlashMessage.send("success", "L'item a été ajouté au panier");
                    })
                    .error(function() {
                        FlashMessage.send('error', 'L\'item n\'a pu être ajouté au panier. Une erreur est survenue.')
                    });
            }
        };

        exports.addItems = function (tickets, category, event) {
            var item = {
                quantity: tickets.length,
                newQuantity: tickets.length,
                category: category,
                tickets: tickets,
                event: event,
                selected: true
            }

            var existingItem = getItemByEventIdAndCategoryId(event.id, category.id);

            var url = '/api/tickets/reserve/';
            for (var i in tickets) {
                url += tickets[i].id;
                if (i < tickets.length - 1)
                    url += ',';
            }
            $http.post(url)
                .success(function () {
                    if (existingItem) {
                        existingItem.tickets.concat(tickets);
                        existingItem.quantity += tickets.length;
                        existingItem.newQuantity += tickets.length;
                    } else {
                        cart.push(item);
                    }
                    updateCartCookie(cart);
                    FlashMessage.send("success", "Les billets ont été ajoutés au panier.");
                })
                .error(function() {
                    FlashMessage.send('error', 'Les billets n\'ont pu être ajoutés au panier. Une erreur est survenue.')
                });
        };

        exports.removeItem = function (index) {
            var url = '/api/tickets/free/';

            for (var i in cart[index].tickets) {
                url += cart[index].tickets[i].id;
                if (i < cart[index].tickets.length - 1)
                    url += ',';
            }

            $http.post(url)
                .success(function () {
                    cart.splice(index, 1);
                    updateCartCookie(cart);
                })
                .error(function () {
                    FlashMessage.send('error', 'L\'item n\'a pu être retiré du panier. Une erreur est survenue.');
                });
        };

        exports.removeAllItem = function () {
            var url = '/api/tickets/free/';
            for (var index in cart) {
                for (var i in cart[index].tickets) {
                    url += cart[index].tickets[i].id;
                    if (i < cart[index].tickets.length - 1)
                        url += ',';
                }
            }

            $http.post(url)
                .success(function () {
                    cart.splice(0, cart.length);
                    updateCartCookie(cart);
                })
                .error(function () {
                    FlashMessage.send('error', 'Le panier n\'a pu être vidé. Une erreur est survenue.');
                });
        };

        exports.getItems = function () {
            return cart;
        };

        exports.isSelectionEmpty = function () {
            for (key in cart) {
                if (cart[key].selected) return false;
            }
            return true;
        };

        exports.getTotalQuantity = function () {
            return cart.reduce(function (a, item) {
                return a + item.quantity;
            }, 0);
        };

        exports.getTotalSelectedQuantity = function () {
            return cart.reduce(function (a, item) {
                if (item.selected) {
                    a += item.quantity;
                }
                return a;
            }, 0);
        };

        exports.getTotalPrice = function () {
            return cart.reduce(function (a, item) {
                if (item.selected) {
                    a += item.quantity * item.category.price;
                }
                return a;
            }, 0);
        };

        exports.setItemSelected = function (key, value) {
            cart[key].selected = value;
            updateCartCookie(cart);
        };

        exports.checkout = function (successCallback, errorCallback) {
            var overrideSuccessCallback = function() {
                removeAllSelectedItems(0, cart);
                successCallback();
            }
            var itemsToCheckout = getCheckoutList();
            var url = '/api/tickets/checkout/';
            for (var index in itemsToCheckout) {
                for (var i in itemsToCheckout[index].tickets) {
                    url += itemsToCheckout[index].tickets[i].id;
                    if (index < itemsToCheckout.length - 1)
                        url += ',';
                }
            }
            $http.post(url)
                .success(overrideSuccessCallback)
                .error(errorCallback);
        };

        exports.updateItemQuantity = function(item) {
            //TODO: Find first 'newQuantity' tickets of the category and reserve them.
            var quantityToReserve = item.newQuantity - item.quantity;
            if (quantityToReserve == 0) {
                return;
            }
            var url = '/api/tickets?eventId='
                + item.event.id + '&categoryId='
                + item.category.id + '&states=AVAILABLE,RESALE'
                + '&quantity=' + quantityToReserve;
            $http.get(url)
                .success(function (tickets) {
                    if (tickets.length > 0) {
                        exports.addItems(tickets, tickets[0].category, tickets[0].event);
                    }
                })
                .error(function() {
                    FlashMessage.send('error', 'Le nombre de billets ajoutés au panier excède le nombre de billets restants.');
                });
        };

        return exports;
    }]);

    app.factory('Login', ['$http', function($http) {
        var exports = {};

        exports.isLoggedIn = false;
        exports.username;

        exports.login = function (username, password, successCallback, errorCallback){
            var data = {username: username, password: password};

            $http.post('/login', data, {})
                .success(function() {
                    setCredentials(true, username);
                    successCallback();
                })
                .error(errorCallback);
        };

        exports.logout = function() {
            $http.post('/logout', {}, {})
                .success(function() {
                    setCredentials(false, null);
                });
        };

        var checkAuthenticationState = function() {
            $http.get('/login')
                .success(function(data) {
                    setCredentials(data.authenticated, data.username);
                });
        };

        var setCredentials = function(isLoggedIn, authenticated) {
            exports.isLoggedIn = isLoggedIn;
            exports.username = authenticated;
        };

        checkAuthenticationState();

        return exports;
    }]);

    app.factory('FlashMessage', ['$rootScope', function ($rootScope) {
        var exports = {};

        var typeAsString = function (type) {
            if (type == "error") return "Erreur";
            else if (type == "info") return "Informations";
            else if (type == "warning") return "Attention";
            else return "Succès";
        }

        exports.send = function (type, message) {
            var message = {
                type: type,
                title: typeAsString(type),
                content: message
            };

            $rootScope.$broadcast('messageEvent', message);
        };

        return exports;
    }]);
});
