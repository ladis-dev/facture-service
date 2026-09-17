package com.example.dto;

import com.example.entity.FileEntity;

public record FileDetailDto(
        Long id,
        String typeFile,
        String path,
        FactureDto facture) {

    public static FileDetailDto fromEntity(FileEntity entity) {
        if (entity == null) {
            return null;
        }

        FactureDto factureDto = FactureDto.fromEntity(entity.getFacture());

        return new FileDetailDto(
                entity.getId(),
                entity.getTypeFile(),
                entity.getPath(),
                factureDto);
    }
}