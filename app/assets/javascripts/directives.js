define(['app'], function (app) {
    app.directive('menu', ['Cart', function (Cart) {
        return {
            replace: true,
            templateUrl: 'assets/templates/directives/menu.html',
            restrict: 'E',
            controller: function($scope) {
                $scope.cart = Cart.getItems();
            }
        };
    }]);
    app.directive('flashMessages', function() {
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

                $scope.$on('messageEvent', handleMessageEvent);
            }
        }
    });
});
