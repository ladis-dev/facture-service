package com.example.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "fournisseur")
public class FournisseurEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nom_entreprise", nullable = false)
    private String nomEntreprise;

    @Column(nullable = false)
    private String adresse;

    @OneToMany(mappedBy = "fournisseur", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FournisseurProduitEntity> produits = new ArrayList<>();

    public FournisseurEntity() {
    }

    public FournisseurEntity(String nomEntreprise, String adresse) {
        this.nomEntreprise = nomEntreprise;
        this.adresse = adresse;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomEntreprise() {
        return nomEntreprise;
    }

    public void setNomEntreprise(String nomEntreprise) {
        this.nomEntreprise = nomEntreprise;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public List<FournisseurProduitEntity> getProduits() {
        return produits;
    }

    public void setProduits(List<FournisseurProduitEntity> produits) {
        this.produits = produits;
    }
}