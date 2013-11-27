define(['./module'], function (UserModule) {
    UserModule.controller('UserController', ['$scope', '$location', 'LoginService', 'FlashMessage', 'UserService',
        function ($scope, $location, LoginService, FlashMessage, UserService) {
            if (LoginService.isLoggedIn) {
                $scope.email = LoginService.username;
            } else {
                $location.path("/");
            }

            $scope.updatePassword = function () {
                if ($scope.password != $scope.passwordConfirmation) {
                    FlashMessage.send("error", "Les deux mots de passe doivent être identiques.")
                }
                else {
                    UserService.updatePassword($scope.actualPassword, $scope.password)
                        .then(onUpdatePasswordSuccess, onUpdatePasswordError);
                }
            };

            $scope.updateEmail = function () {
                var email = $scope.email;
                UserService.updateEmail(email).then(onUpdateEmailSuccess, onUpdateEmailError);
            };

            var onUpdatePasswordSuccess = function () {
                FlashMessage.send("success", "Votre mot de passe a été modifié avec succès.");
            };

            var onUpdatePasswordError = function () {
                FlashMessage.send("error", "Erreur lors de la modification de votre mot de passe.");
            };

            var onUpdateEmailSuccess = function () {
                LoginService.username = $scope.email;
                FlashMessage.send("success", "Votre email a été modifié avec succès.");
            };

            var onUpdateEmailError = function () {
                FlashMessage.send("error", "La modification de votre email a échouée");
            };
        }
    ]);
});
