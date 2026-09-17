package com.example.web;

import jakarta.annotation.PreDestroy;
import jakarta.persistence.EntityManager;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.example.config.JpaConfig;
import com.example.dao.FournisseurProduitRepository;
import com.example.service.FournisseurProduitService;
import com.example.dto.FournisseurProduitDto;

@Path("/api/v1/fournisseurs/{fournisseurId}/produits/{produitId}")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Offres fournisseur", description = "Gestion des offres (prix, stock) d'un fournisseur pour un produit")
public class FournisseurProduitResource {

    private final EntityManager em;
    private final FournisseurProduitService fournisseurProduitService;

    public FournisseurProduitResource() {
        this.em = JpaConfig.createEntityManager();
        this.fournisseurProduitService = new FournisseurProduitService(new FournisseurProduitRepository(em), em);
    }

    public FournisseurProduitResource(FournisseurProduitService fournisseurProduitService) {
        this.em = null;
        this.fournisseurProduitService = fournisseurProduitService;
    }

    @PreDestroy
    public void close() {
        if (em != null)
            em.close();
    }

    @POST
    @Operation(summary = "Ajouter une offre (prix + stock) d'un fournisseur pour un produit")
    public Response addOffre(@PathParam("fournisseurId") Long fournisseurId,
            @PathParam("produitId") Long produitId,
            FournisseurProduitDto dto) {

        FournisseurProduitDto created = fournisseurProduitService.addOffre(fournisseurId, produitId, dto);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @PUT
    @Operation(summary = "Mettre à jour le prix ou le stock d'une offre existante")
    public Response updateOffre(@PathParam("fournisseurId") Long fournisseurId,
            @PathParam("produitId") Long produitId,
            FournisseurProduitDto dto) {

        FournisseurProduitDto updated = fournisseurProduitService.updateOffre(fournisseurId, produitId, dto);
        if (updated == null)
            return Response.status(Response.Status.NOT_FOUND).build();
        return Response.ok(updated).build();
    }

    @DELETE
    @Operation(summary = "Retirer l'offre d'un fournisseur pour un produit")
    public Response removeOffre(@PathParam("fournisseurId") Long fournisseurId,
            @PathParam("produitId") Long produitId) {

        boolean deleted = fournisseurProduitService.removeOffre(fournisseurId, produitId);
        if (!deleted)
            return Response.status(Response.Status.NOT_FOUND).build();
        return Response.noContent().build();
    }
}