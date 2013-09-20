var events = function($scope, $http) {
    $http.get('/api/events').success(function(result){
        console.log(result);
        $scope.events = result;
    });
}

module.exports = {events: events};
