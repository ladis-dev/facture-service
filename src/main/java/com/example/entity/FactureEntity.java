package com.example.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

import com.example.domain.StatutFacture;

@Entity
@Table(name = "facture")
public class FactureEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutFacture statut;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_client_id")
    private ClientEntity client;

    @OneToMany(mappedBy = "facture", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProduitFactureEntity> produits = new ArrayList<>();

    @OneToMany(mappedBy = "facture", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FileEntity> fichiers = new ArrayList<>();

    public FactureEntity() {
    }

    public FactureEntity(ClientEntity client, StatutFacture statut) {
        this.statut = statut;
        this.client = client;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public StatutFacture getStatut() {
        return statut;
    }

    public void setStatut(StatutFacture statut) {
        this.statut = statut;
    }

    public List<ProduitFactureEntity> getProduits() {
        return produits;
    }

    public void setProduits(List<ProduitFactureEntity> produits) {
        this.produits = produits;
    }

    public List<FileEntity> getFichiers() {
        return fichiers;
    }

    public void setFichiers(List<FileEntity> fichiers) {
        this.fichiers = fichiers;
    }

    public ClientEntity getClient() {
        return client;
    }

    public void setClient(ClientEntity client) {
        this.client = client;
    }
}