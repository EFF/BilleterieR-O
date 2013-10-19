# Billetterie R&O

Projet pilote développé dans le cadre du cours GLO-4003 Architecture logicielle à l'université Laval. Billetterie R&O est développé en Java et utilise [Play! framework](http://www.playframework.com/) pour serveur et [Angular.js](http://angularjs.org) pour le client web.
Nous avons opté pour un projet suivant le style "slim API & fat client".

## Pré-requis
* Java 7
* [Play! framework 2.1.4](http://downloads.typesafe.com/play/2.1.4/play-2.1.4.zip)


## Ajout de Play à votre PATH (facultatif)
1. Vous pouvez ajouter `play` à votre `PATH`
2. sinon `cd path_to_/play-2.1.4`

## Lancer l'application
1. `play run`
2. `curl localhost:9000/bootstrap` (initialisation des données en mode développement)
3. `open http://localhost:9000/`

## Lancer les tests
1. Assurez-vous d'avoir une version récente de firefox pour pouvoir rouler les tests d'acceptation
2. `play test`

## Utiliser Eclipse
`play eclipse`

## Utiliser Intellij
`play idea`

## Utiliser Netbeans
`play netbeans`
