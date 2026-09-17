package com.example.web;

import jakarta.annotation.PreDestroy;
import jakarta.persistence.EntityManager;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.example.config.JpaConfig;
import com.example.dao.FactureRepository;
import com.example.dao.FournisseurProduitRepository;
import com.example.dao.ProduitFactureRepository;
import com.example.dao.ProduitRepository;
import com.example.dto.AjoutLigneRequestDto;
import com.example.dto.ProduitFactureDto;
import com.example.service.ProduitFactureService;

@Path("/api/v1/factures/{factureId}/produits")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Lignes de facture", description = "Gestion des lignes produit d'une facture et calcul du montant")
public class ProduitFactureResource {

    private final EntityManager em;
    private final ProduitFactureService produitFactureService;

    public ProduitFactureResource() {
        this.em = JpaConfig.createEntityManager();
        this.produitFactureService = new ProduitFactureService(
                new ProduitFactureRepository(em),
                new FournisseurProduitRepository(em),
                new FactureRepository(em),
                new ProduitRepository(em),
                em);
    }

    public ProduitFactureResource(ProduitFactureService produitFactureService) {
        this.em = null;
        this.produitFactureService = produitFactureService;
    }

    @PreDestroy
    public void close() {
        if (em != null)
            em.close();
    }

    @POST
    @Operation(summary = "Ajouter une ligne produit à une facture (sélectionne automatiquement le fournisseur le moins cher en stock)")
    public Response ajouterLigne(@PathParam("factureId") Long factureId, AjoutLigneRequestDto req) {
        ProduitFactureDto created = produitFactureService.ajouterLigne(factureId, req);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @GET
    @Path("/total")
    @Operation(summary = "Calculer le montant total d'une facture")
    public Response getMontantTotal(@PathParam("factureId") Long factureId) {
        return Response.ok(produitFactureService.getMontantTotal(factureId)).build();
    }

    @DELETE
    @Path("/{produitId}/fournisseurs/{fournisseurId}")
    @Operation(summary = "Supprimer une ligne produit d'une facture pour un fournisseur donné")
    public Response supprimerLigne(@PathParam("factureId") Long factureId,
            @PathParam("produitId") Long produitId,
            @PathParam("fournisseurId") Long fournisseurId) {
        boolean deleted = produitFactureService.supprimerLigne(factureId, produitId, fournisseurId);
        if (!deleted)
            return Response.status(Response.Status.NOT_FOUND).build();
        return Response.noContent().build();
    }
}