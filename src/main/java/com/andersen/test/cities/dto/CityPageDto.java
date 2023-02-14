package com.andersen.test.cities.dto;

import com.andersen.test.cities.City;

import java.util.List;

public record CityPageDto(List<City> data, long total, int page, int pageNumber) {
}
