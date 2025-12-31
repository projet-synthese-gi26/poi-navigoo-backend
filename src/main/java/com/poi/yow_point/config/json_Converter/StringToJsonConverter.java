package com.poi.yow_point.config.json_Converter;

import io.r2dbc.postgresql.codec.Json;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;

@ReadingConverter
@Component
public class StringToJsonConverter implements Converter<String, Json> {
    @Override
    public Json convert(String source) {
        return Json.of(source);
    }
}
