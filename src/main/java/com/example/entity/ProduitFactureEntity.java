package com.example.entity;

import java.util.Objects;

import java.io.Serializable;
import java.math.BigDecimal;

import jakarta.persistence.*;

@Entity
@Table(name = "produits_facture")
public class ProduitFactureEntity {

    @EmbeddedId
    private ProduitFactureId id = new ProduitFactureId();

    @Column(name = "quantite")
    private int quantite;

    @Column(name = "prix_unitaire", nullable = false, precision = 19, scale = 2)
    private BigDecimal prixUnitaire;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("factureId")
    @JoinColumn(name = "fk_facture_id")
    private FactureEntity facture;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("produitId")
    @JoinColumn(name = "fk_produit_id")
    private ProduitEntity produit;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("fournisseurId")
    @JoinColumn(name = "fk_fournisseur_id", nullable = false)
    private FournisseurEntity fournisseur;

    public ProduitFactureEntity() {
    }

    public ProduitFactureEntity(FactureEntity facture,
            ProduitEntity produit, FournisseurEntity fournisseur, int quantite, BigDecimal prixUnitaire) {
        this.quantite = quantite;
        this.prixUnitaire = prixUnitaire;
        this.facture = facture;
        this.produit = produit;
        this.fournisseur = fournisseur;
        this.id = new ProduitFactureId(produit.getId(), facture.getId(), fournisseur.getId());
    }

    public ProduitFactureId getId() {
        return id;
    }

    public void setId(ProduitFactureId id) {
        this.id = id;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public BigDecimal getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(BigDecimal prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }

    public FactureEntity getFacture() {
        return facture;
    }

    public void setFacture(FactureEntity facture) {
        this.facture = facture;
    }

    public ProduitEntity getProduit() {
        return produit;
    }

    public void setProduit(ProduitEntity produit) {
        this.produit = produit;
    }

    public FournisseurEntity getFournisseur() {
        return fournisseur;
    }

    public void setFournisseur(FournisseurEntity fournisseur) {
        this.fournisseur = fournisseur;
    }

    @Embeddable
    public static class ProduitFactureId implements Serializable {
        private Long factureId;
        private Long produitId;
        private Long fournisseurId;

        public ProduitFactureId() {
        }

        public ProduitFactureId(Long factureId, Long produitId, Long fournisseurId) {
            this.factureId = factureId;
            this.produitId = produitId;
            this.fournisseurId = fournisseurId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            ProduitFactureId that = (ProduitFactureId) o;
            return Objects.equals(factureId, that.factureId) &&
                    Objects.equals(produitId, that.produitId) &&
                    Objects.equals(fournisseurId, that.fournisseurId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(factureId, produitId, fournisseurId);
        }
    }
}