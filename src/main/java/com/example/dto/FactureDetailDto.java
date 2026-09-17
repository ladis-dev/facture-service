package com.example.dto;

import java.math.BigDecimal;
import java.util.List;
import com.example.domain.StatutFacture;
import com.example.entity.FactureEntity;
import com.example.entity.ProduitFactureEntity;

public record FactureDetailDto(
        Long id,
        StatutFacture statut,
        ClientDto client,
        List<LigneFactureProduitDto> produits,
        List<FileDto> fichiers) {

    public static FactureDetailDto fromEntity(FactureEntity entity) {
        if (entity == null) {
            return null;
        }

        ClientDto clientDto = ClientDto.fromEntity(entity.getClient());

        List<LigneFactureProduitDto> produitsDto = entity.getProduits() == null ? List.<LigneFactureProduitDto>of()
                : entity.getProduits().stream()
                        .map(LigneFactureProduitDto::fromEntity)
                        .toList();

        List<FileDto> fichiersDto = entity.getFichiers() == null ? List.<FileDto>of()
                : entity.getFichiers().stream()
                        .map(FileDto::fromEntity)
                        .toList();

        return new FactureDetailDto(
                entity.getId(),
                entity.getStatut(),
                clientDto,
                produitsDto,
                fichiersDto);
    }
}

record LigneFactureProduitDto(
        Long produitId,
        String libelle,
        String description,
        int quantite,
        BigDecimal prix) {
    public static LigneFactureProduitDto fromEntity(ProduitFactureEntity pf) {
        return new LigneFactureProduitDto(
                pf.getProduit().getId(),
                pf.getProduit().getLibelle(),
                pf.getProduit().getDescription(),
                pf.getQuantite(),
                pf.getPrixUnitaire());
    }
}
