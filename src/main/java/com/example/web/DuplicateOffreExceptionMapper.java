package com.example.web;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import com.example.exception.DuplicateOffreException;

@Provider
public class DuplicateOffreExceptionMapper implements ExceptionMapper<DuplicateOffreException> {
    @Override
    public Response toResponse(DuplicateOffreException e) {
        return Response.status(Response.Status.CONFLICT).entity(e.getMessage()).build();
    }
}