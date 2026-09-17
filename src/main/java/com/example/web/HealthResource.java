package com.example.web;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.Map;

import io.swagger.v3.oas.annotations.tags.Tag;

@Path("/api/v1/health")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Health", description = "Permet de tester rapidement si l'API est active")
public class HealthResource {

    @GET
    public Map<String, String> check() {
        return Map.of("status", "UP");
    }
}