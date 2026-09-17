# FacturX Management API — Service Web REST JAX-RS

Ce projet est un service web RESTful développé en Java natif avec JAX-RS, conçu sans l'utilisation du framework Spring. Il permet la gestion complète de la facturation d'une entreprise, de la création à l'archivage, incluant le suivi de stock fournisseur en temps réel ainsi que la génération et le téléchargement de factures au format hybride Facture-X (PDF/A-3 avec métadonnées XML) et PDF standard.

---

## 1. Objectifs du projet

- Absence de Spring Framework : Utilisation d'un conteneur JAX-RS léger (Jersey) et d'un ORM Hibernate JPA.
- Respect des bonnes pratiques (Clean Code & MVC) : Séparation claire entre les contrôleurs REST, les services métier, les accès données (DAO/Repositories) et les entités.
- Norme Facture-X : Génération de documents PDF conformes à la réglementation européenne de facturation électronique.
- Exposition OpenAPI : Documentation interactive via Swagger UI.
- Conteneurisation : Déploiement simplifié à l'aide de Docker et Docker Compose.

---

## 2. Modèle de données (BDD)

Le modèle s'appuie sur la structure relationnelle suivante :

```txt
+---------------------+       +---------------------+       +---------------------+
|       CLIENT        |       |       FACTURE       |       |        FILES        |
+---------------------+       +---------------------+       +---------------------+
| ID                  |1     *| ID                  |1     *| ID                  |
| ADRESSE_FACTURATION |<------| FK_CLIENT_ID        |<------| FK_FACTURE_ID       |
| ADRESSE_LIVRAISON   |       | STATE               |       | PATH                |
| NOM                 |       +---------------------+       | TYPE_FILE           |
| PRENOM              |                 / \ 1               +---------------------+
+---------------------+                  |
                                         | *
                              +---------------------+
                              |  PRODUITS_FACTURE   |
                              +---------------------+
                            * | FK_FACTURE_ID       |
            ------------------| FK_FOURNISSEUR_ID   | *
           |                  | FK_PRODUIT_ID       |------------------
           |                  | PRIX_UNITAIRE       |                  |
           |                  | QUANTITE            |                  |
           |                  +---------------------+                  |
          \_/ 1                                                       \_/ 1
+---------------------+       +---------------------+       +---------------------+
|     FOURNISSEUR     |       | FOURNISSEUR_PRODUIT |       |      PRODUITS       |
+---------------------+       +---------------------+       +---------------------+
| ID                  | 1   * | FK_FOURNISSEUR_ID   | *   1 | ID                  |
| ADRESSE             |<------| FK_PRODUIT_ID       |------>| DESCRIPTION         |
| NOM_ENTREPRISE      |       | PRIX_FOURNISSEUR    |       | LIBELLE             |
+---------------------+       | STOCK_FOURNISSEUR   |       +---------------------+
                              +---------------------+

```

---

## 3. Points de validation & Fonctionnalités requises

- CRUD Facture & Génération : Endpoints de création, lecture, mise à jour et suppression de factures + génération automatique des fichiers PDF / Facture-X lors de la validation.
- Automatisation des saisies : Scripts d'automatisation (Selenium / Scripts d'intégration) pour rejouer des scénarios de création de factures.
- Cycle de vie des factures : Gestion des transitions d'état : CREATION -> ENVOYEE -> PAYEE.
- Stock fournisseur en temps réel : Vérification dynamique de la disponibilité des produits chez les fournisseurs associés avant validation d'une facture.
- Calcul du montant : Service dédié calculant le montant total HT/TTC d'une facture sur la base des quantités et prix fournisseurs.
- Téléchargement des fichiers : Export direct du fichier PDF / Facture-X associé à une facture.
- Espace Client : Consultation des factures d'un client sous forme de liste incluant les liens directs de téléchargement.
- Documentation Swagger : Interface Swagger UI générée via OpenAPI/JAX-RS.
- Tests Unitaires : Couverture de la logique métier (calculs, états, règles de stock) avec JUnit 5 & Mockito.

---

## 4. Architecture technique

- Langage : Java 21
- Framework REST : Jakarta RESTful Web Services (JAX-RS / Jersey avec serveur HTTP JDK natif & HK2)
- Persistance : JPA / Hibernate (H2 Database)
- Génération PDF / Facture-X : Apache PDFBox / Mustangproject (librairie Facture-X)
- Tests & Automatisation : JUnit 5, Mockito, Selenium WebDriver / Shell
- Documentation : OpenAPI 3.0 / Swagger-UI
- DevOps : Docker, Docker Compose, Maven

---

## 5. Guide d'installation et de lancement

### Prérequis

- Docker et Docker Compose installés OU
- JDK 21+ et Maven 3.8+ pour un lancement local sans conteneur.

---

### Option A : Lancement via Docker

1. Cloner le projet :
   git clone <URL_DU_DEPOT>
   cd <NOM_DU_PROJET>

2. Démarrer l'application avec Docker Compose :
   docker compose up --build -d

3. L'API et OpenAPI seront accessibles sur <http://localhost:8080>.

---

### Option B : Lancement avec Maven

1. Exécuter l'ensemble des tests (unitaires et intégration) :
   mvn verify

2. Lancer le serveur d'application :
   mvn clean compile exec:java

---

## 6. Interaction avec l'API

### 1. Documentation OpenAPI / Swagger

L'interface OpenAPI permet de consulter les endpoints directement depuis un navigateur :

- URL OpenAPI : <http://localhost:8080/openapi.json>
- URL SwaggerUI : <http://localhost:8080/swagger-ui>

---

### 2. Endpoints principaux

Factures (/api/v1/factures) :

- POST /api/v1/factures : Créer une facture à l'état CREATION.
- GET /api/v1/factures/{id} : Récupérer le détail d'une facture.
- PUT /api/v1/factures/{id} : Mettre à jour le statut ou le client d'une facture.

Fichiers (/api/v1/fichiers) :

- GET /api/v1/fichiers/{id}/download : Télécharger le fichier généré (PDF ou Facture-X).

Clients (/api/v1/clients) :

- GET /api/v1/clients/{clientId}/factures : Lister toutes les factures d'un client avec leurs liens de téléchargement respectifs.

Produits & Stocks (/api/v1/produits) :

- GET /api/v1/produits/{produitId}/stock : Vérifier le stock disponible en temps réel pour un produit donné.

---

### 3. Démonstration automatique

Un script d'intégration complet est fourni pour rejouer l'ensemble des cas d'usage (création, vérification de stock, changement d'état, génération et téléchargement de fichiers) :

./demo.sh

---

## 7. Automatisation & Tests

### Saisie automatisée & Scénario d'intégration

L'automatisation du flux complet de facturation (création, cycle de vie, gestion des stocks et téléchargement) est assurée à deux niveaux :

- **Script shell d'intégration (`./demo.sh`)** : Simule les requêtes HTTP de bout en bout, de la saisie à la récupération des fichiers PDF/Facture-X générés.
- **Test d'intégration Java (`FactureLifecycleIT`)** : Valide la chaîne applicative complète durant le build Maven.

### Tests Unitaires & Intégration (JUnit 5 & Mockito)

Le projet sépare clairement les tests unitaires et les tests d'intégration :

- **Tests d'intégration (`com.example.integration`) :**
  - `FactureLifecycleIT.java` : Validation du cycle de vie d'une facture.

- **Tests unitaires (`com.example.unitaire`) :**
  - `FactureCalculTest.java` : Calculs des montants et des totaux.
  - `FactureGenerationServiceTest.java` : Génération des fichiers PDF / Facture-X.
  - `FactureServiceTest.java` : Logique métier et transitions d'état.
  - `ProduitFactureServiceTest.java` : Gestion des lignes de factures et des stocks.

Pour exécuter l'ensemble de la suite de tests (unitaires et d'intégration) :

```bash
mvn verify
```
