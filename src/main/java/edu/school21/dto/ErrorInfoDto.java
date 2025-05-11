package edu.school21.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorInfoDto {
    @Schema(description = "Request path", example = "{url}/api/v1/{api}")
    private String path;

    @Schema(description = "Error message", example = "error message")
    private String message;
}