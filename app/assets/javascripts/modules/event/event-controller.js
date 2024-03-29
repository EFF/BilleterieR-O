define(['./module'], function (EventModule) {
    EventModule.controller('EventController', ['$scope', '$routeParams', 'Cart', 'FlashMessage', 'EventService', 'TicketService',
        function ($scope, $routeParams, Cart, FlashMessage, EventService, TicketService) {
            var eventId = $routeParams.eventId;
            $scope.event = null;
            $scope.sectionsSelectTagsByCategories = [];
            $scope.ticketsSelectTagsByCategories = [];
            $scope.quantity = [];

            var initPage = function (eventId) {
                var onGetSectionsByEventIdSuccess = function (sections) {
                    for (var categoryId in sections) {
                        $scope.sectionsSelectTagsByCategories.push({
                            name: 'sectionList' + categoryId,
                            selectedValue: null,
                            options: sections[categoryId]
                        });
                    }

                    $scope.refreshPageData();
                };

                var onGetSectionsByEventIdError = function () {
                    FlashMessage.send('error', "Une erreur est survenue.");
                };
                EventService.getSectionsByEventId(eventId).then(onGetSectionsByEventIdSuccess, onGetSectionsByEventIdError);
            };

            initPage(eventId);

            $scope.addSeatTicketToCart = function (ticketId, category) {
                var onGetTicketByIdSuccess = function (ticket) {
                    var successCallback = function () {
                        refreshTicketsList(eventId, category.id, ticket.section);
                        refreshNumberOfAvailableTickets(eventId, category.id);
                    };
                    Cart.addItem(ticket, category, $scope.event, successCallback);
                };

                var onGetTicketByIdError = function () {
                    FlashMessage.send('error', "Une erreur est survenue.");
                };

                TicketService.getById(ticketId).then(onGetTicketByIdSuccess, onGetTicketByIdError);
            };

            $scope.addAdminGeneralTicketsToCart = function (category, quantity) {
                var onGetTicketsSuccess = function (tickets) {
                    var successCallback = function () {
                        refreshNumberOfAvailableTickets(eventId, category.id);
                    };
                    Cart.addItems(tickets, category, $scope.event, successCallback);
                };

                var onGetTicketsError = function () {
                    FlashMessage.send('error', 'Le nombre de billets ajoutés au panier excède le nombre de billets restants.');
                };

                var options = {
                    eventId: eventId,
                    categoryId: category.id,
                    states: 'AVAILABLE',
                    quantity: quantity
                };

                TicketService.getTickets(options).then(onGetTicketsSuccess, onGetTicketsError);
            };

            $scope.addToCart = function (ticketId, category, quantity) {
                //Dans la vue, il n'y a pas de différence entre les 2 types de billet
                if (ticketId) {
                    $scope.addSeatTicketToCart(ticketId, category);
                } else {
                    $scope.addAdminGeneralTicketsToCart(category, quantity);
                }
            };

            var refreshNumberOfAvailableTickets = function (eventId, categoryId) {
                var onGetNumberOfTicketsSuccess = function (count) {
                    $scope.ticketsSelectTagsByCategories[categoryId].numberOfTickets = count;
                };

                var onGetNumberOfTicketsError = function () {
                    FlashMessage.send('error', "Une erreur est survenue.");
                };

                var options = {
                    eventId: eventId,
                    categoryId: categoryId
                };

                TicketService.getNumberOfTickets(options).then(onGetNumberOfTicketsSuccess, onGetNumberOfTicketsError);
            };

            var refreshTicketsList = function (eventId, categoryId, sectionName) {
                if ($scope.ticketsSelectTagsByCategories[categoryId] == null) {
                    $scope.ticketsSelectTagsByCategories[categoryId] = {
                        name: 'ticketList' + categoryId,
                        selectedValue: '',
                        options: [],
                        numberOfTickets: 0
                    };
                }

                if (!sectionName) {
                    $scope.ticketsSelectTagsByCategories[categoryId].selectedValue = '';
                    $scope.ticketsSelectTagsByCategories[categoryId].options = [];
                    return;
                }

                var onGetTicketsSuccess = function (tickets) {
                    var categoryId = null;

                    for (var i in tickets) {
                        var ticket = tickets[i];
                        if (categoryId == null || ticket.categoryId != categoryId) {
                            categoryId = ticket.categoryId;

                            $scope.ticketsSelectTagsByCategories[categoryId] = {
                                name: 'ticketList' + categoryId,
                                selectedValue: '',
                                options: []
                            };
                            $scope.ticketsSelectTagsByCategories[categoryId].selectedValue = '';
                            $scope.ticketsSelectTagsByCategories[categoryId].options = [];
                            refreshNumberOfAvailableTickets(eventId, categoryId);
                        }
                        $scope.ticketsSelectTagsByCategories[categoryId].options.push(ticket);
                    }
                };

                var onGetTicketsError = function () {
                    FlashMessage.send('warning', "Aucun billet n'a été trouvé pour cette section.");
                    //TODO on a un duplicat des 2 lignes suivantes
                    $scope.ticketsSelectTagsByCategories[categoryId].selectedValue = '';
                    $scope.ticketsSelectTagsByCategories[categoryId].options = [];
                };

                var options = {
                    eventId: eventId,
                    categoryId: categoryId,
                    states: 'AVAILABLE',
                    sectionName: sectionName
                };

                TicketService.getTickets(options).then(onGetTicketsSuccess, onGetTicketsError);
            };


            $scope.refreshPageData = function () {
                var onGetEventByIdSuccess = function (event) {
                    $scope.event = event;
                };

                var onGetEventByIdError = function () {
                    FlashMessage.send('error', "Une erreur est survenue.");

                    $scope.event = null;
                    for (var categoryId in $scope.sectionsSelectTagsByCategories) {
                        $scope.sectionsSelectTagsByCategories[categoryId].selectedValue = '';
                    }
                    $scope.ticketsSelectTagsByCategories = [];
                };


                for (var categoryId in $scope.sectionsSelectTagsByCategories) {
                    var sectionName = $scope.sectionsSelectTagsByCategories[categoryId].selectedValue;
                    refreshTicketsList(eventId, categoryId, sectionName);
                    refreshNumberOfAvailableTickets(eventId, categoryId);
                }
                EventService.getById(eventId).then(onGetEventByIdSuccess, onGetEventByIdError);
            };
        }]);
});
