package com.feniksovich.bankcards.dto;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class InstantZonedDateTimeConverter implements Converter<Instant, ZonedDateTime> {

    private final ZoneId zoneId;

    public InstantZonedDateTimeConverter(ZoneId zoneId) {
        this.zoneId = zoneId;
    }

    public InstantZonedDateTimeConverter() {
        this(ZoneId.systemDefault());
    }

    @Override
    public ZonedDateTime convert(MappingContext<Instant, ZonedDateTime> ctx) {
        final Instant source = ctx.getSource();
        return source != null ? source.atZone(zoneId) : null;
    }
}
