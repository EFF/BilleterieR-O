require.config({
    baseUrl: '/assets/javascripts/',
    paths: {
        jQuery: '//ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min',
        angular: '//ajax.googleapis.com/ajax/libs/angularjs/1.0.8/angular.min',
        angularCookies: '//ajax.googleapis.com/ajax/libs/angularjs/1.0.8/angular-cookies.min'
    },
    shim: {
        jQuery: {exports: 'jQuery'},
        angular: {exports: 'angular'},
        angularCookies: {deps: ['angular']}
    }
});

require(['routes'], function () {
});
