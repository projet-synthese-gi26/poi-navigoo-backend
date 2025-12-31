package com.poi.yow_point.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poi.yow_point.config.json_Converter.JsonNodeToJsonConverter;
import com.poi.yow_point.config.json_Converter.JsonToJsonNodeConverter;
import com.poi.yow_point.config.postGIS_Converter.PointToPostgresqlGeographyConverter;
import com.poi.yow_point.config.postGIS_Converter.PostgresqlGeographyToPointConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions;
import org.springframework.data.r2dbc.dialect.PostgresDialect;

import java.util.ArrayList;
import java.util.List;

@Configuration
@Profile("postgres-test")
public class PostgresTestR2dbcConfig {

    @Bean
    public R2dbcCustomConversions r2dbcCustomConversions() {
        List<Converter<?, ?>> converters = new ArrayList<>();

        converters.add(new PointToPostgresqlGeographyConverter());
        converters.add(new PostgresqlGeographyToPointConverter());
        converters.add(new JsonNodeToJsonConverter(new ObjectMapper()));
        converters.add(new JsonToJsonNodeConverter(new ObjectMapper()));

        return R2dbcCustomConversions.of(PostgresDialect.INSTANCE, converters);
    }
}
