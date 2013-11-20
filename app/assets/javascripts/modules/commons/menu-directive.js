define(['./module'], function (AppModule) {
    AppModule.directive('menu', ['Cart', 'Login', function (Cart, Login) {
        return {
            replace: true,
            templateUrl: 'assets/templates/directives/menu.html',
            restrict: 'E',
            controller: function ($scope) {
                $scope.getTotalQuantity = Cart.getTotalQuantity;

                $scope.$watch(function () {
                    return Login.isLoggedIn;
                }, function (isLoggedIn) {
                    $scope.isLoggedIn = isLoggedIn;
                }, false);

                $scope.$watch(function () {
                    return Login.username;
                }, function (username) {
                    $scope.username = username;
                }, false);

                $scope.logout = Login.logout;
            }
        };
    }]);
});
