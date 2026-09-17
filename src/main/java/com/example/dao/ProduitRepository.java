package com.example.dao;

import jakarta.persistence.EntityManager;

import com.example.entity.ProduitEntity;

public class ProduitRepository extends AbstractJpaDao<ProduitEntity, Long> {
    public ProduitRepository(EntityManager em) {
        super(em, ProduitEntity.class);
    }
}
