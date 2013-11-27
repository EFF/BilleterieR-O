define(['./module'], function (CartModule) {
    CartModule.controller('CartController', ['$scope', 'FlashMessage', 'Cart', '$location', 'LoginService',
        function ($scope, FlashMessage, Cart, $location, LoginService) {
            $scope.selectAll = true;
            $scope.cart = Cart.getItems();
            $scope.getTotalSelectedQuantity = Cart.getTotalSelectedQuantity;
            $scope.getTotalPrice = Cart.getTotalPrice;
            $scope.removeItem = Cart.removeItem;
            $scope.removeAllItem = Cart.removeAllItem;
            $scope.validCards = ['Vasi', 'Mistercard', 'AmericanExpresso'];
            $scope.monthOfYear = ['01', '02', '03', '04', '05', '06', '07', '08', '09', '10', '11', '12'];
            $scope.expirationYears = [];

            $scope.updateItemQuantity = function (index) {
                var item = $scope.cart[index];
                var maxQuantity = item.category.numberOfTickets;
                var deltaQuantity = item.desiredQuantity - item.reservedQuantity;
                if (item.desiredQuantity == 0) {
                    Cart.removeItem(index);
                } else {
                    if (item.desiredQuantity > maxQuantity) {
                        FlashMessage.send("warning", "Le nombre de billet maximum est de " + maxQuantity.toString());
                        deltaQuantity = maxQuantity - item.reservedQuantity;
                    }
                    Cart.updateItemQuantity(index, deltaQuantity);
                }
            };

            $scope.updateSelectAll = function () {
                $scope.selectAll = false;
            };

            $scope.toggleAll = function (value) {
                for (var key in $scope.cart) {
                    Cart.setItemSelected(key, value);
                }
            };

            $scope.checkout = function () {
                if (Cart.isSelectionEmpty()) {
                    FlashMessage.send('warning', "La sélection d'achat est vide");
                }
                else if (!LoginService.isLoggedIn()) {
                    notifyUserToLogin();
                }
                else if (window.confirm("Confirmez-vous le paiment de " + $scope.getTotalPrice() + "$ ?")) {
                    Cart.checkout(checkoutSuccess, checkoutError);
                }
            };

            $scope.$on('logoutEvent', function(){
                Cart.removeAllItem();
            });

            var currentDate = new Date();
            var followingYears = 5;
            for (var i = 0; i < followingYears; i++) {
                $scope.expirationYears.push(currentDate.getFullYear() + i);
            }

            var notifyUserToLogin = function () {
                FlashMessage.send('info', 'Vous devez vous connecter avant de procéder au paiement');
            };

            var checkoutSuccess = function (data) {
                Cart.transactionId = data.transactionId;
                FlashMessage.send("success", "La transaction a été complétée");
                $location.path("/thanks");
            };

            var checkoutError = function (error, status) {
                if (status === 401) {
                    notifyUserToLogin();
                }
                else {
                    FlashMessage.send("error", error);
                }
            };
        }]);
});
