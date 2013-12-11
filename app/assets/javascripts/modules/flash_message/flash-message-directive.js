define(['./module'], function (FlashMessageModule) {
    FlashMessageModule.directive('flashMessages', function () {
        return {
            templateUrl: 'assets/templates/directives/flash_message.html',
            restrict: 'E',
            scope: '@',
            controller: function ($scope) {
                $scope.messages = [];

                var handleMessageEvent = function (event, message) {
                    $scope.messages.push(message);
                };

                $scope.remove = function (message) {
                    var index = $scope.messages.indexOf(message);
                    $scope.messages.splice(index, 1);
                };

                $scope.getCssSuffix = function (message) {
                    if (message.type == "error") {
                        return "danger";
                    }
                    return message.type;
                };

                $scope.$on('messageEvent', handleMessageEvent);
            }
        }
    });
});
