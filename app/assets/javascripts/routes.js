define(['app', 'controllers', 'filters'], function (app) {
    return app.config(['$routeProvider', '$locationProvider', function (routeProvider, locationProvider) {
        locationProvider.hashPrefix('!');

        routeProvider.when('/events', {controller: 'EventsController', templateUrl: '/assets/templates/events.html'});
        routeProvider.when('/events/:eventId', {controller: 'EventController', templateUrl: '/assets/templates/event.html'});
        routeProvider.otherwise({redirectTo: '/events'});
    }]);
});
