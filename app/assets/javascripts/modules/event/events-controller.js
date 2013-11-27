define(['./module'], function (EventModule) {
    EventModule.controller('EventsController', ['$scope', '$http', 'FlashMessage', function ($scope, $http, FlashMessage) {
        $scope.events = null;
        $scope.filters = {};
        $scope.isLoading = false;

        //TODO facet service
        $http.get('/api/facets').success(function (facets) {
            $scope.facets = facets;
        });

        var apiCallSuccessCallback = function (results) {
            $scope.events = results;
            $scope.isLoading = false;
        };

        var apiCallErrorCallback = function () {
            //TODO un message plus parlant. Désolé j'ai encore aucune idée c'est quoi apiCall
            FlashMessage.send('error', 'Une erreur est survenue.');
            $scope.events = [];
            $scope.isLoading = false;
        };

        //TODO rename this one ...
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

            //TODO this should be in event service and using params
            $http.get(url)
                .success(apiCallSuccessCallback)
                .error(apiCallErrorCallback);
        };

        $scope.$watch('filters', function () {
            apiCall();
        }, true);

        apiCall();
    }]);
});
