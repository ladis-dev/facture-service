package com.example.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "client")
public class ClientEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    @Column(name = "adresse_livraison", nullable = false)
    private String adresseLivraison;

    @Column(name = "adresse_facturation", nullable = false)
    private String adresseFacturation;

    @OneToMany(mappedBy = "client", fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST,
            CascadeType.MERGE })
    private List<FactureEntity> factures = new ArrayList<>();

    public ClientEntity() {
    }

    public ClientEntity(String nom, String prenom, String adresseLivraison, String adresseFacturation) {
        this.nom = nom;
        this.prenom = prenom;
        this.adresseLivraison = adresseLivraison;
        this.adresseFacturation = adresseFacturation;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getAdresseLivraison() {
        return adresseLivraison;
    }

    public void setAdresseLivraison(String adresseLivraison) {
        this.adresseLivraison = adresseLivraison;
    }

    public String getAdresseFacturation() {
        return adresseFacturation;
    }

    public void setAdresseFacturation(String adresseFacturation) {
        this.adresseFacturation = adresseFacturation;
    }

    public List<FactureEntity> getFactures() {
        return factures;
    }

    public void setFactures(List<FactureEntity> factures) {
        this.factures = factures;
    }
}