package com.example.dto;

import java.math.BigDecimal;
import com.example.entity.ProduitFactureEntity;

public record ProduitFactureDto(
        Long produitId,
        Long fournisseurId,
        int quantite,
        BigDecimal prixUnitaire) {

    public static ProduitFactureDto fromEntity(ProduitFactureEntity entity) {
        return new ProduitFactureDto(
                entity.getProduit().getId(),
                entity.getFournisseur().getId(),
                entity.getQuantite(),
                entity.getPrixUnitaire());
    }
}