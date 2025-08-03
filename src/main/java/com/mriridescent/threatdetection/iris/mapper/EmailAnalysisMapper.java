package com.mriridescent.threatdetection.iris.mapper;

import com.mriridescent.threatdetection.iris.dto.EmailAnalysisDTO;
import com.mriridescent.threatdetection.iris.model.entity.EmailAnalysis;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for converting between EmailAnalysis entities and DTOs.
 */
@Mapper(componentModel = "spring")
public interface EmailAnalysisMapper {

    @Mapping(target = "metadata", source = "metadata")
    EmailAnalysisDTO toDto(EmailAnalysis analysis);
}
