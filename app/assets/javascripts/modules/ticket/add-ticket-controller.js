define(['./module'], function (TicketModule) {
    TicketModule.controller('AddTicketController', ['$scope', '$routeParams', 'TicketService', 'EventService',
        function ($scope, $routeParams, TicketService, EventService) {
            //TODO : protéger la route si le user n'est pas admin !
            var onGetEventSuccess = function(event){
                $scope.event = event
            };

            EventService.getById($routeParams.eventId).then(onGetEventSuccess);
        }]);
});
