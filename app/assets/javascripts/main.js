require.config({
    baseUrl: '/assets/javascripts/',
    paths: {
        angular: '//ajax.googleapis.com/ajax/libs/angularjs/1.0.8/angular.min',
        angularCookies: '//ajax.googleapis.com/ajax/libs/angularjs/1.0.8/angular-cookies.min'
    },
    shim: {
        angular: {exports: 'angular'},
        angularCookies: {deps: ['angular']}
    }
});

require(['routes'], function () {
});
