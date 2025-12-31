package com.poi.yow_point.config.json_Converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.r2dbc.postgresql.codec.Json;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@WritingConverter
@RequiredArgsConstructor
public class JsonNodeToJsonConverter implements Converter<JsonNode, Json> {

    private final ObjectMapper objectMapper;

    @Override
    public Json convert(JsonNode source) {
        if (source == null || source.isNull()) {
            return Json.of("null");
        }
        try {
            String jsonString = objectMapper.writeValueAsString(source);
            return Json.of(jsonString);
        } catch (JsonProcessingException e) {
            log.error("Erreur lors de la conversion JsonNode vers Json: {}", e.getMessage());
            return Json.of("null"); // Ou levez une exception selon votre gestion d'erreur
        }
    }
}