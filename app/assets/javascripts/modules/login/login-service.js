define(['./module'], function (LoginModule) {
    LoginModule.factory('LoginService', ['$http', 'Cart', '$q', function ($http, Cart, $q) {
        var loginService = {
            username: null
        };

        loginService.isLoggedIn = function () {
            return !!loginService.username;
        };

        loginService.login = function (username, password) {
            var deferred = $q.defer();

            var data = {
                username: username,
                password: password
            };

            var onLoginSuccess = function () {
                setCredentials(username);
                deferred.resolve();
            };

            $http.post('/login', data).success(onLoginSuccess).error(deferred.reject);

            return deferred.promise;
        };

        loginService.logout = function () {
            var deferred = $q.defer();

            var onLogoutSuccess = function () {
                setCredentials(null);
                Cart.removeAllItem();
                deferred.resolve();
            };

            $http.post('/logout').success(onLogoutSuccess).error(deferred.reject);

            return deferred.promise;
        };

        var checkAuthenticationState = function () {
            var onCheckAuthenticationStateSuccess = function (result) {
                setCredentials(result.username);
            };

            var onCheckAuthenticationStateError = function () {
                //Ignored
            };

            $http.get('/login').success(onCheckAuthenticationStateSuccess).error(onCheckAuthenticationStateError);
        };

        var setCredentials = function (username) {
            loginService.username = username;
        };

        checkAuthenticationState();

        return loginService;
    }]);
});
