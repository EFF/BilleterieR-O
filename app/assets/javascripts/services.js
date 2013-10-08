define(['app'], function (app) {
    app.factory('Cart', ['$cookieStore', '$http', function ($cookieStore, $http) {
        var exports = {};
        var cart = $cookieStore.get('cart');
        if (!cart) {
            cart = [];
        }

        function updateCartCookie(cart) {
            $cookieStore.put('cart', cart)
        }

        exports.addItem = function (quantity, category, event) {
            var item = {
                quantity: quantity,
                category: category,
                event: event,
                selected: true
            }
            cart.push(item);
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

        function getCheckoutlist() {
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

        function removeAllSelectedItems(index, cart) {
            if (cart[index].selected) {
                cart.splice(index, 1);
            } else index++;

            if (index >= cart.length) {
                updateCartCookie(cart);
                return 0;
            }
            removeAllSelectedItems(index, cart);
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

    app.factory('FlashMessage', ['$rootScope', function ($rootScope) {
        var exports = {};

        function typeAsString(type) {
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
