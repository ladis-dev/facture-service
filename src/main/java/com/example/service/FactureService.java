package com.example.service;

import java.util.List;

import jakarta.persistence.EntityManager;

import com.example.dao.ClientRepository;
import com.example.dao.FactureRepository;
import com.example.domain.StatutFacture;
import com.example.dto.FactureDetailDto;
import com.example.dto.FactureDto;
import com.example.entity.ClientEntity;
import com.example.entity.FactureEntity;

public class FactureService {

    private final EntityManager em;
    private final FactureRepository factureRepository;
    private final ClientRepository clientRepository;

    public FactureService(FactureRepository factureRepository, ClientRepository clientRepository, EntityManager em) {
        this.em = em;
        this.factureRepository = factureRepository;
        this.clientRepository = clientRepository;
    }

    public FactureDto createFacture(FactureDto dto) {
        return TransactionUtil.runInTransaction(em, () -> {
            ClientEntity client = clientRepository.findById(dto.clientId());
            if (client == null) {
                throw new IllegalArgumentException("Client introuvable avec l'ID : " + dto.clientId());
            }

            FactureEntity entity = dto.toEntity();
            entity.setStatut(StatutFacture.CREATION);
            factureRepository.create(entity);
            return FactureDto.fromEntity(entity);
        });
    }

    public List<FactureDto> getAllFactures() {
        return factureRepository.findAll()
                .stream()
                .map(FactureDto::fromEntity)
                .toList();
    }

    public FactureDto getFactureById(Long id) {
        FactureEntity entity = factureRepository.findById(id);
        if (entity == null) {
            return null;
        }
        return FactureDto.fromEntity(entity);
    }

    public FactureDetailDto getFactureDetailById(Long id) {
        return TransactionUtil.runInTransaction(em, () -> {
            FactureEntity entity = factureRepository.findById(id);
            if (entity == null) {
                return null;
            }

            return FactureDetailDto.fromEntity(entity);
        });
    }

    public FactureDto updateFacture(Long id, FactureDto dto) {
        return TransactionUtil.runInTransaction(em, () -> {
            FactureEntity managedEntity = factureRepository.findById(id);
            if (managedEntity == null) {
                return null;
            }

            if (dto.statut() != null && dto.statut() != managedEntity.getStatut()) {
                if (!managedEntity.getStatut().peutTransitionnerVers(dto.statut())) {
                    throw new IllegalArgumentException(
                            "Transition de statut invalide : " + managedEntity.getStatut()
                                    + " -> " + dto.statut());
                }
                managedEntity.setStatut(dto.statut());
            }

            if (dto.clientId() != null && (managedEntity.getClient() == null
                    || !dto.clientId().equals(managedEntity.getClient().getId()))) {
                ClientEntity newClient = clientRepository.findById(dto.clientId());
                if (newClient == null) {
                    throw new IllegalArgumentException("Nouveau client introuvable avec l'ID : " + dto.clientId());
                }
                managedEntity.setClient(newClient);
            }

            return FactureDto.fromEntity(managedEntity);
        });
    }

    public boolean deleteFacture(Long id) {
        return TransactionUtil.runInTransaction(em, () -> {
            FactureEntity entity = factureRepository.findById(id);
            if (entity == null) {
                return false;
            }

            factureRepository.delete(entity);

            return true;
        });
    }
}