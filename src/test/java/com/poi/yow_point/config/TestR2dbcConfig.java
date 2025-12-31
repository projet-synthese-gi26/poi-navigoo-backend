package com.poi.yow_point.config;

import com.poi.yow_point.config.json_Converter.JsonToStringConverter;
import com.poi.yow_point.config.json_Converter.StringToJsonConverter;
import com.poi.yow_point.config.postGIS_Converter.PointToStringConverter;
import com.poi.yow_point.config.postGIS_Converter.StringToPointConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions;
import org.springframework.data.r2dbc.dialect.H2Dialect;

import java.util.ArrayList;
import java.util.List;

@Configuration
@Profile("test")
public class TestR2dbcConfig {

    @Bean
    @ConditionalOnMissingBean(name = "r2dbcCustomConversions")
    public R2dbcCustomConversions r2dbcCustomConversions() {
        List<Converter<?, ?>> converters = new ArrayList<>();
        // Add converters to handle JTS Point <-> String for H2
        converters.add(new PointToStringConverter());
        converters.add(new StringToPointConverter());
        // Add converters to handle Postgresql JSON <-> String for H2
        converters.add(new JsonToStringConverter());
        converters.add(new StringToJsonConverter());
        return R2dbcCustomConversions.of(H2Dialect.INSTANCE, converters);
    }
}
