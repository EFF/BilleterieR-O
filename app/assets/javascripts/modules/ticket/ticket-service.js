define(['./module'], function (TicketModule) {
    TicketModule.factory('TicketService', ['$http', '$q', function ($http, $q) {
        var ticketService = {};

        ticketService.getById = function (ticketId) {
            var deferred = $q.defer();
            $http.get('/api/tickets/' + ticketId).success(deferred.resolve).error(deferred.reject);

            return deferred.promise;
        };

        ticketService.getTickets = function (options) {
            var deferred = $q.defer();

            var url = '/api/tickets';
            var config = {
                params: options
            };

            $http.get(url, config).success(deferred.resolve).error(deferred.reject);

            return deferred.promise;
        };

        ticketService.getNumberOfTickets = function (options) {
            var deferred = $q.defer();

            var url = '/api/tickets/number-of-tickets';
            var config = {
                params: options
            };

            $http.get(url, config).success(deferred.resolve).error(deferred.reject);

            return deferred.promise;
        };

        ticketService.addTicket = function(ticketToAdd){
            var deferred = $q.defer();

            var url = '/api/tickets';
            $http.post(url, ticketToAdd).success(deferred.resolve).error(deferred.reject);

            return deferred.promise;
        };

        ticketService.freeTickets = function (tickets) {
            return updateTicketState(tickets, 'free');
        };

        ticketService.reserveTickets = function (tickets) {
            return updateTicketState(tickets, 'reserve');
        };

        ticketService.checkoutTickets = function (tickets) {
            return updateTicketState(tickets, 'checkout');
        };

        var updateTicketState = function (tickets, state) {
            var deferred = $q.defer();
            var url;
            switch (state) {
                case "checkout":
                    url = '/api/checkout';
                    break;
                case "free":
                    url = '/api/tickets/free';
                    break;
                case "reserve":
                    url = '/api/tickets/reserve';
                    break;
                default:
                    deferred.reject('invalid state');
                    break;
            }

            if (tickets.length <= 0) {
                deferred.reject();
            }
            else if (url) {
                var data = {
                    ticketIds: []
                };

                for (var i in tickets) {
                    if (tickets.hasOwnProperty(i)) {
                        data.ticketIds.push(tickets[i].id);
                    }
                }

                $http.post(url, data).success(deferred.resolve).error(deferred.reject);
            }

            return deferred.promise;
        };

        return ticketService;
    }]);
});
