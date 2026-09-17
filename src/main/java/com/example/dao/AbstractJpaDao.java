package com.example.dao;

import java.util.List;

import jakarta.persistence.EntityManager;

abstract class AbstractJpaDao<T, ID> {
    protected final EntityManager em;
    private final Class<T> entityClass;

    protected AbstractJpaDao(EntityManager em, Class<T> entityClass) {
        this.em = em;
        this.entityClass = entityClass;
    }

    public T create(T entity) {
        em.persist(entity);
        return entity;
    }

    public T findById(ID id) {
        return em.find(entityClass, id);
    }

    public List<T> findAll() {
        return em.createQuery("SELECT e FROM " + entityClass.getSimpleName() + " e", entityClass)
                .getResultList();
    }

    public T update(T entity) {
        return em.merge(entity);
    }

    public void delete(T entity) {
        em.remove(em.contains(entity) ? entity : em.merge(entity));
    }

    public void deleteById(ID id) {
        T entity = findById(id);
        if (entity != null) {
            em.remove(entity);
        }
    }
}