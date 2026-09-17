package com.example.service;

import java.math.BigDecimal;
import java.util.List;

import jakarta.persistence.EntityManager;

import com.example.dao.ClientRepository;
import com.example.dao.FactureRepository;
import com.example.dao.FileRepository;
import com.example.dao.ProduitFactureRepository;
import com.example.dto.ClientDetailDto;
import com.example.dto.ClientDto;
import com.example.dto.FactureAvecLienDto;
import com.example.entity.ClientEntity;
import com.example.entity.FactureEntity;

public class ClientService {

    private final EntityManager em;
    private final ClientRepository clientRepository;
    private final FactureRepository factureRepository;
    private final FileRepository fileRepository;
    private final ProduitFactureRepository produitFactureRepository;

    public ClientService(ClientRepository clientRepository,
            FactureRepository factureRepository,
            FileRepository fileRepository,
            ProduitFactureRepository produitFactureRepository,
            EntityManager em) {
        this.em = em;
        this.clientRepository = clientRepository;
        this.factureRepository = factureRepository;
        this.fileRepository = fileRepository;
        this.produitFactureRepository = produitFactureRepository;
    }

    public ClientDto createClient(ClientDto dto) {
        return TransactionUtil.runInTransaction(em, () -> {
            ClientEntity entity = dto.toEntity();
            clientRepository.create(entity);
            return ClientDto.fromEntity(entity);
        });
    }

    public List<ClientDto> getAllClients() {
        return clientRepository.findAll()
                .stream()
                .map(ClientDto::fromEntity)
                .toList();
    }

    public ClientDto getClientById(Long id) {
        ClientEntity entity = clientRepository.findById(id);
        if (entity == null) {
            return null;
        }
        return ClientDto.fromEntity(entity);
    }

    public ClientDetailDto getClientDetailById(Long id) {
        return TransactionUtil.runInTransaction(em, () -> {
            ClientEntity entity = clientRepository.findById(id);
            if (entity == null) {
                return null;
            }

            return ClientDetailDto.fromEntity(entity);
        });
    }

    public List<FactureAvecLienDto> getFacturesAvecLiens(Long clientId) {
        return TransactionUtil.runInTransaction(em, () -> {
            ClientEntity client = clientRepository.findById(clientId);
            if (client == null) {
                throw new IllegalArgumentException("Client introuvable avec l'ID : " + clientId);
            }

            return factureRepository.findByClientId(clientId)
                    .stream()
                    .map(this::toFactureAvecLienDto)
                    .toList();
        });
    }

    private FactureAvecLienDto toFactureAvecLienDto(FactureEntity facture) {
        BigDecimal montant = produitFactureRepository.calculerMontantTotal(facture.getId());

        String downloadUrl = fileRepository.findByFactureIdAndType(facture.getId(), "FACTURX_PDF")
                .map(file -> "/api/v1/fichiers/" + file.getId() + "/download")
                .orElse(null);

        return new FactureAvecLienDto(facture.getId(), facture.getStatut(), montant, downloadUrl);
    }

    public ClientDto updateClient(Long id, ClientDto dto) {
        return TransactionUtil.runInTransaction(em, () -> {
            ClientEntity managed = clientRepository.findById(id);
            if (managed == null)
                return null;

            managed.setNom(dto.nom());
            managed.setPrenom(dto.prenom());
            managed.setAdresseLivraison(dto.adresseLivraison());
            managed.setAdresseFacturation(dto.adresseFacturation());
            return ClientDto.fromEntity(managed);
        });
    }

    // public boolean deleteClient(Long id) {
    // EntityTransaction tx = em.getTransaction();
    // try {
    // tx.begin();

    // ClientEntity entity = clientRepository.findById(id);
    // if (entity == null) {
    // return false;
    // }

    // clientRepository.delete(entity);

    // tx.commit();
    // return true;
    // } catch (Exception e) {
    // if (tx.isActive())
    // tx.rollback();
    // throw e;
    // }
    // }

    public boolean deleteClient(Long id) {
        return TransactionUtil.runInTransaction(em, () -> {
            ClientEntity entity = clientRepository.findById(id);
            if (entity == null) {
                return false;
            }
            clientRepository.delete(entity);
            return true;
        });
    }

}