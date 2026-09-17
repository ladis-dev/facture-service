package com.example.dao;

import jakarta.persistence.EntityManager;

import com.example.entity.ClientEntity;

public class ClientRepository extends AbstractJpaDao<ClientEntity, Long> {

    public ClientRepository(EntityManager em) {
        super(em, ClientEntity.class);
    }
}