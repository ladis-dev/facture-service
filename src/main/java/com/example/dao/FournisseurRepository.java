package com.example.dao;

import jakarta.persistence.EntityManager;

import com.example.entity.FournisseurEntity;

public class FournisseurRepository extends AbstractJpaDao<FournisseurEntity, Long> {
    public FournisseurRepository(EntityManager em) {
        super(em, FournisseurEntity.class);
    }
}
