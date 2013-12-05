define(['./module'], function (EventModule) {
    EventModule.factory('EventService', ['$http', '$q', function ($http, $q) {
        var eventService = {};

        eventService.getById = function (eventId) {
            var deferred = $q.defer();

            $http.get('/api/events/' + eventId).success(deferred.resolve).error(deferred.reject);

            return deferred.promise;
        };

        eventService.getSectionsByEventId = function (eventId) {
            var deferred = $q.defer();
            var url = '/api/events/' + eventId + '/sections';

            $http.get(url).success(deferred.resolve).error(deferred.reject);

            return deferred.promise;
        };

        eventService.getEvents = function (filters) {
            var deferred = $q.defer();
            var url = '/api/events';

            var config = {
                params: filters
            };

            $http.get(url, config).success(deferred.resolve).error(deferred.reject);

            return deferred.promise;
        };

        eventService.getFacets = function () {
            var deferred = $q.defer();
            var url = '/api/facets';

            $http.get(url).success(deferred.resolve).error(deferred.reject);

            return deferred.promise;
        };

        return eventService;
    }]);
});
