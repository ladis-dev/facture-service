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
import com.example.dao.FactureRepository;
import com.example.dao.FileRepository;
import com.example.service.FileService;
import com.example.dto.FileDetailDto;
import com.example.dto.FileDto;

@Path("/api/v1/fichiers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Fichiers", description = "Gestion des pièces jointes et fichiers de facturation")
public class FileResource {

    private final FileService fileService;
    private final EntityManager em;

    public FileResource() {
        this.em = JpaConfig.createEntityManager();
        this.fileService = new FileService(
                new FileRepository(em),
                new FactureRepository(em),
                em);
    }

    public FileResource(FileService fileService) {
        this.em = null;
        this.fileService = fileService;
    }

    @PreDestroy
    public void close() {
        if (em != null) {
            em.close();
        }
    }

    @POST
    @Operation(summary = "Enregistrer un nouveau fichier rattaché à une facture")
    public Response create(FileDto dto) {
        FileDto created = fileService.createFile(dto);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @GET
    @Operation(summary = "Lister tous les fichiers")
    public Response getAll() {
        List<FileDto> fichiers = fileService.getAllFichiers();
        return Response.ok(fichiers).build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Obtenir les informations d'un fichier par son ID")
    public Response getById(@PathParam("id") Long id) {
        FileDto file = fileService.getFileById(id);
        if (file == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(file).build();
    }

    @GET
    @Path("/{id}/detail")
    @Operation(summary = "Obtenir un fichier avec les détails de sa facture associée")
    public Response getDetailById(@PathParam("id") Long id) {
        FileDetailDto file = fileService.getFileDetailById(id);
        if (file == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(file).build();
    }

    @GET
    @Path("/{id}/download")
    @Produces("application/pdf")
    public Response download(@PathParam("id") Long id) {
        FileDto file = fileService.getFileById(id);
        if (file == null)
            return Response.status(Response.Status.NOT_FOUND).build();

        java.io.File physicalFile = new java.io.File(file.path());
        if (!physicalFile.exists())
            return Response.status(Response.Status.NOT_FOUND).build();

        return Response.ok(physicalFile)
                .header("Content-Disposition", "attachment; filename=\"" + physicalFile.getName() + "\"")
                .build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Mettre à jour un fichier ou le réattribuer à une autre facture")
    public Response update(@PathParam("id") Long id, FileDto dto) {
        FileDto updated = fileService.updateFile(id, dto);
        if (updated == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(updated).build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Supprimer un fichier")
    public Response delete(@PathParam("id") Long id) {
        boolean deleted = fileService.deleteFile(id);
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }
}