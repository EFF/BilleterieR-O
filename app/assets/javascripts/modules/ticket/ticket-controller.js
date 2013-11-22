define(['./module'], function (TicketModule) {
    TicketModule.controller('TicketController', ['$scope', '$http', '$routeParams',
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
});
