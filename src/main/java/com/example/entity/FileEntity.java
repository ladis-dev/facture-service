package com.example.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "fichiers")
public class FileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type_file", nullable = false)
    private String typeFile;

    @Column(nullable = false)
    private String path;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_facture_id", nullable = false)
    private FactureEntity facture;

    public FileEntity() {
    }

    public FileEntity(FactureEntity facture, String typeFile, String path) {
        this.facture = facture;
        this.typeFile = typeFile;
        this.path = path;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FactureEntity getFacture() {
        return facture;
    }

    public void setFacture(FactureEntity facture) {
        this.facture = facture;
    }

    public String getTypeFile() {
        return typeFile;
    }

    public void setTypeFile(String typeFile) {
        this.typeFile = typeFile;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}