# Annuaire-entreprise-app

Application lourde JavaFX pour annuaire entreprise.

## Stack
- Java 21
- JavaFX
- MySQL 8
- JPA + Hibernate
- Maven
- BCrypt

## Naming valide
- groupId: fr.cesi
- artifactId: annuaire-entreprise-app
- package racine: fr.cesi.annuaire

## Acces admin secret
- Raccourci clavier: Ctrl + Shift + Q

## Structure initiale
- UI JavaFX visiteur avec:
	- recherche par nom
	- filtre site
	- filtre service
	- fiche salarie
- Acces admin protege par login + BCrypt
- Dashboard admin avec CRUD:
	- Sites
	- Services
	- Salaries
- Scripts SQL:
	- schema: src/main/resources/db/schema.sql
	- seed: src/main/resources/db/seed.sql

## Prerequis
1. Java 21
2. Maven 3.9+
3. MySQL 8

## Initialisation base de donnees
1. Executer src/main/resources/db/schema.sql
2. Generer un hash BCrypt pour le mot de passe admin
3. Remplacer REPLACE_WITH_BCRYPT_HASH dans src/main/resources/db/seed.sql
4. Executer src/main/resources/db/seed.sql

Pour generer un hash BCrypt avec le projet:

```bash
mvn -q -DskipTests exec:java -Dexec.mainClass="fr.cesi.annuaire.util.PasswordHashGenerator" -Dexec.args="MonMotDePasse"
```

## Configuration JPA
Modifier les informations de connexion dans src/main/resources/META-INF/persistence.xml:
- url
- user
- password

## Lancer l application

```bash
mvn clean javafx:run
```

## Verification rapide
1. La fenetre principale s ouvre
2. Les filtres site/service se chargent
3. La recherche par nom fonctionne
4. Ctrl+Shift+Q ouvre l authentification admin

## Cahier de test
Template complet disponible dans:
- .vscode/cahier_tests_template.md

Tests unitaires ajoutes:
- src/test/java/fr/cesi/annuaire/DirectoryServiceValidationTest.java