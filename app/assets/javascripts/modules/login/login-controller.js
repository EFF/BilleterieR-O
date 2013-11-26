define(['./module'], function (LoginModule) {
    LoginModule.controller('LoginController', ['$scope', '$location', 'Login', 'FlashMessage',
        function ($scope, $location, Login, FlashMessage) {

            var loginSuccess = function () {
                FlashMessage.send("success", "Connexion à votre compte réussie!");
                $location.path("/events");
            };

            var loginFailed = function () {
                FlashMessage.send("error", "Connexion échouée.");
            };

            $scope.login = function () {
                Login.login($scope.username, $scope.password, loginSuccess, loginFailed);
            };

        }]);
});
