package com.poi.yow_point.config;

import com.poi.yow_point.config.json_Converter.JsonNodeToJsonConverter;
import com.poi.yow_point.config.json_Converter.JsonToJsonNodeConverter;
import com.poi.yow_point.config.postGIS_Converter.PointToPostgresqlGeographyConverter;
//import com.poi.yow_point.config.postGIS_Converter.PostgresqlGeographyToPointConverter;
// import com.poi.yow_point.config.postGIS_Converter.PointToStringConverter;
import com.poi.yow_point.config.postGIS_Converter.StringToPointConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions;
import org.springframework.data.r2dbc.dialect.PostgresDialect;

import java.util.ArrayList;
import java.util.List;

@Configuration
@Profile("!test")
public class R2dbcConfig {

    @Bean
    public R2dbcCustomConversions r2dbcCustomConversions(
            
            StringToPointConverter stringToPointConverter,
            // PointToStringConverter pointToStringConverter,
            PointToPostgresqlGeographyConverter pointToPostgresqlGeographyConverter,
            //PostgresqlGeographyToPointConverter postgresqlGeographyToPointConverter,
            JsonNodeToJsonConverter jsonNodeToJsonConverter,
            JsonToJsonNodeConverter jsonToJsonNodeConverter) {

        List<Converter<?, ?>> converters = new ArrayList<>();
        
        // Supprimer l'ajout des anciens convertisseurs
        // converters.add(stringToPointConverter);
        // converters.add(pointToStringConverter);
        
        // Conserver uniquement les convertisseurs WKB pour PostGIS
        converters.add(pointToPostgresqlGeographyConverter);
        converters.add(stringToPointConverter);

        // Ajout des convertisseurs pour JsonNode <-> Json
        converters.add(jsonNodeToJsonConverter);
        converters.add(jsonToJsonNodeConverter);

        // Crée et retourne la configuration avec les bons convertisseurs
        return R2dbcCustomConversions.of(PostgresDialect.INSTANCE, converters);
    }
}
