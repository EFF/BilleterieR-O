var controllers = require("./controllers");

var app = angular.module('billetterieRO', []);

app.config(['$routeProvider', '$locationProvider', function(routeProvider, locationProvider){
    locationProvider.hashPrefix('!');

    routeProvider.when('/events', {controller: 'EventsController', templateUrl: '/assets/templates/events.html'});
    routeProvider.when('/events/:eventId', {controller: 'EventController', templateUrl: '/assets/templates/event.html'});
    routeProvider.otherwise({redirectTo: '/events'});
}]);

app.controller('EventsController', ['$scope', '$http', controllers.events]);
app.controller('EventController', ['$scope', '$http', '$routeParams', controllers.event]);
