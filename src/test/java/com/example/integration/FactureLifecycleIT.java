package com.example.integration;

import java.math.BigDecimal;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FactureLifecycleIT {

    private static Long fournisseurId;
    private static Long produitId;
    private static Long clientId;
    private static Long factureId;

    @BeforeAll
    static void setup() {
        RestAssured.baseURI = "http://localhost:8080/api/v1";
    }

    @Test
    @Order(1)
    void creerFournisseur() {
        fournisseurId = given()
                .contentType(ContentType.JSON)
                .body("""
                        {"nomEntreprise": "ACME", "adresse": "1 rue du Test"}
                        """)
                .when().post("/fournisseurs")
                .then().statusCode(201)
                .extract().jsonPath().getLong("id");
    }

    @Test
    @Order(2)
    void creerProduit() {
        produitId = given()
                .contentType(ContentType.JSON)
                .body("""
                        {"libelle": "Clavier", "description": "Clavier mécanique"}
                        """)
                .when().post("/produits")
                .then().statusCode(201)
                .extract().jsonPath().getLong("id");
    }

    @Test
    @Order(3)
    void creerOffreFournisseur() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {"prixFournisseur": 49.90, "stockFournisseur": 100}
                        """)
                .when().post("/fournisseurs/{fid}/produits/{pid}", fournisseurId, produitId)
                .then().statusCode(201);
    }

    @Test
    @Order(4)
    void verifierStockTempsReel() {
        given()
                .when().get("/produits/{pid}/stock", produitId)
                .then().statusCode(200)
                .body("[0].stockFournisseur", equalTo(100));
    }

    @Test
    @Order(5)
    void creerClient() {
        clientId = given()
                .contentType(ContentType.JSON)
                .body("""
                        {"nom": "Dupont", "prenom": "Jean",
                         "adresseLivraison": "2 rue A", "adresseFacturation": "2 rue A"}
                        """)
                .when().post("/clients")
                .then().statusCode(201)
                .extract().jsonPath().getLong("id");
    }

    @Test
    @Order(6)
    void creerFacture() {
        factureId = given()
                .contentType(ContentType.JSON)
                .body("{\"clientId\": " + clientId + ", \"statut\": \"CREATION\"}")
                .when().post("/factures")
                .then().statusCode(201)
                .extract().jsonPath().getLong("id");
    }

    @Test
    @Order(7)
    void ajouterLigneEtVerifierDecrementStock() {
        given()
                .contentType(ContentType.JSON)
                .body("{\"produitId\": " + produitId + ", \"quantite\": 3}")
                .when().post("/factures/{id}/produits", factureId)
                .then().statusCode(201)
                .body("prixUnitaire", equalTo(49.9f));

        given()
                .when().get("/produits/{pid}/stock", produitId)
                .then().body("[0].stockFournisseur", equalTo(97));
    }

    @Test
    @Order(8)
    void verifierMontantTotal() {
        BigDecimal montant = given()
                .when().get("/factures/{id}/produits/total", factureId)
                .then().statusCode(200)
                .extract().as(BigDecimal.class);

        assertEquals(0, montant.compareTo(new BigDecimal("149.70")));
    }

    @Test
    @Order(9)
    void passerAEnvoyeeEtGenererPdf() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {"clientId": %d, "statut": "ENVOYEE"}
                        """.formatted(clientId))
                .when()
                .put("/factures/{id}", factureId)
                .then()
                .statusCode(200)
                .body("statut", equalTo("ENVOYEE"));

        given()
                .when()
                .post("/factures/{id}/generer", factureId)
                .then()
                .statusCode(201)
                .body("typeFile", equalTo("FACTURX_PDF"));
    }

    @Test
    @Order(10)
    void transitionInvalideRejetee() {
        // ENVOYEE -> CREATION : doit échouer
        given()
                .contentType(ContentType.JSON)
                .body("{\"clientId\": " + clientId + ", \"statut\": \"CREATION\"}")
                .when().put("/factures/{id}", factureId)
                .then().statusCode(400);
    }

    @Test
    @Order(11)
    void listerFacturesDuClientAvecLienTelechargement() {
        given()
                .when().get("/clients/{id}/factures", clientId)
                .then().statusCode(200)
                .body("[0].downloadUrl", notNullValue());
    }

    @Test
    @Order(12)
    void telechargerLeFichier() {
        given()
                .when().get("/clients/{id}/factures", clientId)
                .then().statusCode(200);
        // récupère le downloadUrl puis vérifie le téléchargement effectif
        String url = given()
                .when().get("/clients/{id}/factures", clientId)
                .then().extract().jsonPath().getString("[0].downloadUrl");

        given()
                .baseUri("http://localhost:8080")
                .when().get(url)
                .then().statusCode(200)
                .header("Content-Type", containsString("application/pdf"));
    }
}