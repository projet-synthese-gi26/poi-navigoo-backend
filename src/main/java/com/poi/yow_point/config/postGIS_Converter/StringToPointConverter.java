package com.poi.yow_point.config.postGIS_Converter;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKBReader; // <--- Importez WKBReader
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;

@Component
@ReadingConverter
public class StringToPointConverter implements Converter<String, Point> {

    // On utilise maintenant un lecteur pour le format Binaire (WKB)
    private final WKBReader wkbReader = new WKBReader();

    @Override
    public Point convert(String source) {
        if (source == null || source.isEmpty()) {
            return null;
        }
        try {
            // Étape 1: Convertir la chaîne hexadécimale en tableau de bytes
            byte[] bytes = WKBReader.hexToBytes(source);

            // Étape 2: Lire le tableau de bytes pour créer la géométrie
            Geometry geometry = wkbReader.read(bytes);

            if (geometry instanceof Point) {
                return (Point) geometry;
            }
            // Gérer les cas où la géométrie n'est pas un point si nécessaire
            throw new IllegalArgumentException("La géométrie n'est pas un Point: " + source);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Impossible de parser la chaîne WKB: " + source, e);
        }
    }
}