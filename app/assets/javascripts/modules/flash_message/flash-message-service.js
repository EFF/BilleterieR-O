define(['./module'], function (FlashMessageModule) {
    FlashMessageModule.factory('FlashMessage', ['$rootScope', function ($rootScope) {
        var exports = {};

        var typeAsString = function (type) {
            if (type == "error") return "Erreur";
            else if (type == "info") return "Informations";
            else if (type == "warning") return "Attention";
            else return "Succès";
        }

        exports.send = function (type, message) {
            var message = {
                type: type,
                title: typeAsString(type),
                content: message
            };

            $rootScope.$broadcast('messageEvent', message);
        };

        return exports;
    }]);
});
