define(['./module'], function (EventModule) {
    EventModule.controller('EventController', ['$scope', '$http', '$routeParams', 'Cart', 'FlashMessage', function ($scope, $http, $routeParams, Cart, FlashMessage) {
        var eventId = $routeParams.eventId;
        $scope.event = null;
        $scope.sectionsByCategories = [];
        $scope.ticketsByCategories = [];
        $scope.quantity = [];

        //TODO ne devrait exister, nous avons l'info par les billets de l'event
        $http.get('/api/events/' + eventId + '/sections')
            .success(function (sections) {
                for (var categoryId in sections) {
                    $scope.sectionsByCategories.push({
                        type: 'select',
                        name: 'sectionList' + categoryId,
                        selectedValue: null,
                        options: sections[categoryId]
                    });
                }
                $scope.apiCall();
            });

        $scope.addToCart = function (ticketId, category, quantity) {
            //TODO split in 2 functions!!!
            if (ticketId) {
                //TODO this should be in ticket service.. we need to fix cyclic dependency
                var url = '/api/tickets/' + ticketId;
                $http.get(url)
                    .success(function (ticket) {
                        var successCallback = function () {
                            refreshTicketsCall(eventId, category.id, ticket.section);
                            refreshNumberOfAvailableTickets(eventId, category.id);
                        }
                        Cart.addItem(ticket, category, $scope.event, successCallback);
                    });
            } else {
                //TODO this should be in ticket service.. we need to fix cyclic dependency
                var url = '/api/tickets?eventId=' + eventId + '&categoryId=' + category.id + '&states=AVAILABLE' + '&quantity=' + quantity;
                $http.get(url)
                    .success(function (tickets) {
                        var successCallback = function () {
                            refreshNumberOfAvailableTickets(eventId, category.id);
                        }
                        Cart.addItems(tickets, category, $scope.event, successCallback);
                    })
                    .error(function () {
                        FlashMessage.send('error', 'Le nombre de billets ajoutés au panier excède le nombre de billets restants.');
                    });
            }
        };

        var refreshNumberOfAvailableTickets = function (eventId, categoryId) {
            var successCallback = function (count) {
                $scope.ticketsByCategories[categoryId].numberOfTickets = count;
            };
            //TODO this should be in ticket service.. we need to fix cyclic dependency
            $http.get('api/tickets/number-of-tickets', {
                params: {
                    eventId: eventId,
                    categoryId: categoryId
                }
            }).success(successCallback);
        };

        var refreshTicketsSuccessCallback = function (tickets) {
            var categoryId = null;

            for (var i in tickets) {
                var ticket = tickets[i];
                if (categoryId == null || ticket.categoryId != categoryId) {
                    categoryId = ticket.categoryId;

                    var emptyTicketList = {
                        type: 'select',
                        name: 'ticketList' + categoryId,
                        selectedValue: '',
                        options: []
                    };

                    $scope.ticketsByCategories[categoryId] = emptyTicketList;
                    $scope.ticketsByCategories[categoryId].selectedValue = '';
                    $scope.ticketsByCategories[categoryId].options = [];
                    refreshNumberOfAvailableTickets(eventId, categoryId);
                }
                $scope.ticketsByCategories[categoryId].options.push(ticket);
            }
        };

        //TODO rename ... Do you really refresh the call ?
        var refreshTicketsCall = function (eventId, categoryId, sectionName) {
            //TODO Use params
            var url = '/api/tickets?eventId=' + eventId + '&categoryId=' + categoryId + '&states=AVAILABLE';

            if ($scope.ticketsByCategories[categoryId] == null) {
                $scope.ticketsByCategories[categoryId] = {
                    type: 'select',
                    name: 'ticketList' + categoryId,
                    selectedValue: '',
                    options: [],
                    numberOfTickets: 0
                };
            }

            if (!sectionName) {
                $scope.ticketsByCategories[categoryId].selectedValue = '';
                $scope.ticketsByCategories[categoryId].options = [];
                return;
            }
            url += '&sectionName=' + encodeURIComponent(sectionName);
            //TODO this should be in ticket service.. we need to fix cyclic dependency
            $http.get(url)
                .success(refreshTicketsSuccessCallback);
        };

        var apiCallSuccessCallback = function (result) {
            $scope.event = result;
        };

        var apiCallErrorCallback = function () {
            //TODO emit error event and handle it in a directive
            $scope.event = null;
            for (var categoryId in $scope.sectionsByCategories) {
                $scope.sectionsByCategories[categoryId].selectedValue = '';
            }
            $scope.ticketsByCategories = [];
        };

        //TODO rename I have no clue about the purpose of this one
        $scope.apiCall = function () {
            for (var categoryId in $scope.sectionsByCategories) {
                var sectionName = $scope.sectionsByCategories[categoryId].selectedValue;
                refreshTicketsCall(eventId, categoryId, sectionName);
                refreshNumberOfAvailableTickets(eventId, categoryId);
            }
            $http.get('/api/events/' + eventId)
                .success(apiCallSuccessCallback)
                .error(apiCallErrorCallback);
        };
    }]);
});
