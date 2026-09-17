package com.example.service;

import jakarta.persistence.EntityManager;

import com.example.dao.FactureRepository;
import com.example.dao.FournisseurProduitRepository;
import com.example.dao.ProduitFactureRepository;
import com.example.dao.ProduitRepository;
import com.example.dto.AjoutLigneRequestDto;
import com.example.dto.ProduitFactureDto;
import com.example.entity.FactureEntity;
import com.example.entity.FournisseurProduitEntity;
import com.example.entity.ProduitEntity;
import com.example.entity.ProduitFactureEntity;

public class ProduitFactureService {

    private final EntityManager em;
    private final ProduitFactureRepository produitFactureRepository;
    private final FournisseurProduitRepository fournisseurProduitRepository;
    private final FactureRepository factureRepository;
    private final ProduitRepository produitRepository;

    public ProduitFactureService(ProduitFactureRepository produitFactureRepository,
            FournisseurProduitRepository fournisseurProduitRepository,
            FactureRepository factureRepository,
            ProduitRepository produitRepository,
            EntityManager em) {
        this.em = em;
        this.produitFactureRepository = produitFactureRepository;
        this.fournisseurProduitRepository = fournisseurProduitRepository;
        this.factureRepository = factureRepository;
        this.produitRepository = produitRepository;
    }

    public ProduitFactureDto ajouterLigne(Long factureId, AjoutLigneRequestDto req) {
        return TransactionUtil.runInTransaction(em, () -> {
            FactureEntity facture = factureRepository.findById(factureId);
            if (facture == null) {
                throw new IllegalArgumentException("Facture introuvable avec l'ID : " + factureId);
            }
            ProduitEntity produit = produitRepository.findById(req.produitId());
            if (produit == null) {
                throw new IllegalArgumentException("Produit introuvable avec l'ID : " + req.produitId());
            }
            if (req.quantite() <= 0) {
                throw new IllegalArgumentException("La quantité doit être positive");
            }

            // 1. Trouver le fournisseur le moins cher avec assez de stock
            FournisseurProduitEntity offre = fournisseurProduitRepository
                    .findMeilleureOffre(req.produitId(), req.quantite())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Aucun fournisseur ne dispose d'un stock suffisant pour ce produit"));

            // 2. Décrémenter le stock chez ce fournisseur (même transaction)
            offre.setStockFournisseur(offre.getStockFournisseur() - req.quantite());

            // 3. Figer le prix au moment de la vente (indépendant d'un futur
            // changement de prixFournisseur)
            ProduitFactureEntity ligne = new ProduitFactureEntity(
                    facture, produit, offre.getFournisseur(), req.quantite(), offre.getPrixFournisseur());

            produitFactureRepository.create(ligne);
            return ProduitFactureDto.fromEntity(ligne);
        });
    }

    public java.math.BigDecimal getMontantTotal(Long factureId) {
        return produitFactureRepository.calculerMontantTotal(factureId);
    }

    public boolean supprimerLigne(Long factureId, Long produitId, Long fournisseurId) {
        return TransactionUtil.runInTransaction(em, () -> {
            ProduitFactureEntity ligne = produitFactureRepository.findByIds(factureId, produitId, fournisseurId);
            if (ligne == null) {
                return false;
            }

            // Remettre le stock au fournisseur si on annule la ligne
            FournisseurProduitEntity offre = fournisseurProduitRepository
                    .findById(new com.example.entity.FournisseurProduitEntity.FournisseurProduitId(
                            fournisseurId, produitId));
            if (offre != null) {
                offre.setStockFournisseur(offre.getStockFournisseur() + ligne.getQuantite());
            }

            produitFactureRepository.delete(ligne);
            return true;
        });
    }
}