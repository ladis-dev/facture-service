package com.example.dto;

import java.math.BigDecimal;

import com.example.domain.StatutFacture;

public record FactureAvecLienDto(Long id, StatutFacture statut, BigDecimal montant, String downloadUrl) {
}