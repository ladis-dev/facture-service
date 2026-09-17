package com.example.dto;

import com.example.entity.ClientEntity;

public record ClientDto(
        Long id,
        String nom,
        String prenom,
        String adresseLivraison,
        String adresseFacturation) {

    public static ClientDto fromEntity(ClientEntity entity) {
        if (entity == null) {
            return null;
        }

        return new ClientDto(
                entity.getId(),
                entity.getNom(),
                entity.getPrenom(),
                entity.getAdresseLivraison(),
                entity.getAdresseFacturation());
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