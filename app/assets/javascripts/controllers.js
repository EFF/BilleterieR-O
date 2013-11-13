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

        var apiCallErrorCallback = function () {
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

        $scope.$watch('filters', function () {
            apiCall();
        }, true);

        apiCall();
    }]);

    app.controller('EventController', ['$scope', '$http', '$routeParams', 'Cart', 'FlashMessage', function ($scope, $http, $routeParams, Cart, FlashMessage) {
        var eventId = $routeParams.eventId;
        $scope.event = null;

        $scope.addToCart = function (quantity, category) {
            if (quantity > category.numberOfTickets) {
                FlashMessage.send('error', 'Le nombre de billets ajoutés au panier excède le nombre de billets restants');
            } else {
                Cart.addItem(quantity, category, $scope.event);
                FlashMessage.send("success", "L'item a été ajouté au panier");
            }
        }

        var apiCallSuccessCallback = function (result) {
            $scope.event = result
        }

        var apiCallErrorCallback = function () {
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
            $scope.getTransactionId = function() { return Cart.transactionId; }

            $scope.updateItemQuantity = function (index, newQuantity, maxQuantity) {
                if (newQuantity <= 0) {
                    Cart.removeItem(index);
                } else {
                    if (newQuantity > maxQuantity) {
                        FlashMessage.send("warning", "Le nombre de billet maximum est de " + maxQuantity.toString());
                        newQuantity = maxQuantity;
                    }
                    Cart.updateItemQuantity(index, newQuantity);
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
                    FlashMessage.send('warning', 'La sélection d\'achat est vide');
                }
                else if (!Login.isLoggedIn) {
                    notifyUserToLogin();
                }
                else if (window.confirm("Confirmez-vous le paiment de " + $scope.getTotalPrice() + "$ ?")) {
                    Cart.checkout(checkoutSuccess, checkoutError);
                }
            };

            var currentDate = new Date();
            var followingYears = 5;
            for (var i = 0; i < followingYears; i++) {
                $scope.expirationYears.push(currentDate.getFullYear() + i);
            }

            var notifyUserToLogin = function () {
                FlashMessage.send('info','Vous devez vous connecter avant de procéder au paiement');
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

    app.controller('UserController', ['$scope', '$location', '$http', 'Login', 'FlashMessage',
        function ($scope, $location, $http, Login, FlashMessage) {
            if (Login.isLoggedIn) {
                $scope.email = Login.username;
            } else {
                $location.path("/");
            }

            $scope.updatePassword = function () {
                if ($scope.password != $scope.passwordConfirmation) {
                    FlashMessage.send("error", "Les deux mots de passe doivent être identiques.")
                    return;
                }
                $http.post('/api/user/password', {
                    actualPassword: $scope.actualPassword,
                    password: $scope.password
                }).success(function () {
                        FlashMessage.send("success", "Votre mot de passe a été modifié avec succès.");
                    }).error(function () {
                        FlashMessage.send("error", "Erreur lors de la modification de votre mot de passe.");
                    });
            };

            $scope.updateEmail = function () {
                var email = $scope.email;
                $http.post('api/user/email', {
                    username: email
                }).success(function () {
                        Login.username = email;
                        FlashMessage.send("success", "Votre email a été modifié avec succès.");
                    }).error(function () {
                        FlashMessage.send("error", "La modification de votre email a échouée");
                    });
            }
        }
    ]);
})
;
