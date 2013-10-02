define(['app'], function (app) {
    app.controller('EventsController', ['$scope', '$http', function ($scope, $http) {
        $scope.events = null;
        $scope.filters = {};
        $scope.isLoading = false;

        $http.get('api/facets').success(function (facets) {
            $scope.facets = facets;
        });

        function apiCallSuccessCallback(results) {
            $scope.events = results;
            $scope.isLoading = false;
        };

        function apiCallErrorCallback(err) {
            //TODO emit error event and handle it in a directive
            $scope.events = [];
            $scope.isLoading = false;
        };

        function apiCall() {
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

        function apiCallSuccessCallback(result) {
            $scope.event = result
        }

        function apiCallErrorCallback(err) {
            //TODO emit error event and handle it in a directive
            $scope.event = null;
        }

        $http.get('/api/events/' + eventId)
            .success(apiCallSuccessCallback)
            .error(apiCallErrorCallback);
    }]);

    app.controller('CartController', ['$scope','$http', 'FlashMessage', 'Cart', '$location', function ($scope, $http, FlashMessage, Cart, $location) {
        $scope.cart = Cart.getItems();
        $scope.removeItem = Cart.removeItem;
        $scope.removeAllItem = Cart.removeAllItem;
        var noItemSelected = Cart.noItemSelected;

        $scope.$watch('cart', function () {
            updateTotals();
        }, true)

        $scope.toggleAll = function(value){
            for(var key in $scope.cart){
                $scope.cart[key].selected = value;
            }
        }

        $scope.checkout = function(){
            if(noItemSelected()){
                FlashMessage.send('error', 'Le panier d\'achat est vide');
            }else{
                for(key in $scope.cart){
                    if($scope.cart[key].selected){
                        checkoutItem($scope.cart[key]);
                    }
                }
            }
        }

        var updateTotals = function(){
            $scope.totalPrice = 0;
            $scope.totalSelectedPrice = 0;
            for(var key in $scope.cart) {
                var item = $scope.cart[key];
                var itemPrice = item.quantity * item.category.price;
                $scope.totalPrice += itemPrice;
                if(item.selected){
                    $scope.totalSelectedPrice += itemPrice;
                }
            }
        }

        var checkoutItem = function(item){
            var requestOptions = {
                method: 'POST',
                url: '/api/checkout',
                data: {
                    eventId : item.event.id,
                    categoryId: item.category.id,
                    numberOfTickets: item.quantity
                }
            };

            $http(requestOptions)
                .success(function(result, status){
                    Cart.removeItem($scope.cart.indexOf(item));
                    FlashMessage.send("success", "La transaction a été complétée");
                    $location.path("/thanks");
                })
                .error(function(error, status){
                    FlashMessage.send("error", error);
                });
        }
    }]);
});
