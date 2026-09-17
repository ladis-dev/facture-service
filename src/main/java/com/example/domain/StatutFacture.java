package com.example.domain;

public enum StatutFacture {
    CREATION, ENVOYEE, PAYEE;

    public boolean peutTransitionnerVers(StatutFacture cible) {
        return switch (this) {
            case CREATION -> cible == ENVOYEE;
            case ENVOYEE -> cible == PAYEE;
            case PAYEE -> false;
        };
    }
}