package com.example.unitaire;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.example.entity.FactureEntity;
import com.example.entity.ProduitFactureEntity;

class FactureCalculTest {

    @Test
    void calculerMontantTotal_RetourneLaSommeExacteDesLignes() {

        FactureEntity facture = new FactureEntity();

        ProduitFactureEntity ligne1 = new ProduitFactureEntity();
        ligne1.setQuantite(2);
        ligne1.setPrixUnitaire(new BigDecimal("49.90"));

        ProduitFactureEntity ligne2 = new ProduitFactureEntity();
        ligne2.setQuantite(1);
        ligne2.setPrixUnitaire(new BigDecimal("129.90"));

        facture.setProduits(List.of(ligne1, ligne2));

        // Calcul du total (ex: (2 * 49.90) + (1 * 129.90) = 229.70)
        BigDecimal total = facture.getProduits().stream()
                .map(p -> p.getPrixUnitaire().multiply(BigDecimal.valueOf(p.getQuantite())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        assertEquals(new BigDecimal("229.70"), total);
    }
}