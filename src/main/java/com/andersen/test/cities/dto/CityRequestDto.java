package com.andersen.test.cities.dto;

import javax.validation.constraints.NotNull;

public record CityRequestDto(
    @NotNull
    String id,
    @NotNull
    String name,
    String photoUrl
) {
}
