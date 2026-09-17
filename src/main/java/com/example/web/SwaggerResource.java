package com.example.web;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/swagger-ui")
public class SwaggerResource {

    @GET
    @Produces(MediaType.TEXT_HTML)
    public String getSwaggerUi() {
        return """
                <!DOCTYPE html>
                <html lang="fr">
                <head>
                  <meta charset="UTF-8">
                  <title>Swagger UI - Facture API</title>
                  <link rel="stylesheet" type="text/css" href="https://unpkg.com/swagger-ui-dist@5/swagger-ui.css" />
                  <style>
                    html { box-sizing: border-box; overflow-y: scroll; }
                    *, *:before, *:after { box-sizing: inherit; }
                    body { margin:0; background: #fafafa; }
                  </style>
                </head>
                <body>
                  <div id="swagger-ui"></div>
                  <script src="https://unpkg.com/swagger-ui-dist@5/swagger-ui-bundle.js" charset="UTF-8"></script>
                  <script>
                    window.onload = function() {
                      window.ui = SwaggerUIBundle({
                        url: "/openapi.json",
                        dom_id: '#swagger-ui',
                        deepLinking: true
                      });
                    };
                  </script>
                </body>
                </html>
                """;
    }
}