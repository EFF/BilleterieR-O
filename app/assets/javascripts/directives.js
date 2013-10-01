define(['app'], function (app) {
    app.directive('mainmenu', ['Cart', function (Cart) {
        return {
            replace: true,
            templateUrl: 'assets/templates/directives/mainmenu.html',
            restrict: 'E',
            link: function(scope) {
                scope.cart = Cart.getItems();
            }
        };
    }]);
});
