define(['./module'], function (UserModule) {
    UserModule.controller('UserController', ['$scope', '$location', '$http', 'Login', 'FlashMessage',
        function ($scope, $location, $http, Login, FlashMessage) {
            if (Login.isLoggedIn) {
                $scope.email = Login.username;
            } else {
                $location.path("/");
            }

            $scope.updatePassword = function () {
                if ($scope.password != $scope.passwordConfirmation) {
                    FlashMessage.send("error", "Les deux mots de passe doivent être identiques.")
                    return;
                }
                $http.post('/api/user/password', {
                    actualPassword: $scope.actualPassword,
                    password: $scope.password
                }).success(function () {
                        FlashMessage.send("success", "Votre mot de passe a été modifié avec succès.");
                    }).error(function () {
                        FlashMessage.send("error", "Erreur lors de la modification de votre mot de passe.");
                    });
            };

            $scope.updateEmail = function () {
                var email = $scope.email;
                $http.post('api/user/email', {
                    username: email
                }).success(function () {
                        Login.username = email;
                        FlashMessage.send("success", "Votre email a été modifié avec succès.");
                    }).error(function () {
                        FlashMessage.send("error", "La modification de votre email a échouée");
                    });
            }
        }
    ]);
});
