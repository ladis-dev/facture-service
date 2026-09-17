package com.example.service;

import java.util.List;

import jakarta.persistence.EntityManager;

import com.example.dao.FournisseurRepository;
import com.example.dto.FournisseurDetailDto;
import com.example.dto.FournisseurDto;
import com.example.entity.FournisseurEntity;

public class FournisseurService {

    private final EntityManager em;
    private final FournisseurRepository fournisseurRepository;

    public FournisseurService(FournisseurRepository fournisseurRepository, EntityManager em) {
        this.em = em;
        this.fournisseurRepository = fournisseurRepository;
    }

    public FournisseurDto createFournisseur(FournisseurDto dto) {
        return TransactionUtil.runInTransaction(em, () -> {
            FournisseurEntity entity = dto.toEntity();
            fournisseurRepository.create(entity);
            return FournisseurDto.fromEntity(entity);
        });
    }

    public List<FournisseurDto> getAllFournisseurs() {
        return fournisseurRepository.findAll()
                .stream()
                .map(FournisseurDto::fromEntity)
                .toList();
    }

    public FournisseurDto getFournisseurById(Long id) {
        FournisseurEntity entity = fournisseurRepository.findById(id);
        if (entity == null) {
            return null;
        }
        return FournisseurDto.fromEntity(entity);
    }

    public FournisseurDetailDto getFournisseurDetailById(Long id) {
        return TransactionUtil.runInTransaction(em, () -> {
            FournisseurEntity entity = fournisseurRepository.findById(id);
            if (entity == null) {
                return null;
            }

            return FournisseurDetailDto.fromEntity(entity);
        });
    }

    public FournisseurDto updateFournisseur(Long id, FournisseurDto dto) {
        return TransactionUtil.runInTransaction(em, () -> {
            FournisseurEntity managedEntity = fournisseurRepository.findById(id);
            if (managedEntity == null) {
                return null;
            }

            managedEntity.setNomEntreprise(dto.nomEntreprise());
            managedEntity.setAdresse(dto.adresse());

            return FournisseurDto.fromEntity(managedEntity);
        });
    }

    public boolean deleteFournisseur(Long id) {
        return TransactionUtil.runInTransaction(em, () -> {
            FournisseurEntity entity = fournisseurRepository.findById(id);
            if (entity == null) {
                return false;
            }

            fournisseurRepository.delete(entity);

            return true;
        });
    }
}
