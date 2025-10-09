package com.cooperative.cabin.presentation.mapper;

import com.cooperative.cabin.domain.model.DocumentNumber;
import com.cooperative.cabin.presentation.dto.CreateDocumentNumberRequest;
import com.cooperative.cabin.presentation.dto.DocumentNumberResponse;
import com.cooperative.cabin.presentation.dto.UpdateDocumentNumberRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DocumentNumberMapper {
    DocumentNumberMapper INSTANCE = Mappers.getMapper(DocumentNumberMapper.class);

    DocumentNumberResponse toResponse(DocumentNumber documentNumber);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", expression = "java(com.cooperative.cabin.domain.model.DocumentNumber.DocumentStatus.valueOf(request.status()))")
    DocumentNumber fromCreateRequest(CreateDocumentNumberRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "documentNumber", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", expression = "java(com.cooperative.cabin.domain.model.DocumentNumber.DocumentStatus.valueOf(request.status()))")
    DocumentNumber fromUpdateRequest(UpdateDocumentNumberRequest request);
}
