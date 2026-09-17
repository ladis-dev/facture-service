package com.example.dao;

import java.util.List;

import jakarta.persistence.EntityManager;

import com.example.entity.FactureEntity;

public class FactureRepository extends AbstractJpaDao<FactureEntity, Long> {

    public FactureRepository(EntityManager em) {
        super(em, FactureEntity.class);
    }

    public List<FactureEntity> findByClientId(Long clientId) {
        return em.createQuery("SELECT f FROM FactureEntity f WHERE f.client.id = :clientId", FactureEntity.class)
                .setParameter("clientId", clientId)
                .getResultList();
    }
}