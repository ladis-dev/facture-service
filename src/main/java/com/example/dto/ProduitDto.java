package com.example.dto;

import com.example.entity.ProduitEntity;

public record ProduitDto(
        Long id,
        String libelle,
        String description) {

    public static ProduitDto fromEntity(ProduitEntity entity) {
        if (entity == null) {
            return null;
        }

        return new ProduitDto(
                entity.getId(),
                entity.getLibelle(),
                entity.getDescription());
    }

    public ProduitEntity toEntity() {
        ProduitEntity entity = new ProduitEntity(
                this.libelle,
                this.description);
        entity.setId(this.id);
        return entity;
    }
}