define([
    'angular',
    'angularCookies',
    './modules/commons/index',
    './modules/flash_message/index',
    './modules/cart/index',
    './modules/event/index',
    './modules/login/index',
    './modules/ticket/index',
    './modules/user/index'
], function (angular) {
    return angular.module('billetterieRO', [
        'ngCookies',
        'CommonModule',
        'FlashMessageModule',
        'CartModule',
        'EventModule',
        'LoginModule',
        'TicketModule',
        'UserModule'
    ]);
});
