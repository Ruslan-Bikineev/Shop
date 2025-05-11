package edu.school21.controllers;

import edu.school21.ShopApplicationTests;
import edu.school21.dto.ClientDto;
import edu.school21.models.Client;
import edu.school21.repositories.ClientRepository;
import edu.school21.utils.MappingUtils;
import edu.school21.utils.Utils;
import io.restassured.RestAssured;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.IntStream;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_NO_CONTENT;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class ClientControllerTests extends ShopApplicationTests {
    @Autowired
    private MappingUtils mappingUtils;
    @Autowired
    private ClientRepository clientRepository;

    @Test
    @DisplayName("API. GET. /api/v1/clients. Get all clients without pagination")
    void testGetAllClients_200() {
        SoftAssertions sA = new SoftAssertions();
        List<ClientDto> clientDtoListInDb = clientRepository.findAll().stream()
                .map(mappingUtils::convertToDto)
                .toList();
        List<ClientDto> clientDtoList = RestAssured.given()
                .port(port)
                .when()
                .get("api/v1/clients")
                .then()
                .statusCode(HTTP_OK)
                .body("id", contains(clientDtoListInDb.stream()
                        .map(c -> c.getId().toString())
                        .toArray()))
                .extract()
                .jsonPath()
                .getList("", ClientDto.class);
        IntStream.range(0, clientDtoListInDb.size()).forEach(i -> {
            clientDtoListInDb.get(i).setId(null);
            sA.assertThat(clientDtoListInDb.get(i)).isEqualTo(clientDtoList.get(i));
        });
    }

    @Test
    @DisplayName("API. GET. /api/v1/clients. Get all clients with pagination")
    void testGetAllClientsWithPagination_200() {
        int limit = 2;
        int offset = 2;
        SoftAssertions sA = new SoftAssertions();
        List<ClientDto> clientDtoListInDb = clientRepository.findAll(PageRequest.of(offset, limit)).stream()
                .map(mappingUtils::convertToDto)
                .toList();
        List<ClientDto> clientDtoList = RestAssured.given()
                .port(port)
                .params("limit", limit,
                        "offset", offset)
                .when()
                .get("api/v1/clients")
                .then()
                .statusCode(HTTP_OK)
                .body("id", contains(clientDtoListInDb.stream()
                        .map(c -> c.getId().toString())
                        .toArray()))
                .extract()
                .jsonPath()
                .getList("", ClientDto.class);
        IntStream.range(0, clientDtoListInDb.size()).forEach(i -> {
            clientDtoListInDb.get(i).setId(null);
            sA.assertThat(clientDtoListInDb.get(i)).isEqualTo(clientDtoList.get(i));
        });
    }

    @Test
    @DisplayName("API. GET. /api/v1/clients. Get all client, limit is max value, offset is 0")
    void testGetAllClientsLimitIsMaxOffsetIsZero_200() {
        int limit = 25;
        int offset = 0;
        SoftAssertions sA = new SoftAssertions();
        List<ClientDto> clientDtoListInDb = clientRepository.findAll(PageRequest.of(offset, limit)).stream()
                .map(mappingUtils::convertToDto)
                .toList();
        List<ClientDto> clientDtoList = RestAssured.given()
                .port(port)
                .params("limit", limit,
                        "offset", offset)
                .when()
                .get("api/v1/clients")
                .then()
                .statusCode(HTTP_OK)
                .body("id", contains(clientDtoListInDb.stream()
                        .map(c -> c.getId().toString())
                        .toArray()))
                .extract()
                .jsonPath()
                .getList("", ClientDto.class);
        IntStream.range(0, clientDtoListInDb.size()).forEach(i -> {
            clientDtoListInDb.get(i).setId(null);
            sA.assertThat(clientDtoListInDb.get(i)).isEqualTo(clientDtoList.get(i));
        });
    }

    @Test
    @DisplayName("API. GET. /api/v1/clients. Limit and offset is less than min value")
    void testGetAllClientsLimitAndOffsetIsMinValue_400() {
        int limit = 0;
        int offset = -1;
        RestAssured.given()
                .port(port)
                .params("limit", limit,
                        "offset", offset)
                .when()
                .get("api/v1/clients")
                .then()
                .statusCode(HTTP_BAD_REQUEST)
                .body("path", endsWith("/api/v1/clients"),
                        "message", containsString("offset: must be positive;"),
                        "message", containsString("limit: must be greater than 0;"));
    }

    @Test
    @DisplayName("API. GET. /api/v1/clients. Limit is higher than max value, offset is 1")
    void testGetAllClientsLimitIsHigherMaxValue_400() {
        int limit = 26;
        int offset = 1;
        RestAssured.given()
                .port(port)
                .params("limit", limit,
                        "offset", offset)
                .when()
                .get("api/v1/clients")
                .then()
                .statusCode(HTTP_BAD_REQUEST)
                .body("path", endsWith("/api/v1/clients"),
                        "message", containsString("limit: must be less than 26;"));
    }

    @Test
    @DisplayName("API. GET. /api/v1/clients/search. Get clients by name and surname")
    void testGetClientsByNameAndSurname_200() {
        String name = "Sherlock";
        String surname = "Holmes";
        SoftAssertions sA = new SoftAssertions();
        List<ClientDto> clientDtoListInDb = clientRepository.findByNameAndSurname(name, surname).stream()
                .map(mappingUtils::convertToDto)
                .toList();
        List<ClientDto> clientDtoList = RestAssured.given()
                .port(port)
                .params("name", name,
                        "surname", surname)
                .when()
                .get("api/v1/clients/search")
                .then()
                .statusCode(HTTP_OK)
                .body("id", contains(clientDtoListInDb.stream()
                        .map(c -> c.getId().toString())
                        .toArray()))
                .extract()
                .jsonPath()
                .getList("", ClientDto.class);
        IntStream.range(0, clientDtoListInDb.size()).forEach(i -> {
            clientDtoListInDb.get(i).setId(null);
            sA.assertThat(clientDtoListInDb.get(i)).isEqualTo(clientDtoList.get(i));
        });
    }

    @Test
    @DisplayName("API. GET. /api/v1/clients/search. Try get clients by name and surname when name and surname is empty")
    void testGetClientsByNameAndSurnameWhenNameAndSurnameIsEmpty_400() {
        String name = "";
        String surname = "";
        RestAssured.given()
                .port(port)
                .params("name", name,
                        "surname", surname)
                .when()
                .get("api/v1/clients/search")
                .then()
                .statusCode(HTTP_BAD_REQUEST)
                .body("path", endsWith("/api/v1/clients/search"),
                        "message", containsString("name: cannot be empty;"),
                        "message", containsString("surname: cannot be empty;"));
    }

    @Test
    @DisplayName("API. GET. /api/v1/clients/search. Try get clients by name and surname without required param 'name'")
    void testGetClientsByNameAndSurnameWithoutRequiredParamName_400() {
        String surname = "Holmes";
        RestAssured.given()
                .port(port)
                .params("surname", surname)
                .when()
                .get("api/v1/clients/search")
                .then()
                .statusCode(HTTP_BAD_REQUEST)
                .body("path", endsWith("/api/v1/clients/search"),
                        "message", equalTo("Required request parameter " +
                                "'name' for method parameter type String is not present"));
    }

    @Test
    @DisplayName("API. GET. /api/v1/clients/search. Try get clients by name and surname without required param 'surname'")
    void testGetClientsByNameAndSurnameWithoutRequiredParamSurname_400() {
        String name = "Sherlock";
        RestAssured.given()
                .port(port)
                .params("name", name)
                .when()
                .get("api/v1/clients/search")
                .then()
                .statusCode(HTTP_BAD_REQUEST)
                .body("path", endsWith("/api/v1/clients/search"),
                        "message", equalTo("Required request parameter " +
                                "'surname' for method parameter type String is not present"));
    }

    @Test
    @DisplayName("API. POST. /api/v1/clients. Created client")
    void testPostClient_201() {
        ClientDto randomClientDto = mappingUtils.convertToDto(randomModels.getRandomClient());
        UUID createdClientId = RestAssured.given()
                .port(port)
                .body(randomClientDto)
                .when()
                .post("api/v1/clients")
                .then()
                .statusCode(HTTP_CREATED)
                .body("id", is(notNullValue()),
                        "name", equalTo(randomClientDto.getName()),
                        "surname", equalTo(randomClientDto.getSurname()),
                        "birthday", equalTo(randomClientDto.getBirthday().toString()),
                        "gender", equalTo(randomClientDto.getGender()),
                        "registration_date", is(notNullValue()),
                        "address.id", is(notNullValue()),
                        "address.country", equalTo(randomClientDto.getAddress().getCountry()),
                        "address.city", equalTo(randomClientDto.getAddress().getCity()),
                        "address.street", equalTo(randomClientDto.getAddress().getStreet()))
                .extract()
                .jsonPath()
                .getUUID("id");
        clientRepository.deleteById(createdClientId);
    }

    @Test
    @DisplayName("API. PUT. /api/v1/clients/{id}. Update exists client")
    void testPutExistsClient_200() {
        ClientDto randomClientDto = mappingUtils.convertToDto(randomModels.getRandomClient());
        Client client = randomModels.getRandomClient();
        clientRepository.save(client);
        RestAssured.given()
                .port(port)
                .body(randomClientDto)
                .when()
                .put("api/v1/clients/{id}", client.getId())
                .then()
                .statusCode(HTTP_OK)
                .body("id", equalTo(client.getId().toString()),
                        "name", equalTo(randomClientDto.getName()),
                        "surname", equalTo(randomClientDto.getSurname()),
                        "birthday", equalTo(randomClientDto.getBirthday().toString()),
                        "gender", equalTo(randomClientDto.getGender()),
                        "registration_date", equalTo(Utils.formatTimestamp(client.getRegistrationDate())),
                        "address.id", equalTo(client.getAddress().getId().toString()),
                        "address.country", equalTo(randomClientDto.getAddress().getCountry()),
                        "address.city", equalTo(randomClientDto.getAddress().getCity()),
                        "address.street", equalTo(randomClientDto.getAddress().getStreet()));
        clientRepository.deleteById(client.getId());
    }

    @Test
    @DisplayName("API. PUT. /api/v1/clients/{id}. Update non exists client")
    void testPutClientNonExistsClient_200() {
        ClientDto randomClientDto = mappingUtils.convertToDto(randomModels.getRandomClient());
        UUID createdClientId = RestAssured.given()
                .port(port)
                .body(randomClientDto)
                .when()
                .put("api/v1/clients/{id}", UUID.randomUUID())
                .then()
                .statusCode(HTTP_OK)
                .body("id", is(notNullValue()),
                        "name", equalTo(randomClientDto.getName()),
                        "surname", equalTo(randomClientDto.getSurname()),
                        "birthday", equalTo(randomClientDto.getBirthday().toString()),
                        "gender", equalTo(randomClientDto.getGender()),
                        "registration_date", is(notNullValue()),
                        "address.id", is(notNullValue()),
                        "address.country", equalTo(randomClientDto.getAddress().getCountry()),
                        "address.city", equalTo(randomClientDto.getAddress().getCity()),
                        "address.street", equalTo(randomClientDto.getAddress().getStreet()))
                .extract()
                .jsonPath()
                .getUUID("id");
        clientRepository.deleteById(createdClientId);
    }

    @Test
    @DisplayName("API. PATCH. /api/v1/clients/{id}. Update only address fields in client fields")
    void testPatchOnlyAddressFieldsInClient_200() {
        ClientDto randomClientDto = mappingUtils.convertToDto(randomModels.getRandomClient());
        Client client = randomModels.getRandomClient();
        clientRepository.save(client);
        RestAssured.given()
                .port(port)
                .body(Map.of("address", randomClientDto.getAddress()))
                .when()
                .patch("api/v1/clients/{id}", client.getId())
                .then()
                .statusCode(HTTP_OK)
                .body("id", equalTo(client.getId().toString()),
                        "name", equalTo(client.getName()),
                        "surname", equalTo(client.getSurname()),
                        "birthday", equalTo(client.getBirthday().toString()),
                        "gender", equalTo(client.getGender()),
                        "registration_date", equalTo(Utils.formatTimestamp(client.getRegistrationDate())),
                        "address.id", equalTo(client.getAddress().getId().toString()),
                        "address.country", equalTo(randomClientDto.getAddress().getCountry()),
                        "address.city", equalTo(randomClientDto.getAddress().getCity()),
                        "address.street", equalTo(randomClientDto.getAddress().getStreet()));
        clientRepository.deleteById(client.getId());
    }

    @Test
    @DisplayName("API. PATCH. /api/v1/clients/{id}. Update only client fields, without address")
    void testPatchOnlyClientFields_200() {
        ClientDto randomClientDto = mappingUtils.convertToDto(randomModels.getRandomClient());
        Client client = randomModels.getRandomClient();
        clientRepository.save(client);
        RestAssured.given()
                .port(port)
                .body(Map.of("name", randomClientDto.getName(),
                        "surname", randomClientDto.getSurname(),
                        "birthday", randomClientDto.getBirthday()))
                .when()
                .patch("api/v1/clients/{id}", client.getId())
                .then()
                .statusCode(HTTP_OK)
                .body("id", equalTo(client.getId().toString()),
                        "name", equalTo(randomClientDto.getName()),
                        "surname", equalTo(randomClientDto.getSurname()),
                        "birthday", equalTo(randomClientDto.getBirthday().toString()),
                        "gender", equalTo(client.getGender()),
                        "registration_date", equalTo(Utils.formatTimestamp(client.getRegistrationDate())),
                        "address.id", equalTo(client.getAddress().getId().toString()),
                        "address.country", equalTo(client.getAddress().getCountry()),
                        "address.city", equalTo(client.getAddress().getCity()),
                        "address.street", equalTo(client.getAddress().getStreet()));
        clientRepository.deleteById(client.getId());
    }

    @Test
    @DisplayName("API. DELETE. /api/v1/clients/{id}. Delete exists client")
    void testDeleteClient_204() {
        Client client = randomModels.getRandomClient();
        clientRepository.save(client);
        RestAssured.given()
                .port(port)
                .when()
                .delete("api/v1/clients/{id}", client.getId())
                .then()
                .statusCode(HTTP_NO_CONTENT);
    }

    @Test
    @DisplayName("API. DELETE. /api/v1/clients/{id}. Delete non exists client")
    void testDeleteNonClient_404() {
        UUID randomId = UUID.randomUUID();
        RestAssured.given()
                .port(port)
                .when()
                .delete("api/v1/clients/{id}", randomId)
                .then()
                .statusCode(HTTP_NOT_FOUND)
                .body("path", endsWith("/api/v1/clients/%s".formatted(randomId)),
                        "message", equalTo("Client with id: %s not found".formatted(randomId)));
    }
}
