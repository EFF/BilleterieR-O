define(['./module'], function (LoginModule) {
    LoginModule.controller('LoginController', ['$scope', '$location', 'LoginService', 'FlashMessage',
        function ($scope, $location, LoginService, FlashMessage) {
            var onLoginSuccess = function () {
                FlashMessage.send("success", "Connexion à votre compte réussie!");
                $location.path("/events");
            };

            var onLoginError = function () {
                FlashMessage.send("error", "Connexion échouée.");
            };

            $scope.login = function () {
                LoginService.login($scope.username, $scope.password).then(onLoginSuccess, onLoginError);
            };
        }]);
});
