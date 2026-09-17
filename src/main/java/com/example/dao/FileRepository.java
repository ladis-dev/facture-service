package com.example.dao;

import jakarta.persistence.EntityManager;

import java.util.Optional;

import com.example.entity.FileEntity;

public class FileRepository extends AbstractJpaDao<FileEntity, Long> {
    public FileRepository(EntityManager em) {
        super(em, FileEntity.class);
    }

    public Optional<FileEntity> findByFactureIdAndType(Long factureId, String typeFile) {
        return em.createQuery(
                "SELECT f FROM FileEntity f WHERE f.facture.id = :factureId AND f.typeFile = :typeFile",
                FileEntity.class)
                .setParameter("factureId", factureId)
                .setParameter("typeFile", typeFile)
                .getResultList()
                .stream()
                .findFirst();
    }
}