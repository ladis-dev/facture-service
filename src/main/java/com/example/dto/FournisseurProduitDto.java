package com.example.dto;

import java.math.BigDecimal;

import com.example.entity.FournisseurProduitEntity;

public record FournisseurProduitDto(
        Long fournisseurId,
        Long produitId,
        BigDecimal prixFournisseur,
        int stockFournisseur) {

    public static FournisseurProduitDto fromEntity(FournisseurProduitEntity entity) {
        return new FournisseurProduitDto(
                entity.getFournisseur().getId(),
                entity.getProduit().getId(),
                entity.getPrixFournisseur(),
                entity.getStockFournisseur());
    }
}