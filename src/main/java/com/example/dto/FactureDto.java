package com.example.dto;

import com.example.domain.StatutFacture;
import com.example.entity.ClientEntity;
import com.example.entity.FactureEntity;

public record FactureDto(
        Long id,
        StatutFacture statut,
        Long clientId) {

    public static FactureDto fromEntity(FactureEntity entity) {
        if (entity == null) {
            return null;
        }

        Long clientId = entity.getClient() != null ? entity.getClient().getId() : null;

        return new FactureDto(
                entity.getId(),
                entity.getStatut(),
                clientId);
    }

    public FactureEntity toEntity() {
        ClientEntity client = null;
        if (this.clientId != null) {
            client = new ClientEntity();
            client.setId(this.clientId);
        }

        FactureEntity entity = new FactureEntity(client, this.statut);
        entity.setId(this.id);
        return entity;
    }
}