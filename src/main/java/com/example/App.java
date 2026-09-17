package com.example;

import java.net.URI;
// import java.util.Set;

import com.sun.net.httpserver.HttpServer;

import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.h2.tools.Server;

import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;

import com.example.config.JpaConfig;

public class App {
    // public static final String BASE_URI = "http://localhost:8080/";
    public static final String BASE_URI = "http://0.0.0.0:8080/";

    private static Server h2Server;
    private static HttpServer httpServer;

    public static HttpServer startServer() {
        final ResourceConfig config = new ResourceConfig()
                .packages("com.example.web")
                .register(OpenApiResource.class)
                .property("openApi.configuration.location", "openapi.json");

        httpServer = JdkHttpServerFactory.createHttpServer(URI.create(BASE_URI), config);
        return httpServer;
    }

    public static void main(String[] args) {
        try {
            h2Server = Server.createTcpServer(
                    "-tcp", "-tcpAllowOthers", "-ifNotExists", "-tcpPort", "9092").start();
            System.out.println("Base H2 accessible via TCP sur le port 9092.");
        } catch (Exception e) {
            System.err.println("Impossible de démarrer le serveur H2 TCP : " + e.getMessage());
        }

        JpaConfig.createEntityManager();

        startServer();
        System.out.println("Serveur JAX-RS démarré sur " + BASE_URI);
        System.out.println("Spec OpenAPI disponible sur " + BASE_URI + "openapi.json");

        Runtime.getRuntime().addShutdownHook(new Thread(App::shutdown));
    }

    private static void shutdown() {
        System.out.println("Arrêt du serveur...");
        if (httpServer != null)
            httpServer.stop(1);
        JpaConfig.close();
        if (h2Server != null)
            h2Server.stop();
        System.out.println("Serveur arrêté proprement.");
    }
}