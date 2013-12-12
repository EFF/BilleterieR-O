define(['./module'], function (TicketModule) {
    TicketModule.controller('AddTicketController', ['$scope', '$routeParams', 'TicketService', 'EventService', 'FlashMessage',
        function ($scope, $routeParams, TicketService, EventService, FlashMessage) {
            $scope.ticketToAdd = {};
            $scope.categoryTypes = ["SEAT", "GENERAL_ADMISSION"];
            $scope.eventId = $routeParams.eventId;
            $scope.addTicket = TicketService.addTicket;

            $scope.updateTicketToAdd = function (category) {
                $scope.ticketToAdd.categoryId = category.id;
                $scope.ticketToAdd.categoryType = category.type;

                delete $scope.ticketToAdd.sectionName;
                delete $scope.ticketToAdd.seat;
                delete $scope.ticketToAdd.quantity;
            };

            $scope.addTicket = function () {
                var onAddTicketSuccess = function () {
                    FlashMessage.send('success', "L'ajout de billet s'est fait avec succès. Vous pouvez en ajouter d'autre pour cet événement");
                    $scope.ticketToAdd = {eventId: $scope.eventId,
                        categoryId: $scope.ticketToAdd.categoryId,
                        categoryType: $scope.ticketToAdd.categoryType };
                };

                var onAddTicketFail = function (error) {
                    //TODO: display a more explicit message
                    FlashMessage.send('error', "Une erreure est survenue lors de l'ajout de billet")
                };

                TicketService.addTicket($scope.ticketToAdd).then(onAddTicketSuccess, onAddTicketFail);
            };

            var onInitDataError = function (error) {
                FlashMessage.send('error', 'Une erreure est survenue')
            };

            var onGetEventSuccess = function (event) {
                $scope.event = event;
                $scope.ticketToAdd.eventId = $scope.eventId;
            };

            var onGetEventSectionsSuccess = function (sections) {
                $scope.eventSections = sections;
            };

            var initData = function () {
                EventService.getById($scope.eventId).then(onGetEventSuccess, onInitDataError);
                EventService.getSectionsByEventId($scope.eventId).then(onGetEventSectionsSuccess, onInitDataError);
            };

            initData();
        }]);
});
