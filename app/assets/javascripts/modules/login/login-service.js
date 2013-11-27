define(['./module'], function (LoginModule) {
    LoginModule.factory('LoginService', ['$http', 'Cart', '$q', function ($http, Cart, $q) {
        var loginService = {};

        loginService.isLoggedIn = false;
        loginService.username;

        loginService.login = function (username, password) {
            var deferred = $q.defer();

            var data = {
                username: username,
                password: password
            };

            var onSuccess = function () {
                setCredentials(true, username);
                deferred.resolve();
            };

            $http.post('/login', data).success(onSuccess).error(deferred.reject);

            return deferred.promise;
        };

        loginService.logout = function () {
            var deferred = $q.defer();

            var onSuccess = function () {
                setCredentials(false, null);
                Cart.removeAllItem();
                deferred.resolve();
            };

            $http.post('/logout').success(onSuccess).error(deferred.reject);

            return deferred.promise;
        };

        var checkAuthenticationState = function () {
            $http.get('/login')
                .success(function (data) {
                    setCredentials(data.authenticated, data.username);
                });
        };

        var setCredentials = function (isLoggedIn, authenticated) {
            loginService.isLoggedIn = isLoggedIn;
            loginService.username = authenticated;
        };

        checkAuthenticationState();

        return loginService;
    }]);
});
