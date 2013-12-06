define(['./module'], function (TicketModule) {
    TicketModule.controller('AddTicketController', ['$scope', '$routeProvider', 'TicketService', 'EventService',
        function ($scope, $routeProvider, TicketService, EventService) {
            $scope.eventId = $routeProvider.eventId;
            console.log($scope.eventId);
        }]);
});
