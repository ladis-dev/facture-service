package com.example.service;

import jakarta.persistence.EntityManager;

import java.util.List;

import com.example.dao.FournisseurProduitRepository;
import com.example.dto.FournisseurProduitDto;
import com.example.entity.FournisseurEntity;
import com.example.entity.FournisseurProduitEntity;
import com.example.entity.ProduitEntity;

public class FournisseurProduitService {

    private final EntityManager em;
    private final FournisseurProduitRepository fournisseurProduitRepository;

    public FournisseurProduitService(FournisseurProduitRepository repository, EntityManager em) {
        this.em = em;
        this.fournisseurProduitRepository = repository;
    }

    public List<FournisseurProduitDto> getStocksPourProduit(Long produitId) {
        return TransactionUtil.runInTransaction(em, () -> fournisseurProduitRepository.findByProduitId(produitId)
                .stream()
                .map(FournisseurProduitDto::fromEntity)
                .toList());
    }

    public FournisseurProduitDto addOffre(Long fournisseurId, Long produitId, FournisseurProduitDto dto) {
        return TransactionUtil.runInTransaction(em, () -> {
            if (fournisseurProduitRepository.findByIds(fournisseurId, produitId) != null) {
                throw new IllegalArgumentException("Ce lien existe déjà, utilisez PUT pour le modifier");
            }

            FournisseurEntity fournisseurRef = em.getReference(FournisseurEntity.class, fournisseurId);
            ProduitEntity produitRef = em.getReference(ProduitEntity.class, produitId);

            FournisseurProduitEntity lien = new FournisseurProduitEntity(
                    fournisseurRef, produitRef, dto.prixFournisseur(), dto.stockFournisseur());

            fournisseurProduitRepository.create(lien);
            return FournisseurProduitDto.fromEntity(lien);
        });
    }

    public FournisseurProduitDto updateOffre(Long fournisseurId, Long produitId, FournisseurProduitDto dto) {
        return TransactionUtil.runInTransaction(em, () -> {
            FournisseurProduitEntity lien = fournisseurProduitRepository.findByIds(fournisseurId, produitId);
            if (lien == null) {
                return null;
            }
            lien.setPrixFournisseur(dto.prixFournisseur());
            lien.setStockFournisseur(dto.stockFournisseur());
            return FournisseurProduitDto.fromEntity(lien);
        });
    }

    public boolean removeOffre(Long fournisseurId, Long produitId) {
        return TransactionUtil.runInTransaction(em, () -> {
            FournisseurProduitEntity lien = fournisseurProduitRepository.findByIds(fournisseurId, produitId);
            if (lien == null) {
                return false;
            }
            fournisseurProduitRepository.delete(lien);
            return true;
        });
    }
}