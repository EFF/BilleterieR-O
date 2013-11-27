define(['./module'], function (TicketModule) {
    TicketModule.factory('TicketService', ['$http', '$q', function ($http, $q) {
        return {
            getById: function (ticketId) {
                var deferred = $q.defer();
                $http.get('/api/tickets/' + ticketId).success(deferred.resolve).error(deferred.reject);

                return deferred.promise;
            },
            //TODO move this into EventService but we need to fixe a cyclic dependency between the 2 modules first.
            getEventInfoById: function(eventId){
                var deferred = $q.defer();
                $http.get('/api/events/' + eventId).success(deferred.resolve).error(deferred.reject);

                return deferred.promise;
            }
        };
    }]);
});
