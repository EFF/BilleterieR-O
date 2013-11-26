define(['./module'], function (CommonModule) {
    CommonModule.filter('gender', function () {
        return function (input) {
            return (input == "MALE") ? "Masculin" : "Féminin";
        }
    });

    CommonModule.filter('categoryType', function () {
        return function (input) {
            return (input == "SEAT") ? "Siège réservé" : "Admission Générale";
        }
    });
});
