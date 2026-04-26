package com.elevate;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI elevateOpenAPI() {
        Server localServer = new Server();
        localServer.setUrl("http://localhost:8080");
        localServer.setDescription("Local Development Server");

        Server productionServer = new Server();
        productionServer.setUrl("https://api.elevate.com");
        productionServer.setDescription("Production Server");

        Contact contact = new Contact();
        contact.setEmail("support@elevate.com");
        contact.setName("Elevate Support Team");

        License license = new License()
                .name("Proprietary")
                .url("https://elevate.com/license");

        Info info = new Info()
                .title("Elevate Business Management API")
                .version("2.0.0")
                .contact(contact)
                .description(
                        "Comprehensive REST API for managing business operations including Finance, HR, Inventory, and CRM modules. "
                                +
                                "This multi-tenant application provides complete business management capabilities.")
                .termsOfService("https://elevate.com/terms")
                .license(license);

        return new OpenAPI()
                .info(info)
                .servers(List.of(localServer, productionServer));
    }
}
