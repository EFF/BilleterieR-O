define(['./module'], function (AppModule) {
    AppModule.filter('gender', function () {
        return function (input) {
            return (input == "MALE") ? "Masculin" : "Féminin";
        }
    });
    AppModule.filter('categoryType', function () {
        return function (input) {
            return (input == "SEAT") ? "Siège réservé" : "Admission Générale";
        }
    })
});
