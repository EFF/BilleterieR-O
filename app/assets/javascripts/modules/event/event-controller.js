define(['./module'], function (EventModule) {
    EventModule.controller('EventController', ['$scope', '$http', '$routeParams', 'Cart', 'FlashMessage', function ($scope, $http, $routeParams, Cart, FlashMessage) {
        var eventId = $routeParams.eventId;
        $scope.event = null;
        $scope.sectionsByCategories = [];
        $scope.ticketsByCategories = [];
        $scope.quantity = [];

        $scope.addToCart = function (ticketId, category, quantity) {
            if (ticketId) {
                var url = '/api/tickets/' + ticketId;
                $http.get(url)
                    .success(function (ticket) {
                        Cart.addItem(ticket, category, $scope.event);
                        refreshTicketsList(eventId, category.id, ticket.section);
                    });
            } else {
                var url = '/api/tickets?eventId=' + eventId + '&categoryId=' + category.id + '&states=AVAILABLE,RESALE' + '&quantity=' + quantity;
                $http.get(url)
                    .success(function (tickets) {
                        Cart.addItems(tickets, category, $scope.event);
                    })
                    .error(function() {
                        FlashMessage.send('error', 'Le nombre de billets ajoutés au panier excède le nombre de billets restants.');
                    });
            }
        };

        var refreshTicketsList = function(eventId, categoryId, sectionName) {
            var url = '/api/tickets?eventId=' + eventId + '&categoryId=' + categoryId + '&states=AVAILABLE,RESALE';
            var emptyTicketList = {
                type : 'select',
                name : 'ticketList' + categoryId,
                selectedValue : '',
                options : []
            };

            if (!sectionName) {
                $scope.ticketsByCategories[categoryId] = emptyTicketList;
                return;
            }
            url += '&sectionName=' + encodeURIComponent(sectionName);
            $http.get(url)
                .success(function (tickets) {
                    var categoryId = null;

                    for (var i in tickets) {
                        var ticket = tickets[i];
                        if (categoryId == null || ticket.categoryId != categoryId) {
                            categoryId = ticket.categoryId;
                            $scope.ticketsByCategories[categoryId] = emptyTicketList;
                        }
                        $scope.ticketsByCategories[categoryId].selectedValue = '';
                        $scope.ticketsByCategories[categoryId].options.push(ticket);
                    }
                });
        };

        var apiCallSuccessCallback = function (result) {
            $scope.event = result;
        };

        var apiCallErrorCallback = function () {
            //TODO emit error event and handle it in a directive
            $scope.event = null;
            for (var categoryId in $scope.sectionsByCategories) {
                $scope.sectionsByCategories[categoryId].selectedValue = null;
            }
            $scope.ticketsByCategories = [];
        };

        $scope.apiCall = function () {
            for (var categoryId in $scope.sectionsByCategories) {
                var sectionName = $scope.sectionsByCategories[categoryId].selectedValue;
                refreshTicketsList(eventId, categoryId, sectionName);
            }
            $http.get('/api/events/' + eventId)
                .success(apiCallSuccessCallback)
                .error(apiCallErrorCallback);
        };

        $http.get('/api/events/' + eventId + '/sections')
            .success(function (sections) {
                for (var categoryId in sections) {
                    $scope.sectionsByCategories.push({
                        type : 'select',
                        name : 'sectionList' + categoryId,
                        selectedValue : null,
                        options : sections[categoryId]
                    });
                }});

        $scope.apiCall();
    }]);
});
