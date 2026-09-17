package com.example.dto;

import java.math.BigDecimal;
import java.util.List;

import com.example.domain.StatutFacture;
import com.example.entity.FournisseurProduitEntity;
import com.example.entity.ProduitEntity;
import com.example.entity.ProduitFactureEntity;

public record ProduitDetailDto(
        Long id,
        String libelle,
        String description,
        List<LigneProduitFournisseurDto> fournisseurs,
        List<LigneProduitFactureDto> factures) {

    public static ProduitDetailDto fromEntity(ProduitEntity entity) {
        if (entity == null) {
            return null;
        }

        List<LigneProduitFournisseurDto> fournisseursDto = entity.getFournisseurs() == null ? List.of()
                : entity.getFournisseurs().stream()
                        .map(LigneProduitFournisseurDto::fromEntity)
                        .toList();

        List<LigneProduitFactureDto> facturesDto = entity.getFournisseurs() == null ? List.of()
                : entity.getFactures().stream()
                        .map(LigneProduitFactureDto::fromEntity)
                        .toList();

        return new ProduitDetailDto(
                entity.getId(),
                entity.getLibelle(),
                entity.getDescription(),
                fournisseursDto, facturesDto);
    }
}

record LigneProduitFournisseurDto(
        Long fournisseurId,
        String nomEntreprise,
        String adresse,
        BigDecimal prix,
        int quantite) {
    public static LigneProduitFournisseurDto fromEntity(FournisseurProduitEntity pf) {
        return new LigneProduitFournisseurDto(
                pf.getFournisseur().getId(),
                pf.getFournisseur().getNomEntreprise(),
                pf.getFournisseur().getAdresse(),
                pf.getPrixFournisseur(),
                pf.getStockFournisseur());
    }
}

record LigneProduitFactureDto(
        Long produitId,
        StatutFacture statut,
        int quantite,
        BigDecimal prix) {
    public static LigneProduitFactureDto fromEntity(ProduitFactureEntity pf) {
        return new LigneProduitFactureDto(
                pf.getFacture().getId(),
                pf.getFacture().getStatut(),
                pf.getQuantite(),
                pf.getPrixUnitaire());
    }
}