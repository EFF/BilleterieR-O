define(['app'], function (app) {
    app.controller('EventsController', ['$scope', '$http', function ($scope, $http) {
        $scope.events = null;
        $scope.filters = {};
        $scope.isLoading = false;

        $http.get('api/facets').success(function (facets) {
            $scope.facets = facets;
        });

        var apiCallSuccessCallback = function (results) {
            $scope.events = results;
            $scope.isLoading = false;
        };

        var apiCallErrorCallback = function (err) {
            //TODO emit error event and handle it in a directive
            $scope.events = [];
            $scope.isLoading = false;
        };

        var apiCall = function () {
            $scope.isLoading = true;
            var url = '/api/events';
            var nbFilters = 0;

            for (var filterName in $scope.filters) {
                var filter = $scope.filters[filterName];

                if (filter) {
                    url += (nbFilters == 0) ? '?' : '&';
                    url += filterName + '=' + filter;
                    nbFilters++;
                }
            }

            $http.get(url)
                .success(apiCallSuccessCallback)
                .error(apiCallErrorCallback);
        }

        $scope.$watch('filters', function (newValue, oldValue) {
            apiCall();
        }, true);

        apiCall();
    }]);

    app.controller('EventController', ['$scope', '$http', '$routeParams', 'Cart', 'FlashMessage', function ($scope, $http, $routeParams, Cart, FlashMessage) {
        var eventId = $routeParams.eventId;
        $scope.event = null;

        $scope.addToCart = function (quantity, category) {
            Cart.addItem(quantity, category, $scope.event);
            FlashMessage.send("success", "L'item a été ajouté au panier");
        }

        var apiCallSuccessCallback = function (result) {
            $scope.event = result
        }

        var apiCallErrorCallback = function (err) {
            //TODO emit error event and handle it in a directive
            $scope.event = null;
        }

        $http.get('/api/events/' + eventId)
            .success(apiCallSuccessCallback)
            .error(apiCallErrorCallback);
    }]);

    app.controller('CartController', ['$scope', 'FlashMessage', 'Cart', '$location', 'Login',
        function ($scope, FlashMessage, Cart, $location, Login) {
            $scope.selectAll = true;
            $scope.cart = Cart.getItems();
            $scope.getTotalSelectedQuantity = Cart.getTotalSelectedQuantity;
            $scope.getTotalPrice = Cart.getTotalPrice;
            $scope.removeItem = Cart.removeItem;
            $scope.removeAllItem = Cart.removeAllItem;
            $scope.validCards = ['Vasi', 'Mistercard', 'AmericanExpresso'];
            $scope.monthOfYear = ['01', '02', '03', '04', '05', '06', '07', '08', '09', '10', '11', '12'];
            $scope.expirationYears = [];

            var currentDate = new Date();
            for (var i = 0; i < 5; i++) {
                $scope.expirationYears.push(currentDate.getFullYear() + i);
            }

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
                    FlashMessage.send('warning', 'La sélection d\'achat est vide');
                }
                else if(!Login.isLoggedIn){
                    notifyUserToLogin();
                }
                else if (window.confirm("Confirmez-vous le paiment de " + $scope.getTotalPrice() + "$ ?")) {
                    Cart.checkout(checkoutSuccess, checkoutError);
                }
            }

            var notifyUserToLogin = function () {
                window.alert('Vous devez vous connecter avant de procéder au paiement');
            }

            var checkoutSuccess = function () {
                FlashMessage.send("success", "La transaction a été complétée");
                $location.path("/thanks");
            }

            var checkoutError = function (error, status) {
                if (status === 401) {
                    notifyUserToLogin();
                }
                else {
                    FlashMessage.send("error", error);
                }
            }
        }]);

    app.controller('LoginController', ['$scope', '$location', 'Login', 'FlashMessage',
        function ($scope, $location, Login, FlashMessage) {

            var loginSuccess = function () {
                FlashMessage.send("success", "Connexion à votre compte réussie!");
                $location.path("/events");
            };

            var loginFailed = function () {
                FlashMessage.send("error", "Connexion échouée.");
            };

            $scope.login = function () {
                Login.login($scope.username, $scope.password, loginSuccess, loginFailed);
            };

        }]);

});
