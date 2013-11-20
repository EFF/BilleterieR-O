define(['./module'], function (AppModule) {
    AppModule.controller('ThanksController', ['$scope', 'Cart', function ($scope, Cart) {
        $scope.getTransactionId = function () {
            return Cart.transactionId;
        }
    }]);
});
