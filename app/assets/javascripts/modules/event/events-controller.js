define(['./module'], function (EventModule) {
    EventModule.controller('EventsController', ['$scope', '$http', function ($scope, $http) {
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
            //TODO emit error with FlashMessage
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

            //TODO this should be in event service
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
