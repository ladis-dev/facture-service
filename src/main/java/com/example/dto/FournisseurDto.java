package com.example.dto;

import com.example.entity.FournisseurEntity;

public record FournisseurDto(
        Long id,
        String nomEntreprise,
        String adresse) {

    public static FournisseurDto fromEntity(FournisseurEntity entity) {
        if (entity == null) {
            return null;
        }

        return new FournisseurDto(
                entity.getId(),
                entity.getNomEntreprise(),
                entity.getAdresse());
    }

    public FournisseurEntity toEntity() {
        FournisseurEntity entity = new FournisseurEntity(
                this.nomEntreprise,
                this.adresse);
        entity.setId(this.id);
        return entity;
    }
}