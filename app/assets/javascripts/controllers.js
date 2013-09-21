var events = function($scope, $http) {
    $http.get('/api/events').success(function(result){
        $scope.events = result;
    });
}

var event = function($scope, $http, $routeParams) {
    var eventId = $routeParams.eventId;
    
    $http.get('/api/events/' + eventId).success(function(result){
        $scope.event = result
    });
}

module.exports = {
    events: events,
    event: event
};
