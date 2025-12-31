package com.poi.yow_point.application.mappers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.r2dbc.postgresql.codec.Json;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MapperUtils {

    private final ObjectMapper objectMapper;
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    // --- Méthodes pour les conversions géospatiales ---

    public Point coordinatesToPoint(Double latitude, Double longitude) {
        if (latitude != null && longitude != null) {
            return geometryFactory.createPoint(new Coordinate(longitude, latitude));
        }
        return null;
    }

    @Named("pointToLatitude")
    public Double pointToLatitude(Point point) {
        return (point != null) ? point.getY() : null;
    }

    @Named("pointToLongitude")
    public Double pointToLongitude(Point point) {
        return (point != null) ? point.getX() : null;
    }

    // --- Méthodes pour les conversions String <-> List ---

    @Named("stringToList")
    public List<String> stringToList(String csvString) {
        if (csvString == null || csvString.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.stream(csvString.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    @Named("listToString")
    public String listToString(List<String> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.stream()
                .filter(Objects::nonNull)
                .filter(s -> !s.trim().isEmpty())
                .collect(Collectors.joining(","));
    }

    // --- Méthodes pour les conversions JSON <-> Map ---

    @Named("jsonToMap")
    public Map<String, Object> jsonToMap(Json jsonData) {
        if (jsonData == null || jsonData.asString() == null || "{}".equals(jsonData.asString())) {
            return new HashMap<>();
        }
        try {
            return objectMapper.readValue(jsonData.asString(), new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    @Named("mapToJson")
    public Json mapToJson(Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            return Json.of("{}");
        }
        try {
            String jsonString = objectMapper.writeValueAsString(map);
            return Json.of(jsonString);
        } catch (Exception e) {
            return Json.of("{}");
        }
    }

    // --- Méthodes pour les conversions JsonNode <-> Map ---

    public Map<String, Object> jsonNodeToMap(JsonNode jsonNode) {
        if (jsonNode == null || jsonNode.isNull()) {
            return new HashMap<>();
        }
        return objectMapper.convertValue(jsonNode, new TypeReference<Map<String, Object>>() {
        });
    }

    public JsonNode mapToJsonNode(Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        return objectMapper.valueToTree(map);
    }
}
