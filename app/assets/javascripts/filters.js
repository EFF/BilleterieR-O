define(['app'], function (app) {
    app.filter('gender', function () {
        return function (input) {
            return (input == "MALE") ? "Masculin" : "Féminin";
        }
    })
    app.filter('categoryType', function () {
            return function (input) {
                return (input == "SEAT") ? "Siège réservé" : "Admission Générale";
            }
        })
});
