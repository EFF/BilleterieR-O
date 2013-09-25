define(['app'], function (app) {
    var events = function ($scope, $http) {
        $scope.events = null;
        $scope.filters = {};
        // TODO: Fetch facettes
        $scope.sports = ['Soccer', 'Golf', 'Football'];

        function apiCallSuccessCallback(results) {
            $scope.events = results;
        };

        function apiCallErrorCallback(err) {
            //TODO emit error event and handle it in a directive
            $scope.events = [];
        };

        function apiCall() {
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

        $scope.$watch('filters.sport', function(newValue, oldValue) {
            if (newValue != oldValue) {
                apiCall();
            }
        });

        apiCall();
    }

    var event = function ($scope, $http, $routeParams) {
        var eventId = $routeParams.eventId;
        $scope.event = null;

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
    }

    app.controller('EventsController', ['$scope', '$http', events]);
    app.controller('EventController', ['$scope', '$http', '$routeParams', event]);
})
