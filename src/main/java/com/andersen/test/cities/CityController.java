package com.andersen.test.cities;

import com.andersen.test.cities.dto.CityPageDto;
import com.andersen.test.cities.dto.CityRequestDto;
import com.andersen.test.cities.dto.CityFilterDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/cities")
@RequiredArgsConstructor
public class CityController {

    private final CityManagementService cityManagementService;

    @GetMapping
    public CityPageDto getCities(@RequestParam(value = "pageSize", defaultValue = "100") int pageSize,
                                 @RequestParam(value = "page", defaultValue = "0") int page,
                                 @RequestParam(value = "name", required = false) String name) {
        return cityManagementService.getCities(new CityFilterDto(name, pageSize, page));
    }

    @PutMapping
    public City updateCity(@RequestBody CityRequestDto cityRequestDto) {
        return cityManagementService.update(cityRequestDto);
    }
}

