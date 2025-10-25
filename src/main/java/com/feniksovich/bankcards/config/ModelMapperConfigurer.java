package com.feniksovich.bankcards.config;

import com.feniksovich.bankcards.dto.InstantZonedDateTimeConverter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация ModelMapper для маппинга сущностей и DTO.
 */
@Configuration
public class ModelMapperConfigurer {

    @Bean
    public ModelMapper modelMapper() {
        final ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        modelMapper.addConverter(new InstantZonedDateTimeConverter());
        return modelMapper;
    }

}
