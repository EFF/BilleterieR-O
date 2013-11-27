define(['./module'], function (EventModule) {
    EventModule.controller('EventsController', ['$scope', 'FlashMessage', 'EventService',
        function ($scope, FlashMessage, EventService) {
            $scope.events = null;
            $scope.filters = {};
            $scope.isLoading = false;


            var initPageData = function () {
                var onGetFacetsSuccess = function (facets) {
                    $scope.facets = facets;
                };

                var onGetFacetsError = function () {
                    FlashMessage.send('error', 'Une erreur est survenue.');
                };

                EventService.getFacets().then(onGetFacetsSuccess, onGetFacetsError);

                getEvents();
            };

            var getEvents = function () {
                var onGetEventsSuccess = function (events) {
                    $scope.events = events;
                    $scope.isLoading = false;
                };

                var onGetEventsError = function () {
                    FlashMessage.send('error', 'Une erreur est survenue.');
                    $scope.events = [];
                    $scope.isLoading = false;
                };

                $scope.isLoading = true;

                EventService.getEvents($scope.filters).then(onGetEventsSuccess, onGetEventsError);
            };

            $scope.$watch('filters', function () {
                getEvents();
            }, true);

            initPageData();
        }]);
});
