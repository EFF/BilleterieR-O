define(['./module'], function (TicketModule) {
    TicketModule.controller('AddTicketController', ['$scope', '$routeParams', 'TicketService', 'EventService',
        function ($scope, $routeParams, TicketService, EventService) {
            $scope.ticketToAdd = {};
            $scope.categoryTypes = ["SEAT", "GENERAL_ADMISSION"];
            $scope.eventId = $routeParams.eventId;

            
            //TODO: set le type de catégorie au billet to add, set le nombre de billet si admin général, set la section et le seat sinon
            //TODO: ajouter dans le ticket service un POST
            //TODO : done !


            var onGetEventSuccess = function(event){
                $scope.event = event;
                $scope.ticketToAdd.eventId = $scope.eventId;
            };

            var onGetEventSectionsSuccess = function(sections){
                $scope.eventSections = sections;
            };

            var initData = function(){
                EventService.getById($scope.eventId).then(onGetEventSuccess);
                EventService.getSectionsByEventId($scope.eventId).then(onGetEventSectionsSuccess);
            };

            initData();
        }]);
});
