package com.poi.yow_point.config.json_Converter;

import io.r2dbc.postgresql.codec.Json;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.stereotype.Component;

@WritingConverter
@Component
public class JsonToStringConverter implements Converter<Json, String> {
    @Override
    public String convert(Json source) {
        return source.asString();
    }
}
