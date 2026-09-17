package com.example.dto;

import com.example.entity.FactureEntity;
import com.example.entity.FileEntity;

public record FileDto(
        Long id,
        String typeFile,
        String path,
        Long factureId) {

    public static FileDto fromEntity(FileEntity entity) {
        if (entity == null) {
            return null;
        }

        Long factureId = entity.getFacture() != null ? entity.getFacture().getId() : null;

        return new FileDto(
                entity.getId(),
                entity.getTypeFile(),
                entity.getPath(),
                factureId);
    }

    public FileEntity toEntity() {
        FactureEntity facture = null;
        if (this.factureId != null) {
            facture = new FactureEntity();
            facture.setId(this.factureId);
        }

        FileEntity entity = new FileEntity(facture, this.typeFile, this.path);
        entity.setId(this.id);
        return entity;
    }
}