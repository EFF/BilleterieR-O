# Billetterie R&O

Projet pilote développé dans le cadre du cours GLO-4003 Architecture logicielle à l'université Laval. Billetterie R&O est développé en Java et utilise [Play! framework](http://www.playframework.com/) pour serveur et [Angular.js](http://angularjs.org) pour le client web.
Nous avons opté pour un projet suivant le style "slim API & fat client".

## Pré-requis
* Java 7
* [Play! framework 2.1.4](http://www.playframework.com/download)

## Lancer l'app
1. ```/path/to/play_2.1.4/play run```
2. ```curl localhost:9000/bootstrap``` (initialisation des données)


## Lancer les tests
1. Assurez-vous d'avoir une version récente de firefox pour pouvoir rouler les tests d'acceptation
2. ```/path/to/play_2.1.4/play test```

## Utiliser Eclipse
    /path/to/play_2.1.4/play eclipse

## Utiliser Intellij
    /path/to/play_2.1.4/play idea

## Utiliser Netbeans
    /path/to/play_2.1.4/play netbeans
