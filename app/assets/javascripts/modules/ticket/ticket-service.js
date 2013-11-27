define(['./module'], function (TicketModule) {
    TicketModule.factory('TicketService', ['$http', '$q', function ($http, $q) {
        var ticketService = {};

        ticketService.getById = function (ticketId) {
            var deferred = $q.defer();
            $http.get('/api/tickets/' + ticketId).success(deferred.resolve).error(deferred.reject);

            return deferred.promise;
        }

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

        return ticketService;
    }]);
});
