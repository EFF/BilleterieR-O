define(['app'], function (app) {
    app.factory('Cart', ['$cookieStore', function ($cookieStore) {
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
                event: event
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
