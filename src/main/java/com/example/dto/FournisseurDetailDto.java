package com.example.dto;

import java.math.BigDecimal;
import java.util.List;

import com.example.entity.FournisseurEntity;
import com.example.entity.FournisseurProduitEntity;

public record FournisseurDetailDto(
        Long id,
        String nomEntreprise,
        String adresse,
        List<LigneFournisseurProduitDto> produits) {

    public static FournisseurDetailDto fromEntity(FournisseurEntity entity) {
        if (entity == null) {
            return null;
        }

        List<LigneFournisseurProduitDto> produitsDto = entity.getProduits() == null ? List.of()
                : entity.getProduits().stream()
                        .map(LigneFournisseurProduitDto::fromEntity)
                        .toList();

        return new FournisseurDetailDto(
                entity.getId(),
                entity.getNomEntreprise(),
                entity.getAdresse(),
                produitsDto);
    }
}

record LigneFournisseurProduitDto(
        Long produitId,
        String libelle,
        String description,
        BigDecimal prix,
        int quantite) {
    public static LigneFournisseurProduitDto fromEntity(FournisseurProduitEntity pf) {
        return new LigneFournisseurProduitDto(
                pf.getProduit().getId(),
                pf.getProduit().getLibelle(),
                pf.getProduit().getDescription(),
                pf.getPrixFournisseur(),
                pf.getStockFournisseur());
    }
}