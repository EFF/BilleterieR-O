define(['./module'], function (CommonModule) {
    CommonModule.directive('menu', ['Cart', 'LoginService', function (Cart, LoginService) {
        return {
            replace: true,
            templateUrl: 'assets/templates/directives/menu.html',
            restrict: 'E',
            controller: function ($scope) {
                $scope.getTotalQuantity = Cart.getTotalQuantity;

                $scope.$watch(function () {
                    return LoginService.isLoggedIn;
                }, function (isLoggedIn) {
                    $scope.isLoggedIn = isLoggedIn;
                });

                $scope.$watch(function () {
                    return LoginService.username;
                }, function (username) {
                    $scope.username = username;
                }, false);

                $scope.logout = LoginService.logout;
            }
        };
    }]);
});
