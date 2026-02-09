# Test technique Merjane - Abdelaaly Bouzaroual

## Travail réalisé
L'objectif principal a été de désendetter techniquement le code existant tout en garantissant le respect des règles métier.

### 1. Refactoring & Architecture
- **Délégation au Service** : J'ai vidé le Controller de sa logique métier pour respecter le SRP (Single Responsibility Principle). Le Controller ne sert plus que de point d'entrée API.
- **Lisibilité** : J'ai refactoré les méthodes `handleSeasonalProduct` et `handleExpiredProduct` en utilisant des variables explicatives pour les conditions de date (ex: `isWithinSeason`). Cela rend le code "auto-documenté".
- **Optimisation** : Réduction des appels `repository.save()` inutiles pour n'en garder qu'un par flux logique.

### 2. Tests
- Utilisation des tests d'intégration comme "filet de sécurité" pendant le refactoring.
- Ajout d'un test unitaire spécifique dans `MyUnitTests` pour valider le cas complexe de la règle SEASONAL (réapprovisionnement hors saison).

## Comment lancer les tests
- `mvn test` pour lancer l'ensemble de la suite.


## Consignes: 
* Ignorez les migrations BDD
* Ne pas modifier les classes qui ont un commentaire: `// WARN: Should not be changed during the exercise
`
* Pour lancer les tests (depuis le sous-répertoire `api`) :
  * unitaires: `mvnw test`
  * integration: `mvnw integration-test`
  * tous: `mvnw verify`