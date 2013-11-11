define(['app'], function (app) {
    app.factory('Cart', ['$cookieStore', '$http', function ($cookieStore, $http) {
        var exports = {};
        var cart = $cookieStore.get('cart');
        if (!cart) {
            cart = [];
        }

        exports.transactionId = "TRANSACTION ID";

        var updateCartCookie = function (cart) {
            $cookieStore.put('cart', cart)
        }

        var getItemByEventAndCategory = function (event, category) {
            for (var index in cart) {
                var item = cart[index];

                if (item.event.id == event.id && item.category.id == category.id) {
                    return item;
                }
            }
            return null;
        }

        exports.addItem = function (quantity, category, event) {
            var item = {
                quantity: quantity,
                category: category,
                event: event,
                selected: true
            }

            var existingItem = getItemByEventAndCategory(event, category);

            if (existingItem) {
                existingItem.quantity += quantity;
            } else {
                cart.push(item);
            }

            updateCartCookie(cart);
        }

        exports.removeItem = function (index) {
            cart.splice(index, 1);
            updateCartCookie(cart);
        }

        exports.removeAllItem = function () {
            cart.splice(0, cart.length);
            updateCartCookie(cart);
        }

        exports.getItems = function () {
            return cart;
        }

        exports.isSelectionEmpty = function () {
            for (key in cart) {
                if (cart[key].selected) return false;
            }
            return true;
        }

        exports.getTotalQuantity = function () {
            return cart.reduce(function (a, item) {
                return a + item.quantity;
            }, 0);
        }

        exports.getTotalSelectedQuantity = function () {
            return cart.reduce(function (a, item) {
                if (item.selected) {
                    a += item.quantity;
                }
                return a;
            }, 0);
        }

        exports.getTotalPrice = function () {
            return cart.reduce(function (a, item) {
                if (item.selected) {
                    a += item.quantity * item.category.price;
                }
                return a;
            }, 0);
        }

        exports.setItemSelected = function (key, value) {
            cart[key].selected = value;
            updateCartCookie(cart);
        }

        var getCheckoutlist = function () {
            var checkoutList = [];
            for (var index in cart) {
                var item = cart[index];
                if (item.selected) {
                    var checkoutItem = {
                        eventId: item.event.id,
                        categoryId: item.category.id,
                        quantity: item.quantity
                    }
                    checkoutList.push(checkoutItem);
                }
            }
            return checkoutList;
        }

        var removeAllSelectedItems = function (index, cart) {
            if (cart[index].selected) {
                cart.splice(index, 1);
            } else index++;

            if (index >= cart.length) {
                updateCartCookie(cart);
                return 0;
            }
            return removeAllSelectedItems(index, cart);
        }

        exports.checkout = function (successCallback, errorCallback) {
            var itemsToCheckout = getCheckoutlist()
            removeAllSelectedItems(0, cart);
            var config = {
                method: 'POST',
                url: '/api/checkout',
                data: itemsToCheckout
            }
            $http(config)
                .success(successCallback)
                .error(errorCallback)
        }

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
            }

            $rootScope.$broadcast('messageEvent', message);
        }

        return exports;
    }]);
});
