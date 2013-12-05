define(['./module'], function (TicketModule) {
    TicketModule.controller('TicketController', ['$scope', '$routeParams', 'TicketService', 'EventService',
        function ($scope, $routeParams, TicketService, EventService) {
            var ticketId = $routeParams.ticketId;
            $scope.ticket = null;
            $scope.event = null;

            var onGetTicketByIdSuccess = function (ticket) {
                $scope.ticket = ticket;
                EventService.getById(ticket.eventId).then(onGetEventByIdSuccess, onGetEventByIdError);
            };

            var onGetTicketByIdError = function () {
                $scope.ticket = null;
                $scope.event = null;
            };

            var onGetEventByIdSuccess = function (event) {
                $scope.event = event;
            };

            var onGetEventByIdError = function () {
                $scope.event = null;
            };

            TicketService.getById(ticketId).then(onGetTicketByIdSuccess, onGetTicketByIdError);
        }]);
});
