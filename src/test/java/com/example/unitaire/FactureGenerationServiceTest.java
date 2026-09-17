package com.example.unitaire;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.dao.FactureRepository;
import com.example.dao.FileRepository;
import com.example.domain.StatutFacture;
import com.example.dto.FileDto;
import com.example.entity.ClientEntity;
import com.example.entity.FactureEntity;
import com.example.entity.ProduitEntity;
import com.example.entity.ProduitFactureEntity;
import com.example.service.FactureGenerationService;

@ExtendWith(MockitoExtension.class)
class FactureGenerationServiceTest {

    @Mock
    private EntityManager em;

    @Mock
    private FactureRepository factureRepository;

    @Mock
    private FileRepository fileRepository;

    @InjectMocks
    private FactureGenerationService service;

    @Test
    void genererFacturX_SiFactureInexistante_LeveIllegalArgumentException() {
        Long factureId = 99L;
        when(factureRepository.findById(factureId)).thenReturn(null);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.genererFacturX(factureId));

        assertEquals("Facture introuvable avec l'ID : 99", exception.getMessage());
    }

    @Test
    void genererFacturX_SiFactureEnCreation_LeveIllegalStateException() {
        Long factureId = 1L;
        FactureEntity facture = new FactureEntity();
        facture.setStatut(StatutFacture.CREATION);

        when(factureRepository.findById(factureId)).thenReturn(facture);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> service.genererFacturX(factureId));

        assertEquals("Impossible de générer un fichier pour une facture encore en creation", exception.getMessage());
    }

    @Test
    void genererFacturX_SiFactureValide_GenereLeFichierEtEnregistreEnBase() {
        Long factureId = 1L;

        ClientEntity client = new ClientEntity();
        client.setNom("Dupont");
        client.setPrenom("Jean");
        client.setAdresseFacturation("10 Rue de la Paix, 75002 Paris");

        ProduitEntity produit = new ProduitEntity();
        produit.setLibelle("Écran 27 pouces");
        produit.setDescription("Écran bureautique");

        ProduitFactureEntity ligne = new ProduitFactureEntity();
        ligne.setProduit(produit);
        ligne.setQuantite(1);
        ligne.setPrixUnitaire(new BigDecimal("299.99"));

        FactureEntity facture = new FactureEntity();
        facture.setId(factureId);
        facture.setStatut(StatutFacture.ENVOYEE);
        facture.setClient(client);
        facture.setProduits(List.of(ligne));

        EntityTransaction transactionMock = mock(EntityTransaction.class);
        when(em.getTransaction()).thenReturn(transactionMock);
        when(factureRepository.findById(factureId)).thenReturn(facture);

        FileDto dto = service.genererFacturX(factureId);

        assertNotNull(dto);
        assertEquals("FACTURX_PDF", dto.typeFile());

        verify(fileRepository, times(1)).create(any());
    }
}