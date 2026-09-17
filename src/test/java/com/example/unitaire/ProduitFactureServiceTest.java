package com.example.unitaire;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.dao.FactureRepository;
import com.example.dao.FournisseurProduitRepository;
import com.example.dao.ProduitFactureRepository;
import com.example.dao.ProduitRepository;
import com.example.dto.AjoutLigneRequestDto;
import com.example.dto.ProduitFactureDto;
import com.example.entity.FactureEntity;
import com.example.entity.FournisseurEntity;
import com.example.entity.FournisseurProduitEntity;
import com.example.entity.ProduitEntity;
import com.example.service.ProduitFactureService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

@ExtendWith(MockitoExtension.class)
class ProduitFactureServiceTest {

    @Mock
    private EntityManager em;
    @Mock
    private EntityTransaction tx;
    @Mock
    private FactureRepository factureRepository;
    @Mock
    private ProduitRepository produitRepository;
    @Mock
    private FournisseurProduitRepository fournisseurProduitRepository;
    @Mock
    private ProduitFactureRepository produitFactureRepository;

    @InjectMocks
    private ProduitFactureService service;

    @BeforeEach
    void setUp() {
        lenient().when(em.getTransaction()).thenReturn(tx);
    }

    @Test
    void ajouterLigne_QuantiteNegativeOuNulle_LeveIllegalArgumentException() {
        // Given
        when(factureRepository.findById(1L)).thenReturn(new FactureEntity());
        when(produitRepository.findById(10L)).thenReturn(new ProduitEntity());
        AjoutLigneRequestDto req = new AjoutLigneRequestDto(10L, 0);

        // When & Then
        var ex = assertThrows(IllegalArgumentException.class, () -> service.ajouterLigne(1L, req));
        assertEquals("La quantité doit être positive", ex.getMessage());
    }

    @Test
    void ajouterLigne_StockInsuffisant_LeveIllegalArgumentException() {
        // Given
        when(factureRepository.findById(1L)).thenReturn(new FactureEntity());
        when(produitRepository.findById(10L)).thenReturn(new ProduitEntity());
        when(fournisseurProduitRepository.findMeilleureOffre(eq(10L), eq(50))).thenReturn(Optional.empty());

        AjoutLigneRequestDto req = new AjoutLigneRequestDto(10L, 50);

        // When & Then
        var ex = assertThrows(IllegalArgumentException.class, () -> service.ajouterLigne(1L, req));
        assertEquals("Aucun fournisseur ne dispose d'un stock suffisant pour ce produit", ex.getMessage());
    }

    @Test
    void ajouterLigne_Succes_DecrementeLeStockEtCreeLaLigne() {
        // Given
        FactureEntity facture = new FactureEntity();
        ProduitEntity produit = new ProduitEntity();
        FournisseurEntity fournisseur = new FournisseurEntity();

        FournisseurProduitEntity offre = new FournisseurProduitEntity(fournisseur, produit, new BigDecimal("15.00"),
                100);

        when(factureRepository.findById(1L)).thenReturn(facture);
        when(produitRepository.findById(10L)).thenReturn(produit);
        when(fournisseurProduitRepository.findMeilleureOffre(10L, 5)).thenReturn(Optional.of(offre));

        AjoutLigneRequestDto req = new AjoutLigneRequestDto(10L, 5);

        // When
        ProduitFactureDto result = service.ajouterLigne(1L, req);

        // Then
        assertNotNull(result);
        assertEquals(95, offre.getStockFournisseur()); // Vérifie que le stock a été décrémenté (100 - 5)
        verify(produitFactureRepository, times(1)).create(any());
    }
}