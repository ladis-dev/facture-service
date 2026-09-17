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
import com.example.dao.FournisseurRepository;
import com.example.dto.FournisseurDetailDto;
import com.example.dto.FournisseurDto;
import com.example.service.FournisseurService;

@Path("/api/v1/fournisseurs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Fournisseurs", description = "Gestion des fiches fournisseurs et de leur catalogue")
public class FournisseurResource {

    private final FournisseurService fournisseurService;
    private final EntityManager em;

    public FournisseurResource() {
        this.em = JpaConfig.createEntityManager();
        this.fournisseurService = new FournisseurService(
                new FournisseurRepository(em),

                em);
    }

    public FournisseurResource(FournisseurService fournisseurService) {
        this.em = null;
        this.fournisseurService = fournisseurService;
    }

    @PreDestroy
    public void close() {
        if (em != null) {
            em.close();
        }
    }

    @POST
    @Operation(summary = "Créer un nouveau fournisseur")
    public Response create(FournisseurDto dto) {
        FournisseurDto created = fournisseurService.createFournisseur(dto);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @GET
    @Operation(summary = "Lister tous les fournisseurs (vue résumée)")
    public Response getAll() {
        List<FournisseurDto> fournisseurs = fournisseurService.getAllFournisseurs();
        return Response.ok(fournisseurs).build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Obtenir un fournisseur par son ID (vue résumée)")
    public Response getById(@PathParam("id") Long id) {
        FournisseurDto fournisseur = fournisseurService.getFournisseurById(id);
        if (fournisseur == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(fournisseur).build();
    }

    @GET
    @Path("/{id}/detail")
    @Operation(summary = "Obtenir le détail complet d'un fournisseur avec son catalogue de produits")
    public Response getDetailById(@PathParam("id") Long id) {
        FournisseurDetailDto fournisseurDetail = fournisseurService.getFournisseurDetailById(id);
        if (fournisseurDetail == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(fournisseurDetail).build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Mettre à jour les informations d'un fournisseur")
    public Response update(@PathParam("id") Long id, FournisseurDto dto) {
        FournisseurDto updated = fournisseurService.updateFournisseur(id, dto);
        if (updated == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(updated).build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Supprimer un fournisseur")
    public Response delete(@PathParam("id") Long id) {
        boolean deleted = fournisseurService.deleteFournisseur(id);
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }
}