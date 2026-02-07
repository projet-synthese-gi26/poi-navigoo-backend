package com.poi.yow_point;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@SpringBootApplication
@EnableR2dbcRepositories
@OpenAPIDefinition(info = @Info(
		title = "Yow Point API",
		version = "1.0",
		description = "Documentation for the Yow Point API, providing endpoints for Points of Interest, Users, Organizations, and more."
))
public class YowPointApplication {

	public static void main(String[] args) {
		SpringApplication.run(YowPointApplication.class, args);
	}

}
