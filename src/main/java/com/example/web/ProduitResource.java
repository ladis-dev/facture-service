package com.example.web;

import java.util.List;

import jakarta.annotation.PreDestroy;
import jakarta.persistence.EntityManager;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.example.config.JpaConfig;
import com.example.dao.FournisseurProduitRepository;
import com.example.dao.ProduitRepository;
import com.example.dto.FournisseurProduitDto;
import com.example.dto.ProduitDetailDto;
import com.example.dto.ProduitDto;
import com.example.service.FournisseurProduitService;
import com.example.service.ProduitService;

@Path("/api/v1/produits")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Produits", description = "Gestion du catalogue des produits")
public class ProduitResource {

    private final EntityManager em;
    private final ProduitService produitService;
    private final FournisseurProduitService fournisseurProduitService;

    public ProduitResource() {
        this.em = JpaConfig.createEntityManager();
        this.produitService = new ProduitService(new ProduitRepository(em), em);
        this.fournisseurProduitService = new FournisseurProduitService(new FournisseurProduitRepository(em), em);

    }

    public ProduitResource(ProduitService produitService, FournisseurProduitService fournisseurProduitService) {
        this.em = null;
        this.produitService = produitService;
        this.fournisseurProduitService = fournisseurProduitService;
    }

    @PreDestroy
    public void close() {
        if (em != null) {
            em.close();
        }
    }

    @POST
    @Operation(summary = "Créer un nouveau produit")
    public Response create(ProduitDto dto) {
        ProduitDto created = produitService.createProduit(dto);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @GET
    @Operation(summary = "Lister tous les produits (vue résumée)")
    public Response getAll() {
        List<ProduitDto> produits = produitService.getAllProduits();
        return Response.ok(produits).build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Obtenir un produit par son ID (vue résumée)")
    public Response getById(@PathParam("id") Long id) {
        ProduitDto produit = produitService.getProduitById(id);
        if (produit == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(produit).build();
    }

    @GET
    @Path("/{id}/detail")
    @Operation(summary = "Obtenir le détail complet d'un produit (fournisseurs et factures associées)")
    public Response getDetailById(@PathParam("id") Long id) {
        ProduitDetailDto produitDetail = produitService.getProduitDetailById(id);
        if (produitDetail == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(produitDetail).build();
    }

    @GET
    @Path("/{produitId}/stock")
    public Response getStockDisponible(@PathParam("produitId") Long produitId) {
        List<FournisseurProduitDto> stocks = fournisseurProduitService.getStocksPourProduit(produitId);
        return Response.ok(stocks).build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Mettre à jour les informations d'un produit")
    public Response update(@PathParam("id") Long id, ProduitDto dto) {
        ProduitDto updated = produitService.updateProduit(id, dto);
        if (updated == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(updated).build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Supprimer un produit")
    public Response delete(@PathParam("id") Long id) {
        boolean deleted = produitService.deleteProduit(id);
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }
}