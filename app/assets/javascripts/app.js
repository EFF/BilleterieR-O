define([
    'angular',
    'angularCookies',
    './modules/independent_components/index',
    './modules/flash_message/index',
    './modules/cart/index',
    './modules/event/index',
    './modules/login/index',
    './modules/ticket/index',
    './modules/user/index'
], function (angular) {
    return angular.module('billetterieRO', [
        'ngCookies',
        'IndependentModule',
        'FlashMessageModule',
        'CartModule',
        'EventModule',
        'LoginModule',
        'TicketModule',
        'UserModule'
    ]);
});
