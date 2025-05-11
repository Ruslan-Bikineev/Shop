package edu.school21.controllers;

import edu.school21.ShopApplicationTests;
import edu.school21.models.Image;
import edu.school21.services.ImageService;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_NO_CONTENT;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class ImageControllerTests extends ShopApplicationTests {
    @Autowired
    private ImageService imageService;

    @Test
    @DisplayName("API. GET. /api/v1/images/{id}. Get exists image by id")
    void testGetExistsImageById_200() {
        Image randomImage = randomModels.getRandomImage();
        byte[] expectedImageBytes = randomImage.getImage();
        imageService.saveImage(randomImage);
        byte[] responseImageBytes = RestAssured.given()
                .port(port)
                .when()
                .get("api/v1/images/{id}", randomImage.getId())
                .then()
                .statusCode(HTTP_OK)
                .extract()
                .asByteArray();
        Assertions.assertArrayEquals(expectedImageBytes, responseImageBytes);
        imageService.deleteById(randomImage.getId());
    }

    @Test
    @DisplayName("API. GET. /api/v1/images/{id}. Get non exists image by id")
    void testGetNonExistsImageById_404() {
        UUID randomId = UUID.randomUUID();
        RestAssured.given()
                .port(port)
                .when()
                .get("api/v1/images/{id}", randomId)
                .then()
                .statusCode(HTTP_NOT_FOUND)
                .body("path", endsWith("/api/v1/images/%s".formatted(randomId)),
                        "message", equalTo("Image with id: %s not found".formatted(randomId)));
    }

    @Test
    @DisplayName("API. POST. /api/v1/images. Created image")
    void testPostImage_201() throws IOException {
        File tempFile = File.createTempFile("temp-image", ".tmp");
        tempFile.deleteOnExit();
        Image randomImage = randomModels.getRandomImage();
        FileOutputStream fos = new FileOutputStream(tempFile);
        fos.write(randomImage.getImage());
        UUID createdImageId = RestAssured.given()
                .contentType(ContentType.MULTIPART)
                .port(port)
                .multiPart("file", tempFile)
                .when()
                .post("api/v1/images")
                .then()
                .statusCode(HTTP_CREATED)
                .body("id", notNullValue(),
                        "message", equalTo("Image saved successfully"))
                .extract()
                .jsonPath()
                .getUUID("id");
        imageService.deleteById(createdImageId);
    }

    @Test
    @DisplayName("API. POST. /api/v1/images. Try created empty image")
    void testPostEmptyImage_404() throws IOException {
        File tempFile = File.createTempFile("temp-image", ".tmp");
        tempFile.deleteOnExit();
        FileOutputStream fos = new FileOutputStream(tempFile);
        fos.write(new byte[0]);
        RestAssured.given()
                .contentType(ContentType.MULTIPART)
                .port(port)
                .multiPart("file", tempFile)
                .when()
                .post("api/v1/images")
                .then()
                .statusCode(HTTP_BAD_REQUEST)
                .body("path", endsWith("/api/v1/images"),
                        "message", equalTo("File cannot be empty"));
    }

    @Test
    @DisplayName("API. PUT. /api/v1/images/{id}. Update exists image")
    void testPutExistsImage_200() throws IOException {
        File tempFile = File.createTempFile("temp-image", ".tmp");
        tempFile.deleteOnExit();
        Image oldEandomImage = randomModels.getRandomImage();
        Image newRandomImage = randomModels.getRandomImage();
        FileOutputStream fos = new FileOutputStream(tempFile);
        fos.write(newRandomImage.getImage());
        byte[] expectedImageBytes = newRandomImage.getImage();
        imageService.saveImage(oldEandomImage);
        RestAssured.given()
                .contentType(ContentType.MULTIPART)
                .port(port)
                .multiPart("file", tempFile)
                .when()
                .put("api/v1/images/{id}", oldEandomImage.getId())
                .then()
                .statusCode(HTTP_OK)
                .body("id", equalTo(oldEandomImage.getId().toString()),
                        "message", equalTo("Image updated successfully"));
        byte[] responseImageBytes = RestAssured.given()
                .port(port)
                .when()
                .get("api/v1/images/{id}", oldEandomImage.getId())
                .then()
                .statusCode(HTTP_OK)
                .extract()
                .asByteArray();
        Assertions.assertArrayEquals(expectedImageBytes, responseImageBytes);
        imageService.deleteById(oldEandomImage.getId());
    }

    @Test
    @DisplayName("API. PUT. /api/v1/images/{id}. Update non exists image")
    void testPutNonExistsImage_200() throws IOException {
        File tempFile = File.createTempFile("temp-image", ".tmp");
        tempFile.deleteOnExit();
        Image randomImage = randomModels.getRandomImage();
        FileOutputStream fos = new FileOutputStream(tempFile);
        fos.write(randomImage.getImage());
        byte[] expectedImageBytes = randomImage.getImage();
        UUID createdImageId = RestAssured.given()
                .contentType(ContentType.MULTIPART)
                .port(port)
                .multiPart("file", tempFile)
                .when()
                .put("api/v1/images/{id}", UUID.randomUUID())
                .then()
                .statusCode(HTTP_OK)
                .body("id", notNullValue(),
                        "message", equalTo("Image saved successfully"))
                .extract()
                .jsonPath()
                .getUUID("id");
        byte[] responseImageBytes = RestAssured.given()
                .port(port)
                .when()
                .get("api/v1/images/{id}", createdImageId)
                .then()
                .statusCode(HTTP_OK)
                .extract()
                .asByteArray();
        Assertions.assertArrayEquals(expectedImageBytes, responseImageBytes);
        imageService.deleteById(createdImageId);
    }

    @Test
    @DisplayName("API. DELETE. /api/v1/images/{id}. Delete exists image")
    void testDeleteExistsImage_204() {
        Image randomImage = randomModels.getRandomImage();
        imageService.saveImage(randomImage);
        RestAssured.given()
                .port(port)
                .when()
                .delete("api/v1/images/{id}", randomImage.getId())
                .then()
                .statusCode(HTTP_NO_CONTENT);
    }

    @Test
    @DisplayName("API. DELETE. /api/v1/images/{id}. Delete non exists image")
    void testDeleteNonExistsImage_404() {
        UUID randomId = UUID.randomUUID();
        RestAssured.given()
                .port(port)
                .when()
                .delete("api/v1/images/{id}", randomId)
                .then()
                .statusCode(HTTP_NOT_FOUND)
                .body("path", endsWith("/api/v1/images/%s".formatted(randomId)),
                        "message", equalTo("Image with id: %s not found".formatted(randomId)));
    }
}
