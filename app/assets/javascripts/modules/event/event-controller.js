define(['./module'], function (EventModule) {
    EventModule.controller('EventController', ['$scope', '$http', '$routeParams', 'Cart', 'FlashMessage', function ($scope, $http, $routeParams, Cart, FlashMessage) {
        var eventId = $routeParams.eventId;
        $scope.event = null;
        $scope.sectionsSelectTagsByCategories = [];
        $scope.ticketsSelectTagsByCategories = [];
        $scope.quantity = [];

        $http.get('/api/events/' + eventId + '/sections')
            .success(function (sections) {
                for (var categoryId in sections) {
                    $scope.sectionsSelectTagsByCategories.push({
                        type: 'select',
                        name: 'sectionList' + categoryId,
                        selectedValue: null,
                        options: sections[categoryId]
                    });
                }
                $scope.apiCall();
            });

        $scope.addToCart = function (ticketId, category, quantity) {
            if (ticketId) {
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
                $scope.ticketsSelectTagsByCategories[categoryId].numberOfTickets = count;
            };
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

                    $scope.ticketsSelectTagsByCategories[categoryId] = emptyTicketList;
                    $scope.ticketsSelectTagsByCategories[categoryId].selectedValue = '';
                    $scope.ticketsSelectTagsByCategories[categoryId].options = [];
                    refreshNumberOfAvailableTickets(eventId, categoryId);
                }
                $scope.ticketsSelectTagsByCategories[categoryId].options.push(ticket);
            }
        };

        var refreshTicketsCall = function (eventId, categoryId, sectionName) {
            var url = '/api/tickets?eventId=' + eventId + '&categoryId=' + categoryId + '&states=AVAILABLE';

            if ($scope.ticketsSelectTagsByCategories[categoryId] == null) {
                $scope.ticketsSelectTagsByCategories[categoryId] = {
                    type: 'select',
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
            url += '&sectionName=' + encodeURIComponent(sectionName);
            $http.get(url)
                .success(function(tickets){
                    if(tickets.length == 0){
                        $scope.ticketsSelectTagsByCategories[categoryId].selectedValue = '';
                        $scope.ticketsSelectTagsByCategories[categoryId].options = [];
                    }
                    else{
                        refreshTicketsSuccessCallback(tickets);
                    }
                });
        };

        var apiCallSuccessCallback = function (result) {
            $scope.event = result;
        };

        var apiCallErrorCallback = function () {
            //TODO emit error event and handle it in a directive
            $scope.event = null;
            for (var categoryId in $scope.sectionsSelectTagsByCategories) {
                $scope.sectionsSelectTagsByCategories[categoryId].selectedValue = '';
            }
            $scope.ticketsSelectTagsByCategories = [];
        };

        $scope.apiCall = function () {
            for (var categoryId in $scope.sectionsSelectTagsByCategories) {
                var sectionName = $scope.sectionsSelectTagsByCategories[categoryId].selectedValue;
                refreshTicketsCall(eventId, categoryId, sectionName);
                refreshNumberOfAvailableTickets(eventId, categoryId);
            }
            $http.get('/api/events/' + eventId)
                .success(apiCallSuccessCallback)
                .error(apiCallErrorCallback);
        };
    }]);
});
