package com.example.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "produits")
public class ProduitEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String libelle;

    @Column(nullable = false)
    private String description;

    @OneToMany(mappedBy = "produit", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FournisseurProduitEntity> fournisseurs = new ArrayList<>();

    @OneToMany(mappedBy = "produit", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProduitFactureEntity> factures = new ArrayList<>();

    public ProduitEntity() {
    }

    public ProduitEntity(String libelle, String description) {
        this.libelle = libelle;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<FournisseurProduitEntity> getFournisseurs() {
        return fournisseurs;
    }

    public void setFournisseurs(List<FournisseurProduitEntity> fournisseurs) {
        this.fournisseurs = fournisseurs;
    }

    public List<ProduitFactureEntity> getFactures() {
        return factures;
    }

    public void setFactures(List<ProduitFactureEntity> factures) {
        this.factures = factures;
    }
}