package com.valuation.demo.config;

import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI valuationOpenAPI() {
		return new OpenAPI().info(new Info().title("ValuationProject-POC API").version("v1")
				.description("API documentation for ValuationProject-POC (CRUD + preview/pdf endpoints)")
				.contact(new Contact().name("Valuation Team").email("dev@company.com"))
				.license(new License().name("MIT").url("https://opensource.org/licenses/MIT")));
	}
}
