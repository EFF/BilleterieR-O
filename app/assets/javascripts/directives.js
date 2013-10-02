define(['app'], function (app) {
    app.directive('menu', ['Cart', function (Cart) {
        return {
            replace: true,
            templateUrl: 'assets/templates/directives/menu.html',
            restrict: 'E',
            controller: function ($scope) {
                $scope.getTotalQuantity = Cart.getTotalQuantity;
            }
        };
    }]);
    app.directive('flashMessages', function () {
        return {
            templateUrl: 'assets/templates/directives/flash_message.html',
            restrict: 'E',
            scope: '@',
            controller: function ($scope) {
                $scope.messages = [];

                handleMessageEvent = function (event, message) {
                    $scope.messages.push(message);
                }

                $scope.remove = function (message) {
                    var index = $scope.messages.indexOf(message);
                    $scope.messages.splice(index, 1);
                }

                $scope.getCssClass = function (message) {
                    if (message.type == "error") {
                        return "danger";
                    }
                    return message.type;
                }

                $scope.$on('messageEvent', handleMessageEvent);
            }
        }
    });
});
