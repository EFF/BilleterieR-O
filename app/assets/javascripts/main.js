require.config({
    baseUrl: '/assets/javascripts/',
    paths: {
        jQuery: '//ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min',
        angular: '//ajax.googleapis.com/ajax/libs/angularjs/1.0.8/angular.min'
    },
    shim: {
        'angular' : {'exports' : 'angular'},
        'jQuery': {'exports' : 'jQuery'}
    }
});

require(['jQuery', 'angular', 'routes'], function($, angular, routes) {
    $(function () {
        angular.bootstrap(document, ['billetterieRO']);
    });
});
