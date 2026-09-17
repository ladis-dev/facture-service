package com.example.dao;

import java.util.List;
import java.math.BigDecimal;

import jakarta.persistence.EntityManager;

import com.example.entity.ProduitFactureEntity;

public class ProduitFactureRepository extends AbstractJpaDao<ProduitFactureEntity, Long> {

    public ProduitFactureRepository(EntityManager em) {
        super(em, ProduitFactureEntity.class);
    }

    public ProduitFactureEntity findByIds(Long factureId, Long produitId, Long fournisseurId) {
        return em.find(ProduitFactureEntity.class,
                new ProduitFactureEntity.ProduitFactureId(factureId, produitId, fournisseurId));
    }

    public List<ProduitFactureEntity> findByFactureId(Long factureId) {
        return em.createQuery(
                "SELECT pf FROM ProduitFactureEntity pf WHERE pf.facture.id = :factureId",
                ProduitFactureEntity.class)
                .setParameter("factureId", factureId)
                .getResultList();
    }

    /**
     * Calcule le montant total d'une facture directement en SQL, sans charger
     * toutes les lignes en mémoire côté Java. Répond à l'exigence "retourner
     * le montant d'une facture".
     */
    public BigDecimal calculerMontantTotal(Long factureId) {
        return em.createQuery(
                "SELECT COALESCE(SUM(pf.prixUnitaire * pf.quantite), 0) " +
                        "FROM ProduitFactureEntity pf WHERE pf.facture.id = :factureId",
                BigDecimal.class)
                .setParameter("factureId", factureId)
                .getSingleResult();
    }
}
