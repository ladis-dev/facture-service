package com.example.service;

import java.util.List;

import jakarta.persistence.EntityManager;

import com.example.dao.FactureRepository;
import com.example.dao.FileRepository;
import com.example.dto.FileDetailDto;
import com.example.dto.FileDto;
import com.example.entity.FactureEntity;
import com.example.entity.FileEntity;

public class FileService {
    private final FileRepository fileRepository;
    private final FactureRepository factureRepository;
    private final EntityManager em;

    public FileService(FileRepository fileRepository, FactureRepository factureRepository, EntityManager em) {
        this.fileRepository = fileRepository;
        this.factureRepository = factureRepository;
        this.em = em;
    }

    public FileDto createFile(FileDto dto) {
        return TransactionUtil.runInTransaction(em, () -> {
            if (dto.factureId() == null) {
                throw new IllegalArgumentException("L'ID de la facture est obligatoire pour rattaché un fichier.");
            }

            FactureEntity facture = factureRepository.findById(dto.factureId());
            if (facture == null) {
                throw new IllegalArgumentException("Facture introuvable avec l'ID : " + dto.factureId());
            }

            FileEntity entity = new FileEntity(facture, dto.typeFile(), dto.path());
            fileRepository.create(entity);

            return FileDto.fromEntity(entity);
        });
    }

    public List<FileDto> getAllFichiers() {
        return fileRepository.findAll()
                .stream()
                .map(FileDto::fromEntity)
                .toList();
    }

    public FileDto getFileById(Long id) {
        FileEntity entity = fileRepository.findById(id);
        if (entity == null) {
            return null;
        }
        return FileDto.fromEntity(entity);
    }

    public FileDetailDto getFileDetailById(Long id) {
        return TransactionUtil.runInTransaction(em, () -> {
            FileEntity entity = fileRepository.findById(id);
            if (entity == null) {
                return null;
            }

            return FileDetailDto.fromEntity(entity);
        });
    }

    public FileDto updateFile(Long id, FileDto dto) {
        return TransactionUtil.runInTransaction(em, () -> {
            FileEntity managedEntity = fileRepository.findById(id);
            if (managedEntity == null) {
                return null;
            }

            managedEntity.setTypeFile(dto.typeFile());
            managedEntity.setPath(dto.path());

            if (dto.factureId() != null && (managedEntity.getFacture() == null
                    || !dto.factureId().equals(managedEntity.getFacture().getId()))) {
                FactureEntity newFacture = factureRepository.findById(dto.factureId());
                if (newFacture == null) {
                    throw new IllegalArgumentException("Nouvelle facture introuvable avec l'ID : " + dto.factureId());
                }
                managedEntity.setFacture(newFacture);
            }

            return FileDto.fromEntity(managedEntity);
        });
    }

    public boolean deleteFile(Long id) {
        return TransactionUtil.runInTransaction(em, () -> {
            FileEntity entity = fileRepository.findById(id);
            if (entity == null) {
                return false;
            }

            fileRepository.delete(entity);

            return true;
        });
    }
}
