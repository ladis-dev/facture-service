package com.example.web;

import jakarta.annotation.PreDestroy;
import jakarta.persistence.EntityManager;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

import com.example.config.JpaConfig;
import com.example.dao.ClientRepository;
import com.example.dao.FactureRepository;
import com.example.dao.FileRepository;
import com.example.dto.FactureDetailDto;
import com.example.dto.FactureDto;
import com.example.dto.FileDto;
import com.example.service.FactureGenerationService;
import com.example.service.FactureService;

@Path("/api/v1/factures")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Factures", description = "Gestion du cycle de vie et génération des factures")
public class FactureResource {

    private final EntityManager em;
    private final FactureService factureService;
    private final FactureGenerationService factureGenerationService;

    public FactureResource() {
        this.em = JpaConfig.createEntityManager();
        this.factureService = new FactureService(new FactureRepository(em), new ClientRepository(em), em);
        this.factureGenerationService = new FactureGenerationService(new FactureRepository(em), new FileRepository(em),
                em);
    }

    public FactureResource(FactureService factureService, FactureGenerationService factureGenerationService) {
        this.em = null;
        this.factureService = factureService;
        this.factureGenerationService = factureGenerationService;
    }

    @PreDestroy
    public void close() {
        if (em != null)
            em.close();
    }

    @POST
    @Operation(summary = "Créer une nouvelle facture")
    public Response create(FactureDto dto) {
        FactureDto created = factureService.createFacture(dto);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @GET
    @Operation(summary = "Lister toutes les factures (vue résumée)")
    public Response getAll() {
        List<FactureDto> factures = factureService.getAllFactures();
        return Response.ok(factures).build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Obtenir une facture par son ID (vue résumée)")
    public Response getById(@PathParam("id") Long id) {
        FactureDto facture = factureService.getFactureById(id);
        if (facture == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(facture).build();
    }

    @GET
    @Path("/{id}/detail")
    @Operation(summary = "Obtenir le détail complet d'une facture (client, produits et fichiers joints)")
    public Response getDetailById(@PathParam("id") Long id) {
        FactureDetailDto facture = factureService.getFactureDetailById(id);
        if (facture == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(facture).build();
    }

    @POST
    @Path("/{id}/generer")
    @Consumes(MediaType.WILDCARD)
    @Operation(summary = "Générer le PDF Factur-X de la facture")
    public Response genererFacture(@PathParam("id") Long id) {
        try {
            FileDto file = factureGenerationService.genererFacturX(id);
            return Response.status(Response.Status.CREATED).entity(file).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Mettre à jour le statut ou le client d'une facture")
    public Response update(@PathParam("id") Long id, FactureDto dto) {
        FactureDto updated = factureService.updateFacture(id, dto);
        if (updated == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(updated).build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Supprimer une facture")
    public Response delete(@PathParam("id") Long id) {
        boolean deleted = factureService.deleteFacture(id);
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }
}
