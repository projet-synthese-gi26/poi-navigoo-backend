package com.poi.yow_point.config.postGIS_Converter;

import io.r2dbc.postgresql.codec.Json;
import lombok.extern.slf4j.Slf4j;

import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKBReader;
import org.locationtech.jts.io.WKBWriter;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;

@WritingConverter
@Slf4j
@Component
public class PointToPostgresqlGeographyConverter implements Converter<Point, Object> {

    @Override
    public Object convert(Point source) {
        if (source == null) {
            return null;
        }
        // Convert JTS Point to WKB (Well-Known Binary)
        WKBWriter writer = new WKBWriter();
        byte[] wkb = writer.write(source);
        return ByteBuffer.wrap(wkb);
    }
}