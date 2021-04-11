package it.sweven.blockcovid.configurations;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
        name = "bearer",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer"
)
@OpenAPIDefinition(
        info = @Info(title = "BlockCOVID")
)
public class OpenAPIConfiguration {
}
