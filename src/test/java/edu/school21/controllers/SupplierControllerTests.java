package edu.school21.controllers;

import edu.school21.ShopApplicationTests;
import edu.school21.dto.SupplierDto;
import edu.school21.models.Supplier;
import edu.school21.repositories.SupplierRepository;
import edu.school21.utils.MappingUtils;
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

public class SupplierControllerTests extends ShopApplicationTests {
    @Autowired
    private MappingUtils mappingUtils;
    @Autowired
    private SupplierRepository supplierRepository;

    @Test
    @DisplayName("API. GET. /api/v1/suppliers. Get all clients without pagination")
    void testGetAllSuppliers_200() {
        SoftAssertions sA = new SoftAssertions();
        List<SupplierDto> supplierDtoListInDb = supplierRepository.findAll().stream()
                .map(mappingUtils::convertToDto)
                .toList();
        List<SupplierDto> supplierDtoList = RestAssured.given()
                .port(port)
                .when()
                .get("api/v1/suppliers")
                .then()
                .statusCode(HTTP_OK)
                .body("id", contains(supplierDtoListInDb.stream()
                        .map(s -> s.getId().toString())
                        .toArray()))
                .extract()
                .jsonPath()
                .getList("", SupplierDto.class);
        IntStream.range(0, supplierDtoListInDb.size()).forEach(i -> {
            supplierDtoListInDb.get(i).setId(null);
            sA.assertThat(supplierDtoListInDb.get(i)).isEqualTo(supplierDtoList.get(i));
        });
    }

    @Test
    @DisplayName("API. GET. /api/v1/suppliers. Get all suppliers with pagination")
    void testGetAllSuppliersWithPagination_200() {
        int limit = 2;
        int offset = 2;
        SoftAssertions sA = new SoftAssertions();
        List<SupplierDto> supplierDtoListInDb = supplierRepository.findAll(PageRequest.of(offset, limit)).stream()
                .map(mappingUtils::convertToDto)
                .toList();
        List<SupplierDto> supplierDtoList = RestAssured.given()
                .port(port)
                .params("limit", limit,
                        "offset", offset)
                .when()
                .get("api/v1/suppliers")
                .then()
                .statusCode(HTTP_OK)
                .body("id", contains(supplierDtoListInDb.stream()
                        .map(s -> s.getId().toString())
                        .toArray()))
                .extract()
                .jsonPath()
                .getList("", SupplierDto.class);
        IntStream.range(0, supplierDtoListInDb.size()).forEach(i -> {
            supplierDtoListInDb.get(i).setId(null);
            sA.assertThat(supplierDtoListInDb.get(i)).isEqualTo(supplierDtoList.get(i));
        });
    }

    @Test
    @DisplayName("API. GET. /api/v1/suppliers. Get all suppliers, limit is max value, offset is 0")
    void testGetAllSuppliersLimitIsMaxValue_200() {
        int limit = 25;
        int offset = 0;
        SoftAssertions sA = new SoftAssertions();
        List<SupplierDto> supplierDtoListInDb = supplierRepository.findAll(PageRequest.of(offset, limit)).stream()
                .map(mappingUtils::convertToDto)
                .toList();
        List<SupplierDto> supplierDtoList = RestAssured.given()
                .port(port)
                .params("limit", limit,
                        "offset", offset)
                .when()
                .get("api/v1/suppliers")
                .then()
                .statusCode(HTTP_OK)
                .body("id", contains(supplierDtoListInDb.stream()
                        .map(s -> s.getId().toString())
                        .toArray()))
                .extract()
                .jsonPath()
                .getList("", SupplierDto.class);
        IntStream.range(0, supplierDtoListInDb.size()).forEach(i -> {
            supplierDtoListInDb.get(i).setId(null);
            sA.assertThat(supplierDtoListInDb.get(i)).isEqualTo(supplierDtoList.get(i));
        });
    }

    @Test
    @DisplayName("API. GET. /api/v1/suppliers. Limit and offset is less than min value")
    void testGetAllSuppliersLimitAndOffsetIsMinValue_400() {
        int limit = 0;
        int offset = -1;
        RestAssured.given()
                .port(port)
                .params("limit", limit,
                        "offset", offset)
                .when()
                .get("api/v1/suppliers")
                .then()
                .statusCode(HTTP_BAD_REQUEST)
                .body("path", endsWith("/api/v1/suppliers"),
                        "message", containsString("offset: must be positive;"),
                        "message", containsString("limit: must be greater than 0;"));
    }

    @Test
    @DisplayName("API. GET. /api/v1/suppliers. Limit is higher than max value, offset is 1")
    void testGetAllSuppliersLimitIsHigherMaxValue_400() {
        int limit = 26;
        int offset = 1;
        RestAssured.given()
                .port(port)
                .params("limit", limit,
                        "offset", offset)
                .when()
                .get("api/v1/suppliers")
                .then()
                .statusCode(HTTP_BAD_REQUEST)
                .body("path", endsWith("/api/v1/suppliers"),
                        "message", containsString("limit: must be less than 26;"));
    }

    @Test
    @DisplayName("API. GET. /api/v1/suppliers. Get exists supplier by id")
    void testGetExistsSupplierById_200() {
        Supplier supplier = randomModels.getRandomSupplier();
        supplierRepository.save(supplier);
        RestAssured.given()
                .port(port)
                .when()
                .get("api/v1/suppliers/{id}", supplier.getId())
                .then()
                .statusCode(HTTP_OK)
                .body("id", equalTo(supplier.getId().toString()),
                        "name", equalTo(supplier.getName()),
                        "phone_number", equalTo(supplier.getPhoneNumber()),
                        "address.id", equalTo(supplier.getAddress().getId().toString()),
                        "address.country", equalTo(supplier.getAddress().getCountry()),
                        "address.city", equalTo(supplier.getAddress().getCity()),
                        "address.street", equalTo(supplier.getAddress().getStreet()));
        supplierRepository.deleteById(supplier.getId());
    }

    @Test
    @DisplayName("API. GET. /api/v1/suppliers. Get non exists supplier by id")
    void testGetNonExistsSupplierById_404() {
        UUID randomId = UUID.randomUUID();
        RestAssured.given()
                .port(port)
                .when()
                .get("api/v1/suppliers/{id}", randomId)
                .then()
                .statusCode(HTTP_NOT_FOUND)
                .body("path", endsWith("/api/v1/suppliers/%s".formatted(randomId)),
                        "message", equalTo("Supplier with id: %s not found".formatted(randomId)));
    }

    @Test
    @DisplayName("API. POST. /api/v1/suppliers. Created supplier")
    void testPostSupplier_201() {
        SupplierDto randomSupplierDto = mappingUtils.convertToDto(randomModels.getRandomSupplier());
        UUID createdSupplierId = RestAssured.given()
                .port(port)
                .body(randomSupplierDto)
                .when()
                .post("api/v1/suppliers")
                .then()
                .statusCode(HTTP_CREATED)
                .body("id", is(notNullValue()),
                        "name", equalTo(randomSupplierDto.getName()),
                        "phone_number", equalTo(randomSupplierDto.getPhoneNumber()),
                        "address.id", is(notNullValue()),
                        "address.country", equalTo(randomSupplierDto.getAddress().getCountry()),
                        "address.city", equalTo(randomSupplierDto.getAddress().getCity()),
                        "address.street", equalTo(randomSupplierDto.getAddress().getStreet()))
                .extract()
                .jsonPath()
                .getUUID("id");
        supplierRepository.deleteById(createdSupplierId);
    }

    @Test
    @DisplayName("API. PUT. /api/v1/suppliers/{id}. Update non exists supplier")
    void testPutNonExistsSupplier_200() {
        SupplierDto randomSupplierDto = mappingUtils.convertToDto(randomModels.getRandomSupplier());
        UUID createdSupplierId = RestAssured.given()
                .port(port)
                .body(randomSupplierDto)
                .when()
                .put("api/v1/suppliers/{id}", UUID.randomUUID())
                .then()
                .statusCode(HTTP_OK)
                .body("id", is(notNullValue()),
                        "name", equalTo(randomSupplierDto.getName()),
                        "phone_number", equalTo(randomSupplierDto.getPhoneNumber()),
                        "address.id", is(notNullValue()),
                        "address.country", equalTo(randomSupplierDto.getAddress().getCountry()),
                        "address.city", equalTo(randomSupplierDto.getAddress().getCity()),
                        "address.street", equalTo(randomSupplierDto.getAddress().getStreet()))
                .extract()
                .jsonPath()
                .getUUID("id");
        supplierRepository.deleteById(createdSupplierId);
    }

    @Test
    @DisplayName("API. PUT. /api/v1/suppliers/{id}. Update exists supplier")
    void testPutExistsSupplier_200() {
        SupplierDto randomSupplierDto = mappingUtils.convertToDto(randomModels.getRandomSupplier());
        Supplier supplier = randomModels.getRandomSupplier();
        supplierRepository.save(supplier);
        RestAssured.given()
                .port(port)
                .body(randomSupplierDto)
                .when()
                .put("api/v1/suppliers/{id}", supplier.getId())
                .then()
                .statusCode(HTTP_OK)
                .body("id", equalTo(supplier.getId().toString()),
                        "name", equalTo(randomSupplierDto.getName()),
                        "phone_number", equalTo(randomSupplierDto.getPhoneNumber()),
                        "address.id", equalTo(supplier.getAddress().getId().toString()),
                        "address.country", equalTo(randomSupplierDto.getAddress().getCountry()),
                        "address.city", equalTo(randomSupplierDto.getAddress().getCity()),
                        "address.street", equalTo(randomSupplierDto.getAddress().getStreet()));
        supplierRepository.deleteById(supplier.getId());
    }

    @Test
    @DisplayName("API. PATCH. /api/v1/suppliers/{id}. Update only supplier fields, without address")
    void testPatchOnlySupplierFields_200() {
        SupplierDto randomSupplierDto = mappingUtils.convertToDto(randomModels.getRandomSupplier());
        Supplier supplier = randomModels.getRandomSupplier();
        supplierRepository.save(supplier);
        RestAssured.given()
                .port(port)
                .body(Map.of("name", randomSupplierDto.getName(),
                        "phone_number", randomSupplierDto.getPhoneNumber()))
                .when()
                .patch("api/v1/suppliers/{id}", supplier.getId())
                .then()
                .statusCode(HTTP_OK)
                .body("id", equalTo(supplier.getId().toString()),
                        "name", equalTo(randomSupplierDto.getName()),
                        "phone_number", equalTo(randomSupplierDto.getPhoneNumber()),
                        "address.id", equalTo(supplier.getAddress().getId().toString()),
                        "address.country", equalTo(supplier.getAddress().getCountry()),
                        "address.city", equalTo(supplier.getAddress().getCity()),
                        "address.street", equalTo(supplier.getAddress().getStreet()));
        supplierRepository.deleteById(supplier.getId());
    }

    @Test
    @DisplayName("API. PATCH. /api/v1/suppliers/{id}. Update only address fields, in supplier")
    void testPatchOnlyAddressFieldsInSupplier_200() {
        SupplierDto randomSupplierDto = mappingUtils.convertToDto(randomModels.getRandomSupplier());
        Supplier supplier = randomModels.getRandomSupplier();
        supplierRepository.save(supplier);
        RestAssured.given()
                .port(port)
                .body(Map.of("address", randomSupplierDto.getAddress()))
                .when()
                .patch("api/v1/suppliers/{id}", supplier.getId())
                .then()
                .statusCode(HTTP_OK)
                .body("id", equalTo(supplier.getId().toString()),
                        "name", equalTo(supplier.getName()),
                        "phone_number", equalTo(supplier.getPhoneNumber()),
                        "address.id", equalTo(supplier.getAddress().getId().toString()),
                        "address.country", equalTo(randomSupplierDto.getAddress().getCountry()),
                        "address.city", equalTo(randomSupplierDto.getAddress().getCity()),
                        "address.street", equalTo(randomSupplierDto.getAddress().getStreet()));
        supplierRepository.deleteById(supplier.getId());
    }

    @Test
    @DisplayName("API. DELETE. /api/v1/suppliers/{id}. Delete exists supplier")
    void testDeleteExistsSupplier_204() {
        Supplier supplier = randomModels.getRandomSupplier();
        supplierRepository.save(supplier);
        RestAssured.given()
                .port(port)
                .when()
                .delete("api/v1/suppliers/{id}", supplier.getId())
                .then()
                .statusCode(HTTP_NO_CONTENT);
    }

    @Test
    @DisplayName("API. DELETE. /api/v1/suppliers/{id}. Delete non exists supplier")
    void testDeleteNonExistsSupplier_404() {
        UUID randomId = UUID.randomUUID();
        RestAssured.given()
                .port(port)
                .when()
                .delete("api/v1/suppliers/{id}", randomId)
                .then()
                .statusCode(HTTP_NOT_FOUND)
                .body("path", endsWith("/api/v1/suppliers/%s".formatted(randomId)),
                        "message", equalTo("Supplier with id: %s not found".formatted(randomId)));
    }
}
