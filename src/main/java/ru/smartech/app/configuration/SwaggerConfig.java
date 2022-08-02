package ru.smartech.app.configuration;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@ConditionalOnProperty(name = "application.swagger.enabled", havingValue = "true")
@SecurityScheme(
        name = "Bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class SwaggerConfig {

    @Bean
    @Autowired
    public OpenAPI openApi(Info info, List<Server> servers){
        return new OpenAPI()
                .info(info)
                .servers(servers)
                .addSecurityItem(new SecurityRequirement()
                        .addList("Bearer"));
    }

    @Bean
    public Info info(@Value("${application.swagger.title}") String swaggerTitle,
                      @Value("${application.swagger.description}") String swaggerDescription,
                      @Value("${application.swagger.version}") String swaggerVersion,
                     @Value("${application.swagger.contactName}") String contactName,
                     @Value("${application.swagger.contactUrl}") String contactUrl) {
        return new Info()
                .title(swaggerTitle)
                .description(swaggerDescription)
                .version(swaggerVersion)
                .contact(new Contact().name(contactName).url(contactUrl));
    }

    @Bean
    public List<Server> servers(@Value("${application.swagger.servers}") String[] swaggerServers) {
        return Arrays.stream(swaggerServers)
                        .map(server->server.split("::"))
                        .map(server->new Server()
                                .url(server[0].trim())
                                .description(server[1].trim())
                        ).collect(Collectors.toList());
    }
}
