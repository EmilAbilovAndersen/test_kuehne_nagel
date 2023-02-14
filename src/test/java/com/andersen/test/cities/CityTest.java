package com.andersen.test.cities;

import com.andersen.test.TestApplication;
import com.andersen.test.cities.dto.CityPageDto;
import com.andersen.test.cities.dto.CityRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@RunWith(SpringRunner.class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.MOCK,
    classes = TestApplication.class)
@AutoConfigureMockMvc
public class CityTest {

        @Autowired
        private MockMvc mvc;

        @Autowired
        private CityRepository cityRepository;

        private ObjectMapper objectMapper = new ObjectMapper();

        @ClassRule
        public static PostgreSQLContainer postgreSQLContainer = PostgresqlContainer.getInstance();

        @Test
        public void givenCities_whenGetCityByName_thenReturnCorrectCityAndStatus200() throws Exception {
                var city = createCity("test_city", "http://testUrl");

                // when
                var responseBody = mvc.perform(get("/cities?name=test_cit")
                        .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

                CityPageDto responseDto = objectMapper.readValue(responseBody, CityPageDto.class);

                // then
                Assertions.assertThat(responseDto).isNotNull();
                Assertions.assertThat(responseDto.data()).isNotNull();
                Assertions.assertThat(responseDto.data().size()).isEqualTo(1);
                Assertions.assertThat(responseDto.data().get(0)).isEqualTo(city);
        }

        @Test
        public void givenCities_whenGetSecondPage_thenReturnCorrectPage() throws Exception {
                //given
                var page = 5;
                var pageSize = 20;

                // when
                var responseBody = mvc.perform(get(String.format("/cities?page=%d&pageSize=%d", page, pageSize))
                        .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

                CityPageDto responseDto = objectMapper.readValue(responseBody, CityPageDto.class);

                // then
                Assertions.assertThat(responseDto).isNotNull();
                Assertions.assertThat(responseDto.data()).isNotNull();
                Assertions.assertThat(responseDto.data().size()).isEqualTo(20);
                Assertions.assertThat(responseDto.page()).isEqualTo(5);
        }

        @Test
        public void givenCity_whenUpdate_thenFieldsUpdated() throws Exception {
                var city = createCity(UUID.randomUUID().toString(), "http://testUrl");
                var cityDto = new CityRequestDto(
                    city.getId(),
                    city.getName() + "_changed",
                    city.getPhotoUrl() + "_changed"
                );

                // when
                mvc.perform(put("/cities")
                        .content(objectMapper.writeValueAsString(cityDto))
                        .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

                var updatedCity = cityRepository.findById(city.getId());

                // then
                Assertions.assertThat(updatedCity).isPresent();
                Assertions.assertThat(updatedCity.get().getName()).isEqualTo(cityDto.name());
                Assertions.assertThat(updatedCity.get().getPhotoUrl()).isEqualTo(cityDto.photoUrl());
        }

        private City createCity(String name, String photoUrl) {
                var city = new City(null, name, photoUrl);
                cityRepository.save(city);
                var updatedUsersSize = cityRepository.findById(city.getId());
                Assertions.assertThat(updatedUsersSize).isPresent();

                return updatedUsersSize.get();
        }

}
