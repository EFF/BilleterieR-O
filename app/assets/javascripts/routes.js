define(['app', 'controllers', 'filters', 'services', 'directives'], function (app) {
    return app.config(['$routeProvider', '$locationProvider', function (routeProvider, locationProvider) {
        locationProvider.hashPrefix('!');

        routeProvider.when('/events', {controller: 'EventsController',templateUrl: '/assets/templates/events.html'});
        routeProvider.when('/events/:eventId', {controller: 'EventController', templateUrl: '/assets/templates/event.html'});
        routeProvider.when('/tickets/:ticketId', {controller: 'TicketController', templateUrl: '/assets/templates/ticket.html'});
        routeProvider.when('/cart', {controller: 'CartController', templateUrl: 'assets/templates/cart.html'});
        routeProvider.when('/thanks', {templateUrl: 'assets/templates/thanks.html'});
        routeProvider.when('/login', {controller: 'LoginController', templateUrl: 'assets/templates/login.html'});
        routeProvider.otherwise({redirectTo: '/events'});
    }]);
});
