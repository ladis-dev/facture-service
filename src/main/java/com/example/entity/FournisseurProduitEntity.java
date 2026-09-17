package com.example.entity;

import java.util.Objects;

import java.io.Serializable;

import java.math.BigDecimal;

import jakarta.persistence.*;

@Entity
@Table(name = "fournisseur_produit")
public class FournisseurProduitEntity {

    @EmbeddedId
    private FournisseurProduitId id = new FournisseurProduitId();

    @Column(name = "prix_fournisseur", nullable = false, precision = 19, scale = 2)
    private BigDecimal prixFournisseur;

    @Column(name = "stock_fournisseur", nullable = false)
    private int stockFournisseur;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("fournisseurId")
    @JoinColumn(name = "fk_fournisseur_id")
    private FournisseurEntity fournisseur;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("produitId")
    @JoinColumn(name = "fk_produit_id")
    private ProduitEntity produit;

    public FournisseurProduitEntity() {
    }

    public FournisseurProduitEntity(FournisseurEntity fournisseur, ProduitEntity produit, BigDecimal prixFournisseur,
            int stockFournisseur) {
        this.fournisseur = fournisseur;
        this.produit = produit;
        this.prixFournisseur = prixFournisseur;
        this.stockFournisseur = stockFournisseur;
        this.id = new FournisseurProduitId(fournisseur.getId(), produit.getId());
    }

    public FournisseurProduitId getId() {
        return id;
    }

    public void setId(FournisseurProduitId id) {
        this.id = id;
    }

    public BigDecimal getPrixFournisseur() {
        return prixFournisseur;
    }

    public void setPrixFournisseur(BigDecimal prixFournisseur) {
        this.prixFournisseur = prixFournisseur;
    }

    public int getStockFournisseur() {
        return stockFournisseur;
    }

    public void setStockFournisseur(int stockFournisseur) {
        this.stockFournisseur = stockFournisseur;
    }

    public FournisseurEntity getFournisseur() {
        return fournisseur;
    }

    public void setFournisseur(FournisseurEntity fournisseur) {
        this.fournisseur = fournisseur;
    }

    public ProduitEntity getProduit() {
        return produit;
    }

    public void setProduit(ProduitEntity produit) {
        this.produit = produit;
    }

    @Embeddable
    public static class FournisseurProduitId implements Serializable {
        private Long fournisseurId;
        private Long produitId;

        public FournisseurProduitId() {
        }

        public FournisseurProduitId(Long fournisseurId, Long produitId) {
            this.fournisseurId = fournisseurId;
            this.produitId = produitId;
        }

        public Long getFournisseurId() {
            return fournisseurId;
        }

        public void setFournisseurId(Long fournisseurId) {
            this.fournisseurId = fournisseurId;
        }

        public Long getProduitId() {
            return produitId;
        }

        public void setProduitId(Long produitId) {
            this.produitId = produitId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            FournisseurProduitId that = (FournisseurProduitId) o;
            return Objects.equals(fournisseurId, that.fournisseurId) && Objects.equals(produitId, that.produitId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(fournisseurId, produitId);
        }
    }
}