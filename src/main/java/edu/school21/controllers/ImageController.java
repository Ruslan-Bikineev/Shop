package edu.school21.controllers;

import edu.school21.dto.ErrorInfoDto;
import edu.school21.dto.MessageDto;
import edu.school21.exceptions.EmptyFileException;
import edu.school21.models.Image;
import edu.school21.services.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Tag(name = "Image", description = "Image operations")
@Validated
@RestController
@RequestMapping("/api/v1/images")
public class ImageController {
    private ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @Operation(summary = "Get image by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success",
                    content = {@Content(mediaType = "application/octet-stream",
                            schema = @Schema(implementation = byte[].class))
                    }),
            @ApiResponse(responseCode = "404", description = "Image not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorInfoDto.class))
                    })
    })
    @GetMapping(value = "/{id}",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public byte[] getImage(@PathVariable UUID id) {
        return imageService.findById(id).getImage();
    }

    @Operation(summary = "Create image")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Image saved successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = MessageDto.class))
                    }),
            @ApiResponse(responseCode = "400", description = "File cannot be empty",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorInfoDto.class))
                    })
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MessageDto saveImage(
            @NotNull(message = "cannot be empty")
            @RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new EmptyFileException("File cannot be empty");
        }
        Image image = new Image(file.getBytes());
        return imageService.saveImage(image);
    }

    @Operation(summary = "Create or update image by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update exists image or adds a new image " +
                    "in database and returns the updated or created image id.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = MessageDto.class))
                    }),
            @ApiResponse(responseCode = "400", description = "File cannot be empty",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorInfoDto.class))
                    })
    })
    @PutMapping("/{id}")
    public MessageDto putUpdateImage(
            @PathVariable("id") UUID id,
            @NotNull(message = "cannot be empty")
            @RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new EmptyFileException("File cannot be empty");
        }
        Image image = new Image(id, file.getBytes());
        return imageService.putUpdateImage(image);
    }

    @Operation(summary = "Delete image by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Image successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Not found image",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorInfoDto.class))
                    })
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteImage(@PathVariable UUID id) {
        imageService.deleteById(id);
    }
}
