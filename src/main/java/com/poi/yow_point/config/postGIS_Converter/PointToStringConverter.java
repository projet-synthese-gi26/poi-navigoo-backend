package com.poi.yow_point.config.postGIS_Converter;

import org.locationtech.jts.geom.Point;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.stereotype.Component;

@Component
@WritingConverter // Marks this as a converter for writing to the database
public class PointToStringConverter implements Converter<Point, String> {

    @Override
    public String convert(Point source) {
        if (source == null) {
            return null;
        }
        // Converts the Point object to its Well-Known Text (WKT) representation
        return source.toText();
    }
}