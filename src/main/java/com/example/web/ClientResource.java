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
import com.example.dao.ClientRepository;
import com.example.dao.FactureRepository;
import com.example.dao.FileRepository;
import com.example.dao.ProduitFactureRepository;
import com.example.dto.ClientDetailDto;
import com.example.dto.ClientDto;
import com.example.dto.FactureAvecLienDto;
import com.example.service.ClientService;

@Path("/api/v1/clients")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Clients", description = "Gestion des fiches clients")
public class ClientResource {

    private final ClientService clientService;
    private final EntityManager em;

    public ClientResource() {
        this.em = JpaConfig.createEntityManager();
        this.clientService = new ClientService(
                new ClientRepository(em),
                new FactureRepository(em),
                new FileRepository(em),
                new ProduitFactureRepository(em),
                em);
    }

    public ClientResource(ClientService clientService) {
        this.em = null;
        this.clientService = clientService;
    }

    @PreDestroy
    public void close() {
        if (em != null) {
            em.close();
        }
    }

    @POST
    @Operation(summary = "Créer un nouveau client")
    public Response create(ClientDto dto) {
        ClientDto created = clientService.createClient(dto);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @GET
    @Operation(summary = "Lister tous les clients (vue résumée)")
    public Response getAll() {
        List<ClientDto> clients = clientService.getAllClients();
        return Response.ok(clients).build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Obtenir un client par son ID (vue résumée)")
    public Response getById(@PathParam("id") Long id) {
        ClientDto client = clientService.getClientById(id);
        if (client == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(client).build();
    }

    @GET
    @Path("/{id}/detail")
    @Operation(summary = "Obtenir le détail complet d'un client avec son historique de factures")
    public Response getDetailById(@PathParam("id") Long id) {
        ClientDetailDto clientDetail = clientService.getClientDetailById(id);
        if (clientDetail == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(clientDetail).build();
    }

    @GET
    @Path("/{id}/factures")
    public Response getFacturesByClient(@PathParam("id") Long id) {
        List<FactureAvecLienDto> factures = clientService.getFacturesAvecLiens(id);
        return Response.ok(factures).build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Mettre à jour les informations d'un client")
    public Response update(@PathParam("id") Long id, ClientDto dto) {
        ClientDto updated = clientService.updateClient(id, dto);
        if (updated == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(updated).build();

    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Supprimer un client")
    public Response delete(@PathParam("id") Long id) {
        boolean deleted = clientService.deleteClient(id);
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }
}