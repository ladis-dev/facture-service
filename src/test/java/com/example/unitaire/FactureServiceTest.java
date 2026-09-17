package com.example.unitaire;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.dao.ClientRepository;
import com.example.dao.FactureRepository;
import com.example.domain.StatutFacture;
import com.example.dto.FactureDto;
import com.example.entity.FactureEntity;
import com.example.service.FactureService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

@ExtendWith(MockitoExtension.class)
class FactureServiceTest {

    @Mock
    private EntityManager em;
    @Mock
    private EntityTransaction tx;
    @Mock
    private FactureRepository factureRepository;
    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private FactureService service;

    @BeforeEach
    void setUp() {
        lenient().when(em.getTransaction()).thenReturn(tx);
    }

    @Test
    void updateFacture_TransitionStatutInvalide_LeveIllegalArgumentException() {
        // Given
        FactureEntity facture = new FactureEntity();
        facture.setStatut(StatutFacture.CREATION);

        when(factureRepository.findById(1L)).thenReturn(facture);

        // Tentative d'aller directement de CREATION -> PAYEE (transition interdite)
        FactureDto dto = new FactureDto(1L, StatutFacture.PAYEE, 1L);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> service.updateFacture(1L, dto));
    }
}