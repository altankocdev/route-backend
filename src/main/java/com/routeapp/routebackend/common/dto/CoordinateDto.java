package com.routeapp.routebackend.common.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record CoordinateDto(
        @NotNull(message = "{validation.latitude.required}")
        @DecimalMin(value = "-90.0", message = "{validation.latitude.range}")
        @DecimalMax(value = "90.0", message = "{validation.latitude.range}")
        Double latitude,

        @NotNull(message = "{validation.longitude.required}")
        @DecimalMin(value = "-180.0", message = "{validation.longitude.range}")
        @DecimalMax(value = "180.0", message = "{validation.longitude.range}")
        Double longitude
) {}