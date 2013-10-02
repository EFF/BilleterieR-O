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

    app.controller('CartController', ['$scope', 'Cart', function ($scope, Cart) {
        $scope.totalItemsPrice = 0;
        $scope.cart = Cart.getItems();

        function computeTotalItemsPrice() {
            //TODO: update total with selected items only
            var total = 0;
            for (var x in $scope.cart) {
                var item = $scope.cart[x];
                total += item.quantity * item.category.price;
            }
            return total;
        }

        $scope.removeItem = Cart.removeItem;
        $scope.removeAllItem = Cart.removeAllItem;

        $scope.$watch('cart', function () {
            $scope.totalItemsPrice = computeTotalItemsPrice();
        }, true)

        $scope.toggleAll = function(value){
            for(var key in $scope.cart){
                $scope.cart[key].selected = value;
            }
        }

        $scope.checkout = function(){
            //TODO: http.post(/api/checkout) for each selected items in the cart
        }
    }]);
});
