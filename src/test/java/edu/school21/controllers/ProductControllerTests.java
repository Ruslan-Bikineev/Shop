package edu.school21.controllers;

import edu.school21.ShopApplicationTests;
import edu.school21.dto.ProductDto;
import edu.school21.models.Product;
import edu.school21.repositories.ProductRepository;
import edu.school21.repositories.SupplierRepository;
import edu.school21.services.ImageService;
import edu.school21.utils.MappingUtils;
import edu.school21.utils.Utils;
import io.restassured.RestAssured;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Assertions;
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

public class ProductControllerTests extends ShopApplicationTests {
    @Autowired
    private MappingUtils mappingUtils;
    @Autowired
    private ImageService imageService;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private SupplierRepository supplierRepository;

    @Test
    @DisplayName("API. GET. /api/v1/products. Get all products without pagination")
    void testGetAllProducts_200() {
        SoftAssertions sA = new SoftAssertions();
        List<ProductDto> productDtoListInDb = productRepository.findAll().stream()
                .map(mappingUtils::convertToDto)
                .toList();
        List<ProductDto> productDtoList = RestAssured.given()
                .port(port)
                .when()
                .get("api/v1/products")
                .then()
                .statusCode(HTTP_OK)
                .body("id", contains(productDtoListInDb.stream()
                        .map(p -> p.getId().toString())
                        .toArray()))
                .extract()
                .jsonPath()
                .getList("", ProductDto.class);
        IntStream.range(0, productDtoListInDb.size()).forEach(i -> {
            productDtoListInDb.get(i).setId(null);
            sA.assertThat(productDtoListInDb.get(i)).isEqualTo(productDtoList.get(i));
        });
    }

    @Test
    @DisplayName("API. GET. /api/v1/products. Get all products with pagination")
    void testGetAllProductsWithPagination_200() {
        int limit = 2;
        int offset = 2;
        SoftAssertions sA = new SoftAssertions();
        List<ProductDto> productDtoListInDb = productRepository.findAll(PageRequest.of(offset, limit)).stream()
                .map(mappingUtils::convertToDto)
                .toList();
        List<ProductDto> productDtoList = RestAssured.given()
                .port(port)
                .params("limit", limit,
                        "offset", offset)
                .when()
                .get("api/v1/products")
                .then()
                .statusCode(HTTP_OK)
                .body("id", contains(productDtoListInDb.stream()
                        .map(p -> p.getId().toString())
                        .toArray()))
                .extract()
                .jsonPath()
                .getList("", ProductDto.class);
        IntStream.range(0, productDtoListInDb.size()).forEach(i -> {
            productDtoListInDb.get(i).setId(null);
            sA.assertThat(productDtoListInDb.get(i)).isEqualTo(productDtoList.get(i));
        });
    }

    @Test
    @DisplayName("API. GET. /api/v1/products. Get all products, limit is max value, offset is 0")
    void testGetAllProductsLimitIsMaxOffsetIsZero_200() {
        int limit = 25;
        int offset = 0;
        SoftAssertions sA = new SoftAssertions();
        List<ProductDto> productDtoListInDb = productRepository.findAll(PageRequest.of(offset, limit)).stream()
                .map(mappingUtils::convertToDto)
                .toList();
        List<ProductDto> productDtoList = RestAssured.given()
                .port(port)
                .params("limit", limit,
                        "offset", offset)
                .when()
                .get("api/v1/products")
                .then()
                .statusCode(HTTP_OK)
                .body("id", contains(productDtoListInDb.stream()
                        .map(p -> p.getId().toString())
                        .toArray()))
                .extract()
                .jsonPath()
                .getList("", ProductDto.class);
        IntStream.range(0, productDtoListInDb.size()).forEach(i -> {
            productDtoListInDb.get(i).setId(null);
            sA.assertThat(productDtoListInDb.get(i)).isEqualTo(productDtoList.get(i));
        });
    }

    @Test
    @DisplayName("API. GET. /api/v1/products. Get all products, limit and offset is less than min value")
    void testGetAllProductsLimitAndOffsetIsMinValue_200() {
        int limit = 0;
        int offset = -1;
        RestAssured.given()
                .port(port)
                .params("limit", limit,
                        "offset", offset)
                .when()
                .get("api/v1/products")
                .then()
                .statusCode(HTTP_BAD_REQUEST)
                .body("path", endsWith("/api/v1/products"),
                        "message", containsString("offset: must be positive;"),
                        "message", containsString("limit: must be greater than 0;"));
    }

    @Test
    @DisplayName("API. GET. /api/v1/products. Limit is higher than max value, offset is 1")
    void testGetAllProductsLimitIsHigherMaxValue_200() {
        int limit = 26;
        int offset = 1;
        RestAssured.given()
                .port(port)
                .params("limit", limit,
                        "offset", offset)
                .when()
                .get("api/v1/products")
                .then()
                .statusCode(HTTP_BAD_REQUEST)
                .body("path", endsWith("/api/v1/products"),
                        "message", containsString("limit: must be less than 26;"));
    }

    @Test
    @DisplayName("API. GET. /api/v1/products/{id}. Get exists product by id")
    void testGetExistsProductById_200() {
        Product randomProductForDb = randomModels.getRandomProduct();
        supplierRepository.save(randomProductForDb.getSupplier());
        imageService.saveImage(randomProductForDb.getImage());
        productRepository.save(randomProductForDb);
        RestAssured.given()
                .port(port)
                .when()
                .get("api/v1/products/{id}", randomProductForDb.getId())
                .then()
                .statusCode(HTTP_OK)
                .body("id", equalTo(randomProductForDb.getId().toString()),
                        "name", equalTo(randomProductForDb.getName()),
                        "category", equalTo(randomProductForDb.getCategory()),
                        "price.toString()", equalTo(randomProductForDb.getPrice().toString()),
                        "available_stock", equalTo(randomProductForDb.getAvailableStock()),
                        "last_update_date", equalTo(Utils.formatTimestamp(randomProductForDb.getLastUpdateDate())),
                        "supplier_id", equalTo(randomProductForDb.getSupplier().getId().toString()),
                        "image_id", equalTo(randomProductForDb.getImage().getId().toString()));
        supplierRepository.deleteById(randomProductForDb.getSupplier().getId());
        imageService.deleteById(randomProductForDb.getImage().getId());
    }

    @Test
    @DisplayName("API. GET. /api/v1/products/{id}. Get non exists product by id")
    void testGetNonExistsProductById_404() {
        UUID randomId = UUID.randomUUID();
        RestAssured.given()
                .port(port)
                .when()
                .get("api/v1/products/{id}", randomId)
                .then()
                .statusCode(HTTP_NOT_FOUND)
                .body("path", endsWith("/api/v1/products/%s".formatted(randomId)),
                        "message", equalTo("Product with id: %s not found".formatted(randomId)));
    }

    @Test
    @DisplayName("API. POST. /api/v1/products. Created product")
    void testPostProduct_201() {
        Product randomProductForDb = randomModels.getRandomProduct();
        supplierRepository.save(randomProductForDb.getSupplier());
        imageService.saveImage(randomProductForDb.getImage());
        ProductDto randomProductDto = mappingUtils.convertToDto(randomProductForDb);
        RestAssured.given()
                .port(port)
                .when()
                .body(randomProductDto)
                .post("api/v1/products")
                .then()
                .statusCode(HTTP_CREATED)
                .body("id", is(notNullValue()),
                        "name", equalTo(randomProductDto.getName()),
                        "category", equalTo(randomProductDto.getCategory()),
                        "price.toString()", equalTo(randomProductDto.getPrice().toString()),
                        "available_stock", equalTo(randomProductDto.getAvailableStock()),
                        "last_update_date", is(notNullValue()),
                        "supplier_id", equalTo(randomProductDto.getSupplierId().toString()),
                        "image_id", equalTo(randomProductDto.getImageId().toString()));
        supplierRepository.deleteById(randomProductDto.getSupplierId());
        imageService.deleteById(randomProductDto.getImageId());
    }

    @Test
    @DisplayName("API. PUT. /api/v1/products/{id}. Update exists product")
    void testPutExistsProduct_200() {
        Product updatedProduct = randomModels.getRandomProduct();
        supplierRepository.save(updatedProduct.getSupplier());
        imageService.saveImage(updatedProduct.getImage());
        ProductDto randomProductDto = mappingUtils.convertToDto(updatedProduct);
        Product existsProductInDb = randomModels.getRandomProduct();
        supplierRepository.save(existsProductInDb.getSupplier());
        imageService.saveImage(existsProductInDb.getImage());
        productRepository.save(existsProductInDb);
        RestAssured.given()
                .port(port)
                .when()
                .body(randomProductDto)
                .put("api/v1/products/{id}", existsProductInDb.getId())
                .then()
                .statusCode(HTTP_OK)
                .body("id", equalTo(existsProductInDb.getId().toString()),
                        "name", equalTo(randomProductDto.getName()),
                        "category", equalTo(randomProductDto.getCategory()),
                        "price.toString()", equalTo(randomProductDto.getPrice().toString()),
                        "available_stock", equalTo(randomProductDto.getAvailableStock()),
                        "last_update_date", is(notNullValue()),
                        "supplier_id", equalTo(randomProductDto.getSupplierId().toString()),
                        "image_id", equalTo(randomProductDto.getImageId().toString()));
        supplierRepository.deleteById(existsProductInDb.getSupplier().getId());
        supplierRepository.deleteById(randomProductDto.getSupplierId());
        imageService.deleteById(existsProductInDb.getImage().getId());
        imageService.deleteById(randomProductDto.getImageId());
    }

    @Test
    @DisplayName("API. PUT. /api/v1/products/{id}. Update non exists product")
    void testPutNonExistsProduct_200() {
        Product product = randomModels.getRandomProduct();
        supplierRepository.save(product.getSupplier());
        imageService.saveImage(product.getImage());
        ProductDto randomProductDto = mappingUtils.convertToDto(product);
        RestAssured.given()
                .port(port)
                .when()
                .body(randomProductDto)
                .put("api/v1/products/{id}", UUID.randomUUID())
                .then()
                .statusCode(HTTP_OK)
                .body("id", is(notNullValue()),
                        "name", equalTo(randomProductDto.getName()),
                        "category", equalTo(randomProductDto.getCategory()),
                        "price.toString()", equalTo(randomProductDto.getPrice().toString()),
                        "available_stock", equalTo(randomProductDto.getAvailableStock()),
                        "last_update_date", is(notNullValue()),
                        "supplier_id", equalTo(randomProductDto.getSupplierId().toString()),
                        "image_id", equalTo(randomProductDto.getImageId().toString()));
        supplierRepository.deleteById(randomProductDto.getSupplierId());
        imageService.deleteById(randomProductDto.getImageId());
    }

    @Test
    @DisplayName("API. PATCH. /api/v1/products/{id}. Update exists product only " +
            "name, category, price, available_stock fields")
    void testPatchExistsProductOnlyNameCategoryPriceAvailableStockFields_200() {
        ProductDto randomProductDto = mappingUtils.convertToDto(randomModels.getRandomProduct());
        Product existsProductInDb = randomModels.getRandomProduct();
        supplierRepository.save(existsProductInDb.getSupplier());
        imageService.saveImage(existsProductInDb.getImage());
        productRepository.save(existsProductInDb);
        RestAssured.given()
                .port(port)
                .when()
                .body(Map.of("name", randomProductDto.getName(),
                        "category", randomProductDto.getCategory(),
                        "price", randomProductDto.getPrice(),
                        "available_stock", randomProductDto.getAvailableStock()))
                .patch("api/v1/products/{id}", existsProductInDb.getId())
                .then()
                .statusCode(HTTP_OK)
                .body("id", equalTo(existsProductInDb.getId().toString()),
                        "name", equalTo(randomProductDto.getName()),
                        "category", equalTo(randomProductDto.getCategory()),
                        "price.toString()", equalTo(randomProductDto.getPrice().toString()),
                        "available_stock", equalTo(randomProductDto.getAvailableStock()),
                        "last_update_date", is(notNullValue()),
                        "supplier_id", equalTo(existsProductInDb.getSupplier().getId().toString()),
                        "image_id", equalTo(existsProductInDb.getImage().getId().toString()));
        supplierRepository.deleteById(existsProductInDb.getSupplier().getId());
        imageService.deleteById(existsProductInDb.getImage().getId());
    }

    @Test
    @DisplayName("API. PATCH. /api/v1/products/{id}. Update exists product only supplier_id, image_id fields")
    void testPatchExistsProductOnlySupplierIdImageIdFields_200() {
        Product updatedProduct = randomModels.getRandomProduct();
        supplierRepository.save(updatedProduct.getSupplier());
        imageService.saveImage(updatedProduct.getImage());
        ProductDto randomProductDto = mappingUtils.convertToDto(updatedProduct);
        Product existsProductInDb = randomModels.getRandomProduct();
        supplierRepository.save(existsProductInDb.getSupplier());
        imageService.saveImage(existsProductInDb.getImage());
        productRepository.save(existsProductInDb);
        RestAssured.given()
                .port(port)
                .when()
                .body(Map.of("supplier_id", randomProductDto.getSupplierId(),
                        "image_id", randomProductDto.getImageId()))
                .patch("api/v1/products/{id}", existsProductInDb.getId())
                .then()
                .statusCode(HTTP_OK)
                .body("id", equalTo(existsProductInDb.getId().toString()),
                        "name", equalTo(existsProductInDb.getName()),
                        "category", equalTo(existsProductInDb.getCategory()),
                        "price.toString()", equalTo(existsProductInDb.getPrice().toString()),
                        "available_stock", equalTo(existsProductInDb.getAvailableStock()),
                        "last_update_date", is(notNullValue()),
                        "supplier_id", equalTo(randomProductDto.getSupplierId().toString()),
                        "image_id", equalTo(randomProductDto.getImageId().toString()));
        supplierRepository.deleteById(existsProductInDb.getSupplier().getId());
        supplierRepository.deleteById(randomProductDto.getSupplierId());
        imageService.deleteById(existsProductInDb.getImage().getId());
        imageService.deleteById(randomProductDto.getImageId());
    }

    @Test
    @DisplayName("API. PATCH. /api/v1/products/amount/{id}. Reduce exists product amount in stock")
    void testPatchExistsProductReduceAmount_200() {
        Product existsProductInDb = randomModels.getRandomProduct();
        supplierRepository.save(existsProductInDb.getSupplier());
        imageService.saveImage(existsProductInDb.getImage());
        productRepository.save(existsProductInDb);
        RestAssured.given()
                .port(port)
                .when()
                .params("amount", existsProductInDb.getAvailableStock())
                .patch("api/v1/products/amount/{id}", existsProductInDb.getId())
                .then()
                .statusCode(HTTP_OK)
                .body("id", equalTo(existsProductInDb.getId().toString()),
                        "name", equalTo(existsProductInDb.getName()),
                        "category", equalTo(existsProductInDb.getCategory()),
                        "price.toString()", equalTo(existsProductInDb.getPrice().toString()),
                        "available_stock", equalTo(0),
                        "last_update_date", is(notNullValue()),
                        "supplier_id", equalTo(existsProductInDb.getSupplier().getId().toString()),
                        "image_id", equalTo(existsProductInDb.getImage().getId().toString()));
        supplierRepository.deleteById(existsProductInDb.getSupplier().getId());
        imageService.deleteById(existsProductInDb.getImage().getId());
    }

    @Test
    @DisplayName("API. GET. /api/v1/products/image/{id}. Get image by exists product id")
    void testGetImageByExistsProductId_200() {
        Product existsProductInDb = randomModels.getRandomProduct();
        byte[] expectedImageBytes = existsProductInDb.getImage().getImage();
        supplierRepository.save(existsProductInDb.getSupplier());
        imageService.saveImage(existsProductInDb.getImage());
        productRepository.save(existsProductInDb);
        byte[] productImage = RestAssured.given()
                .port(port)
                .when()
                .get("api/v1/products/image/{id}", existsProductInDb.getId())
                .then()
                .statusCode(HTTP_OK)
                .extract()
                .asByteArray();
        Assertions.assertArrayEquals(expectedImageBytes, productImage);
        supplierRepository.deleteById(existsProductInDb.getSupplier().getId());
        imageService.deleteById(existsProductInDb.getImage().getId());
    }

    @Test
    @DisplayName("API. DELETE. /api/v1/products/{id}. Delete exists product")
    void testDeleteExistsProduct_204() {
        Product existsProductInDb = randomModels.getRandomProduct();
        supplierRepository.save(existsProductInDb.getSupplier());
        imageService.saveImage(existsProductInDb.getImage());
        productRepository.save(existsProductInDb);
        RestAssured.given()
                .port(port)
                .when()
                .delete("api/v1/products/{id}", existsProductInDb.getId())
                .then()
                .statusCode(HTTP_NO_CONTENT);
        supplierRepository.deleteById(existsProductInDb.getSupplier().getId());
        imageService.deleteById(existsProductInDb.getImage().getId());
    }

    @Test
    @DisplayName("API. DELETE. /api/v1/products/{id}. Delete non exists product")
    void testDeleteNonExistsProduct_404() {
        UUID randomId = UUID.randomUUID();
        RestAssured.given()
                .port(port)
                .when()
                .delete("api/v1/products/{id}", randomId)
                .then()
                .statusCode(HTTP_NOT_FOUND)
                .body("path", endsWith("/api/v1/products/%s".formatted(randomId)),
                        "message", equalTo("Product with id: %s not found".formatted(randomId)));
    }
}
