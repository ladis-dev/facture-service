package com.example.dao;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityManager;

import com.example.entity.FournisseurProduitEntity;
import com.example.entity.FournisseurProduitEntity.FournisseurProduitId;

public class FournisseurProduitRepository
        extends AbstractJpaDao<FournisseurProduitEntity, FournisseurProduitId> {

    public FournisseurProduitRepository(EntityManager em) {
        super(em, FournisseurProduitEntity.class);
    }

    public FournisseurProduitEntity findByIds(Long fournisseurId, Long produitId) {
        return em.find(FournisseurProduitEntity.class,
                new FournisseurProduitEntity.FournisseurProduitId(fournisseurId, produitId));
    }

    public List<FournisseurProduitEntity> findByProduitId(Long produitId) {
        return em.createQuery(
                "SELECT fp FROM FournisseurProduitEntity fp WHERE fp.produit.id = :produitId",
                FournisseurProduitEntity.class)
                .setParameter("produitId", produitId)
                .getResultList();
    }

    /**
     * Le fournisseur le moins cher disposant d'un stock suffisant pour la
     * quantité demandée. Utilisé à la fois pour vérifier le stock en temps
     * réel et pour déterminer le prix à figer sur une ligne de facture.
     */
    public Optional<FournisseurProduitEntity> findMeilleureOffre(Long produitId, int quantiteRequise) {
        return em.createQuery(
                "SELECT fp FROM FournisseurProduitEntity fp " +
                        "WHERE fp.produit.id = :produitId AND fp.stockFournisseur >= :quantite " +
                        "ORDER BY fp.prixFournisseur ASC",
                FournisseurProduitEntity.class)
                .setParameter("produitId", produitId)
                .setParameter("quantite", quantiteRequise)
                .setMaxResults(1)
                .getResultList()
                .stream()
                .findFirst();
    }
}