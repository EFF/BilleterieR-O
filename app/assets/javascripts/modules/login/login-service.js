define(['./module'], function (LoginModule) {
    LoginModule.factory('Login', ['$http', function ($http) {
        var exports = {};

        exports.isLoggedIn = false;
        exports.username;

        exports.login = function (username, password, successCallback, errorCallback) {
            var data = {username: username, password: password};

            $http.post('/login', data, {})
                .success(function () {
                    setCredentials(true, username);
                    successCallback();
                })
                .error(errorCallback);
        };

        exports.logout = function () {
            $http.post('/logout', {}, {})
                .success(function () {
                    setCredentials(false, null);
                });
        };

        var checkAuthenticationState = function () {
            $http.get('/login')
                .success(function (data) {
                    setCredentials(data.authenticated, data.username);
                });
        };

        var setCredentials = function (isLoggedIn, authenticated) {
            exports.isLoggedIn = isLoggedIn;
            exports.username = authenticated;
        };

        checkAuthenticationState();

        return exports;
    }]);
});
