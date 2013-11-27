define(['./module'], function (UserModule) {
    UserModule.factory('UserService', ['$http', '$q', function ($http, $q) {
        return {
            updatePassword: function (actualPassword, newPassword) {
                var deferred = $q.defer();
                var data = {
                    actualPassword: actualPassword,
                    password: newPassword
                };
                $http.post('/api/user/password', data).success(deferred.resolve).error(deferred.reject);

                return deferred.promise;
            },

            updateEmail: function (email) {
                var deferred = $q.defer();
                var data = {
                    username: email
                };
                $http.post('api/user/email', data).success(deferred.resolve).error(deferred.reject);

                return deferred.promise;
            }
        };
    }]);
});
