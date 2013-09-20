var controllers = require("./controllers");

var app = angular.module('billetterieRO', []);

app.config(['$routeProvider', '$locationProvider', function(routeProvider, locationProvider){
    locationProvider.hashPrefix('!')

    routeProvider.when('/events', {controller: 'EventsController', templateUrl: '/assets/events.html'});
}]);

app.controller('EventsController', ['$scope', '$http', controllers.events]);
