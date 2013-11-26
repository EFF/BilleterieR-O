define(['./module'], function (UserModule) {
    UserModule.controller('UserController', ['$scope', '$location', '$http', 'Login', 'FlashMessage', 'UserService',
        function ($scope, $location, $http, Login, FlashMessage, UserService) {
            if (Login.isLoggedIn) {
                $scope.email = Login.username;
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
                Login.username = email;
                FlashMessage.send("success", "Votre email a été modifié avec succès.");
            };

            var onUpdateEmailError = function () {
                FlashMessage.send("error", "La modification de votre email a échouée");
            };
        }
    ]);
});
