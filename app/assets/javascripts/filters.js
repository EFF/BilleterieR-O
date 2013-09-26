define(['app'], function (app) {
    app.filter('gender', function () {
        return function (input) {
            return (input == "MALE") ? "Masculin" : "Féminin";
        }
    })
});
