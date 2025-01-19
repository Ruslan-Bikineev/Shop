package edu.school21.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID id;
    @NotBlank(message = "country: cannot be empty")
    @Size(min = 2, max = 50, message = "Country must be between 2 and 50 characters")
    @Schema(description = "Address country", example = "Russia")
    private String country;
    @NotBlank(message = "city: cannot be empty")
    @Size(min = 2, max = 50, message = "City must be between 2 and 50 characters")
    @Schema(description = "Address city", example = "Kazan")
    private String city;
    @NotBlank(message = "street: cannot be empty")
    @Size(min = 2, max = 100, message = "Street must be between 2 and 100 characters")
    @Schema(description = "Address street", example = "Lenina 12")
    private String street;
}