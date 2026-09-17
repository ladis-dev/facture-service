-- =============================================================================
-- CLIENTS
-- =============================================================================
INSERT INTO
    client (
        nom,
        prenom,
        adresse_facturation,
        adresse_livraison
    )
VALUES (
        'Dupont',
        'Jean',
        '10 Rue de la Paix, 75002 Paris',
        '10 Rue de la Paix, 75002 Paris'
    );

INSERT INTO
    client (
        nom,
        prenom,
        adresse_facturation,
        adresse_livraison
    )
VALUES (
        'Martin',
        'Sophie',
        '25 Avenue des Champs-Élysées, 75008 Paris',
        '3 Quai de la Loire, 44000 Nantes'
    );

INSERT INTO
    client (
        nom,
        prenom,
        adresse_facturation,
        adresse_livraison
    )
VALUES (
        'Bernard',
        'Thomas',
        '8 Place Bellecour, 69002 Lyon',
        '8 Place Bellecour, 69002 Lyon'
    );

-- =============================================================================
-- PRODUITS
-- =============================================================================
INSERT INTO
    produits (libelle, description)
VALUES (
        'Ecran 27 pouces',
        'Ecran idéal pour la bureautique'
    );

INSERT INTO
    produits (libelle, description)
VALUES ('Clavier', 'Clavier filaire');

INSERT INTO
    produits (libelle, description)
VALUES (
        'Souris Sans Fil',
        'Souris sans fil'
    );

INSERT INTO
    produits (libelle, description)
VALUES (
        'Casque Audio Bluetooth',
        'Casque avec 30h d autonomie'
    );

-- =============================================================================
-- FOURNISSEURS
-- =============================================================================
INSERT INTO
    fournisseur (nom_entreprise, adresse)
VALUES (
        'TechDistro Corp',
        '120 Boulevard Haussmann, 75008 Paris'
    );

INSERT INTO
    fournisseur (nom_entreprise, adresse)
VALUES (
        'Global Electronics',
        '45 Rue du Commerce, 69003 Lyon'
    );

-- =============================================================================
-- FOURNISSEUR_PRODUIT (Table de liaison avec attributs)
-- =============================================================================
-- TechDistro vend l'écran, le clavier et la souris
INSERT INTO
    fournisseur_produit (
        fk_fournisseur_id,
        fk_produit_id,
        prix_fournisseur,
        stock_fournisseur
    )
VALUES (1, 1, 299.99, 50);

INSERT INTO
    fournisseur_produit (
        fk_fournisseur_id,
        fk_produit_id,
        prix_fournisseur,
        stock_fournisseur
    )
VALUES (1, 2, 79.50, 120);

INSERT INTO
    fournisseur_produit (
        fk_fournisseur_id,
        fk_produit_id,
        prix_fournisseur,
        stock_fournisseur
    )
VALUES (1, 3, 45.00, 200);

-- Global Electronics vend l'écran (moins cher) et le casque
INSERT INTO
    fournisseur_produit (
        fk_fournisseur_id,
        fk_produit_id,
        prix_fournisseur,
        stock_fournisseur
    )
VALUES (2, 1, 280.00, 15);

INSERT INTO
    fournisseur_produit (
        fk_fournisseur_id,
        fk_produit_id,
        prix_fournisseur,
        stock_fournisseur
    )
VALUES (2, 4, 129.90, 80);

-- =============================================================================
-- FACTURES
-- =============================================================================
-- Factures pour Jean Dupont (ID 1)
INSERT INTO facture (fk_client_id, statut) VALUES (1, 'PAYEE');

INSERT INTO facture (fk_client_id, statut) VALUES (1, 'ENVOYEE');

-- Facture pour Sophie Martin (ID 2)
INSERT INTO facture (fk_client_id, statut) VALUES (2, 'CREATION');

-- =============================================================================
-- PRODUITS_FACTURE (Lignes de facture)
-- =============================================================================
-- Facture 1 (Payée) : 1 écran 4K (TechDistro) + 1 souris (TechDistro)
INSERT INTO
    produits_facture (
        fk_facture_id,
        fk_produit_id,
        fk_fournisseur_id,
        quantite,
        prix_unitaire
    )
VALUES (1, 1, 1, 1, 300);

INSERT INTO
    produits_facture (
        fk_facture_id,
        fk_produit_id,
        fk_fournisseur_id,
        quantite,
        prix_unitaire
    )
VALUES (1, 3, 1, 1, 45);

-- Facture 2 (Envoyée) : 2 claviers (TechDistro)
INSERT INTO
    produits_facture (
        fk_facture_id,
        fk_produit_id,
        fk_fournisseur_id,
        quantite,
        prix_unitaire
    )
VALUES (2, 2, 1, 2, 80);

-- Facture 3 (En création) : 1 casque Bluetooth (Global Electronics)
INSERT INTO
    produits_facture (
        fk_facture_id,
        fk_produit_id,
        fk_fournisseur_id,
        quantite,
        prix_unitaire
    )
VALUES (3, 4, 2, 1, 130);

-- =============================================================================
-- FILES (Pièces jointes)
-- =============================================================================
-- INSERT INTO
--     fichiers (
--         fk_facture_id,
--         type_file,
--         path
--     )
-- VALUES (
--         1,
--         'PDF',
--         '/storage/factures/2026/FAC-0001.pdf'
--     );

-- INSERT INTO
--     fichiers (
--         fk_facture_id,
--         type_file,
--         path
--     )
-- VALUES (
--         1,
--         'RECU',
--         '/storage/recus/2026/REC-0001.pdf'
--     );

-- INSERT INTO
--     fichiers (
--         fk_facture_id,
--         type_file,
--         path
--     )
-- VALUES (
--         2,
--         'PDF',
--         '/storage/factures/2026/FAC-0002.pdf'
--     );