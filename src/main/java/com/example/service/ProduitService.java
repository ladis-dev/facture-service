package com.example.service;

import java.util.List;

import jakarta.persistence.EntityManager;

import com.example.dao.ProduitRepository;
import com.example.dto.ProduitDetailDto;
import com.example.dto.ProduitDto;
import com.example.entity.ProduitEntity;

public class ProduitService {

    private final EntityManager em;
    private final ProduitRepository produitRepository;

    public ProduitService(ProduitRepository produitRepository, EntityManager em) {
        this.em = em;
        this.produitRepository = produitRepository;
    }

    public ProduitDto createProduit(ProduitDto dto) {
        return TransactionUtil.runInTransaction(em, () -> {
            ProduitEntity entity = dto.toEntity();
            produitRepository.create(entity);
            return ProduitDto.fromEntity(entity);
        });
    }

    public List<ProduitDto> getAllProduits() {
        return produitRepository.findAll()
                .stream()
                .map(ProduitDto::fromEntity)
                .toList();
    }

    public ProduitDto getProduitById(Long id) {
        ProduitEntity entity = produitRepository.findById(id);
        if (entity == null) {
            return null;
        }
        return ProduitDto.fromEntity(entity);
    }

    public ProduitDetailDto getProduitDetailById(Long id) {
        return TransactionUtil.runInTransaction(em, () -> {
            ProduitEntity entity = produitRepository.findById(id);
            if (entity == null) {
                return null;
            }

            return ProduitDetailDto.fromEntity(entity);
        });
    }

    public ProduitDto updateProduit(Long id, ProduitDto dto) {
        return TransactionUtil.runInTransaction(em, () -> {
            ProduitEntity managedEntity = produitRepository.findById(id);
            if (managedEntity == null) {
                return null;
            }

            managedEntity.setLibelle(dto.libelle());
            managedEntity.setDescription(dto.description());

            return ProduitDto.fromEntity(managedEntity);
        });
    }

    public boolean deleteProduit(Long id) {
        return TransactionUtil.runInTransaction(em, () -> {
            ProduitEntity entity = produitRepository.findById(id);
            if (entity == null) {
                return false;
            }

            produitRepository.delete(entity);

            return true;
        });
    }
}
