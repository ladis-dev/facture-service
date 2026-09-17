package com.example.dto;

import java.util.List;

import com.example.entity.ClientEntity;

public record ClientDetailDto(
        Long id,
        String nom,
        String prenom,
        String adresseLivraison,
        String adresseFacturation,
        List<FactureDto> factures) {

    public static ClientDetailDto fromEntity(ClientEntity entity) {
        if (entity == null) {
            return null;
        }

        List<FactureDto> facturesDto = entity.getFactures() == null ? List.of()
                : entity.getFactures().stream()
                        .map(FactureDto::fromEntity)
                        .toList();

        return new ClientDetailDto(
                entity.getId(),
                entity.getNom(),
                entity.getPrenom(),
                entity.getAdresseLivraison(),
                entity.getAdresseFacturation(), facturesDto);
    }

    public ClientEntity toEntity() {
        ClientEntity entity = new ClientEntity(
                this.nom,
                this.prenom,
                this.adresseLivraison,
                this.adresseFacturation);
        entity.setId(this.id);
        return entity;
    }
}