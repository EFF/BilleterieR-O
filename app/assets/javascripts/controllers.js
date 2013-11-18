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
        };

        $scope.$watch('filters', function () {
            apiCall();
        }, true);

        apiCall();
    }]);

    app.controller('EventController', ['$scope', '$http', '$routeParams', 'Cart', 'FlashMessage', function ($scope, $http, $routeParams, Cart, FlashMessage) {
        var eventId = $routeParams.eventId;
        $scope.event = null;
        $scope.sectionsByCategories = [];
        $scope.ticketsByCategories = [];
        $scope.quantity = [];

        $scope.addToCart = function (ticketId, category, quantity) {
            if (ticketId) {
                var url = '/api/tickets/' + ticketId;
                $http.get(url)
                    .success(function (ticket) {
                        Cart.addItem(ticket, category, $scope.event);
                        refreshTicketsList(eventId, category.id, ticket.section);
                    });
            } else {
                var url = '/api/tickets?eventId=' + eventId + '&categoryId=' + category.id + '&states=AVAILABLE,RESALE' + '&quantity=' + quantity;
                $http.get(url)
                    .success(function (tickets) {
                        Cart.addItems(tickets, category, $scope.event);
                    })
                    .error(function() {
                        FlashMessage.send('error', 'Le nombre de billets ajoutés au panier excède le nombre de billets restants.');
                    });
            }
        };

        var refreshTicketsList = function(eventId, categoryId, sectionName) {
            var url = '/api/tickets?eventId=' + eventId + '&categoryId=' + categoryId + '&states=AVAILABLE,RESALE';
            var emptyTicketList = {
                type : 'select',
                name : 'ticketList' + categoryId,
                selectedValue : '',
                options : []
            };

            if (!sectionName) {
                $scope.ticketsByCategories[categoryId] = emptyTicketList;
                return;
            }
            url += '&sectionName=' + encodeURIComponent(sectionName);
            $http.get(url)
                .success(function (tickets) {
                    var categoryId = null;

                    for (var i in tickets) {
                        var ticket = tickets[i];
                        if (categoryId == null || ticket.categoryId != categoryId) {
                            categoryId = ticket.categoryId;
                            $scope.ticketsByCategories[categoryId] = emptyTicketList;
                        }
                        $scope.ticketsByCategories[categoryId].selectedValue = '';
                        $scope.ticketsByCategories[categoryId].options.push(ticket);
                    }
                });
        };

        var apiCallSuccessCallback = function (result) {
            $scope.event = result;
        };

        var apiCallErrorCallback = function () {
            //TODO emit error event and handle it in a directive
            $scope.event = null;
            for (var categoryId in $scope.sectionsByCategories) {
                $scope.sectionsByCategories[categoryId].selectedValue = null;
            }
            $scope.ticketsByCategories = [];
        };

        $scope.apiCall = function () {
            for (var categoryId in $scope.sectionsByCategories) {
                var sectionName = $scope.sectionsByCategories[categoryId].selectedValue;
                refreshTicketsList(eventId, categoryId, sectionName);
            }
            $http.get('/api/events/' + eventId)
                .success(apiCallSuccessCallback)
                .error(apiCallErrorCallback);
        };

        $http.get('/api/events/' + eventId + '/sections')
            .success(function (sections) {
                for (var categoryId in sections) {
                    $scope.sectionsByCategories.push({
                        type : 'select',
                        name : 'sectionList' + categoryId,
                        selectedValue : null,
                        options : sections[categoryId]
                    });
                }});

        $scope.apiCall();
    }]);

    app.controller('ThanksController', ['$scope', 'Cart',
        function ($scope, Cart) {
            $scope.getTransactionId = function() { return Cart.transactionId; }
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
            };
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

    app.controller('TicketController', ['$scope', '$http', '$routeParams',
        function ($scope, $http, $routeParams) {
            var ticketId = $routeParams.ticketId;
            $scope.ticket = null;
            $scope.event = null;

            var apiCallSuccessCallback = function (result) {
                $scope.ticket = result;
                $http.get('api/events/' + result.eventId).success(function (event) {
                    $scope.event = event;
                });
            };

            var apiCallErrorCallback = function () {
                //TODO emit error event and handle it in a directive
                $scope.ticket = null;
            };

            $http.get('/api/tickets/' + ticketId)
                .success(apiCallSuccessCallback)
                .error(apiCallErrorCallback);
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
});
