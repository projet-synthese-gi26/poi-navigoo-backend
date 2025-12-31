package com.poi.yow_point.config; // Ou un autre package approprié comme util ou mapper

import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
/* import org.springframework.context.annotation.Bean; // Si vous voulez le déclarer comme un bean ici */
import org.springframework.stereotype.Component; // Ou marquer la classe comme un Component

@Component // Rend cette classe un bean Spring, découvrable pour l'injection
public class GeometryFactoryProvider {
    private final GeometryFactory geometryFactory;

    public GeometryFactoryProvider() {
        // SRID 4326 pour WGS84 (coordonnées géographiques latitude/longitude)
        // Ce SRID devrait correspondre à celui utilisé dans votre columnDefinition
        // "geometry(Point,4326)"
        this.geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
    }

    // Cette méthode peut être appelée par MapStruct si le provider est dans 'uses'
    // ou le bean GeometryFactory peut être injecté directement si déclaré avec
    // @Bean
    public GeometryFactory provideFactory() {
        return geometryFactory;
    }

    // Alternative: Déclarer GeometryFactory comme un @Bean directement
    // @Bean // Si cette classe est annotée @Configuration
    // public GeometryFactory geometryFactory() {
    // return new GeometryFactory(new PrecisionModel(), 4326);
    // }
}