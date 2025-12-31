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

@ReadingConverter
@Slf4j
@Component
public class PostgresqlGeographyToPointConverter implements Converter<ByteBuffer, Point> {

    @Override
    public Point convert(ByteBuffer source) {
        if (source == null) {
            return null;
        }
        try {
            byte[] bytes = new byte[source.remaining()];
            source.get(bytes);
            return (Point) new WKBReader().read(bytes);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Failed to parse PostGIS geography to Point", e);
        }
    }
}