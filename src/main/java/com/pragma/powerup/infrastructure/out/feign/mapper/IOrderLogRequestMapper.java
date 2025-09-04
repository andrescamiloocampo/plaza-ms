package com.pragma.powerup.infrastructure.out.feign.mapper;

import com.pragma.powerup.domain.model.OrderLogModel;
import com.pragma.powerup.infrastructure.out.feign.dto.request.OrderLogRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface IOrderLogRequestMapper {
    OrderLogModel toModel(OrderLogRequestDto orderLogRequestDto);
    OrderLogRequestDto toRequestDto(OrderLogModel orderLogModel);
}
