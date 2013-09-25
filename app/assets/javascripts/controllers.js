var events = function ($scope, $http) {
    $scope.events = null;

    var apiCallSuccessCallback = function (result) {
        $scope.events = result
    };

    var apiCallErrorCallback = function (err) {
        //TODO emit error event and handle it in a directive
        $scope.events = null;
    };

    $http.get('/api/events')
        .success(apiCallSuccessCallback)
        .error(apiCallErrorCallback);
}

var event = function ($scope, $http, $routeParams) {
    var eventId = $routeParams.eventId;
    $scope.event = null;

    var apiCallSuccessCallback = function (result) {
        $scope.event = result
    };

    var apiCallErrorCallback = function (err) {
        //TODO emit error event and handle it in a directive
        $scope.event = null;
    };

    $http.get('/api/events/' + eventId)
        .success(apiCallSuccessCallback)
        .error(apiCallErrorCallback);
}

module.exports = {
    events: events,
    event: event
};
