define(['./module'], function (TicketModule) {
    TicketModule.controller('TicketController', ['$scope', '$routeParams', 'TicketService',
        function ($scope, $routeParams, TicketService) {
            var ticketId = $routeParams.ticketId;
            $scope.ticket = null;
            $scope.event = null;

            var onGetTicketByIdSuccess = function (ticket) {
                $scope.ticket = ticket;
                TicketService.getEventInfoById(ticket.eventId).then(onGetEventByIdSuccess, onGetEventByIdError);
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
