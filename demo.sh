#!/usr/bin/env bash
set -e

BASE_URL="http://localhost:8080/api/v1"
SERVER_URL="http://localhost:8080"

# Fonction d'extraction d'une valeur JSON simple sans outil externe
json_val() {
    local key="$1"
    local json="$2"
    echo "$json" | grep -o "\"$key\":[^,}]*" | head -n 1 | cut -d':' -f2 | tr -d ' "'
}

echo "================================================="
echo "  1. Création Fournisseur, Produit et Offre"
echo "================================================="
RES_FOURNISSEUR=$(curl -s -X POST "$BASE_URL/fournisseurs" \
  -H "Content-Type: application/json" \
  -d '{"nomEntreprise": "ACME Corp", "adresse": "10 Rue du Test"}')
FOURNISSEUR_ID=$(json_val "id" "$RES_FOURNISSEUR")
echo "Fournisseur créé (ID: $FOURNISSEUR_ID) : $RES_FOURNISSEUR"

RES_FOURNISSEUR2=$(curl -s -X POST "$BASE_URL/fournisseurs" \
  -H "Content-Type: application/json" \
  -d '{"nomEntreprise": "Global Tech", "adresse": "20 Rue du Microprocesseur"}')
FOURNISSEUR2_ID=$(json_val "id" "$RES_FOURNISSEUR2")
echo "Fournisseur créé (ID: $FOURNISSEUR2_ID) : $RES_FOURNISSEUR2"

RES_PRODUIT=$(curl -s -X POST "$BASE_URL/produits" \
  -H "Content-Type: application/json" \
  -d '{"libelle": "Ecran", "description": "Ecran 27 pouces"}')
PRODUIT_ID=$(json_val "id" "$RES_PRODUIT")
echo "Produit créé (ID: $PRODUIT_ID) : $RES_PRODUIT"

RES_PRODUIT2=$(curl -s -X POST "$BASE_URL/produits" \
  -H "Content-Type: application/json" \
  -d '{"libelle": "Clavier", "description": "Clavier Sans Fil"}')
PRODUIT2_ID=$(json_val "id" "$RES_PRODUIT2")
echo "Produit créé (ID: $PRODUIT2_ID) : $RES_PRODUIT2"

echo "Création offre fournisseur :"
curl -s -X POST "$BASE_URL/fournisseurs/$FOURNISSEUR_ID/produits/$PRODUIT_ID" \
  -H "Content-Type: application/json" \
  -d '{"prixFournisseur": 299.99, "stockFournisseur": 50}'
echo ""

curl -s -X POST "$BASE_URL/fournisseurs/$FOURNISSEUR2_ID/produits/$PRODUIT2_ID" \
  -H "Content-Type: application/json" \
  -d '{"prixFournisseur": 89.90, "stockFournisseur": 30}'
echo ""

echo -e "\n================================================="
echo "  2. Vérification Stock Temps Réel"
echo "================================================="
curl -s -X GET "$BASE_URL/produits/$PRODUIT_ID/stock"
echo ""
curl -s -X GET "$BASE_URL/produits/$PRODUIT2_ID/stock"
echo ""

echo -e "\n================================================="
echo "  3. Création Client et Facture (État: CREATION)"
echo "================================================="
RES_CLIENT=$(curl -s -X POST "$BASE_URL/clients" \
  -H "Content-Type: application/json" \
  -d '{"nom": "Lovelace", "prenom": "Ada", "adresseLivraison": "5 Ave Algorithme", "adresseFacturation": "5 Ave Algorithme"}')
CLIENT_ID=$(json_val "id" "$RES_CLIENT")

RES_FACTURE=$(curl -s -X POST "$BASE_URL/factures" \
  -H "Content-Type: application/json" \
  -d "{\"clientId\": $CLIENT_ID, \"statut\": \"CREATION\"}")
FACTURE_ID=$(json_val "id" "$RES_FACTURE")

RES_FACTURE2=$(curl -s -X POST "$BASE_URL/factures" \
  -H "Content-Type: application/json" \
  -d "{\"clientId\": $CLIENT_ID, \"statut\": \"CREATION\"}")
FACTURE2_ID=$(json_val "id" "$RES_FACTURE2")

echo "Client créé (ID: $CLIENT_ID), Facture créée (ID: $FACTURE_ID, $FACTURE2_ID)"

echo -e "\n================================================="
echo "  4. Ajout de Ligne & Impact sur Stock"
echo "================================================="
# Facture 1 : 2 Ecrans + 1 Clavier
echo "-> Ajout d'Ecran (x2) à la facture $FACTURE_ID :"
curl -s -X POST "$BASE_URL/factures/$FACTURE_ID/produits" \
  -H "Content-Type: application/json" \
  -d "{\"produitId\": $PRODUIT_ID, \"quantite\": 2}"
echo ""

echo "-> Ajout de Clavier (x1) à la facture $FACTURE_ID :"
curl -s -X POST "$BASE_URL/factures/$FACTURE_ID/produits" \
  -H "Content-Type: application/json" \
  -d "{\"produitId\": $PRODUIT2_ID, \"quantite\": 1}"
echo ""

# Facture 2 : 1 Clavier
echo "-> Ajout de Clavier (x1) à la facture $FACTURE2_ID :"
curl -s -X POST "$BASE_URL/factures/$FACTURE2_ID/produits" \
  -H "Content-Type: application/json" \
  -d "{\"produitId\": $PRODUIT2_ID, \"quantite\": 1}"

echo -e "\n--> Vérification du stock décrémenté :"
curl -s -X GET "$BASE_URL/produits/$PRODUIT_ID/stock"
echo ""
curl -s -X GET "$BASE_URL/produits/$PRODUIT2_ID/stock"
echo ""

echo -e "\n================================================="
echo "  5. Consultation Montant Total Facture"
echo "================================================="
MONTANT=$(curl -s -X GET "$BASE_URL/factures/$FACTURE_ID/produits/total")
echo "Montant total calculé : $MONTANT €, facture ID : $FACTURE_ID"

MONTANT2=$(curl -s -X GET "$BASE_URL/factures/$FACTURE2_ID/produits/total")
echo "Montant total calculé : $MONTANT2 €, facture ID : $FACTURE2_ID"

echo -e "\n================================================="
echo "  6. Cycle de Vie : Passage à ENVOYEE"
echo "================================================="
curl -s -X PUT "$BASE_URL/factures/$FACTURE_ID" \
  -H "Content-Type: application/json" \
  -d "{\"clientId\": $CLIENT_ID, \"statut\": \"ENVOYEE\"}"
echo ""

curl -s -X PUT "$BASE_URL/factures/$FACTURE2_ID" \
  -H "Content-Type: application/json" \
  -d "{\"clientId\": $CLIENT_ID, \"statut\": \"ENVOYEE\"}"
echo ""

echo -e "\n================================================="
echo "  7. Génération Factur-X / PDF"
echo "================================================="
curl -s -X POST "$BASE_URL/factures/$FACTURE_ID/generer"
echo ""
curl -s -X POST "$BASE_URL/factures/$FACTURE2_ID/generer"
echo ""

echo -e "\n================================================="
echo "  8. Liste Factures du Client & Lien Téléchargement"
echo "================================================="
FACTURES_JSON=$(curl -s -X GET "$BASE_URL/clients/$CLIENT_ID/factures")
echo "$FACTURES_JSON"

# DOWNLOAD_URL=$(json_val "downloadUrl" "$FACTURES_JSON")

# echo -e "\n================================================="
# echo "  9. Téléchargement Effectif du PDF"
# echo "================================================="
# echo "URL cible : $SERVER_URL$DOWNLOAD_URL"
# curl -s -o "facture_demo_$FACTURE_ID.pdf" "$SERVER_URL$DOWNLOAD_URL"

# echo -e "\nFichier téléchargé avec succès : facture_demo_$FACTURE_ID.pdf"

echo -e "\n================================================="
echo "  9. Téléchargement Effectif de TOUTES les Factures"
echo "================================================="
# Extraction de toutes les URL de téléchargement (une par ligne) sans 'jq'
URLS=$(echo "$FACTURES_JSON" | grep -o '"downloadUrl":[^,}]*' | cut -d':' -f2 | tr -d ' "')

i=1
for URL in $URLS; do
    echo "Téléchargement du fichier $i : $SERVER_URL$URL"
    curl -s -o "facture_client_${CLIENT_ID}_num_${i}.pdf" "$SERVER_URL$URL"
    echo " -> Enregistré sous : facture_client_${CLIENT_ID}_num_${i}.pdf"
    i=$((i + 1))
done