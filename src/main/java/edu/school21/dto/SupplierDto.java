package edu.school21.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplierDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID id;

    @NotBlank(message = "name: cannot be empty")
    @Size(min = 1, max = 255, message = "Name must be between 1 and 255 characters")
    @Schema(description = "Supplier name", example = "Igor")
    private String name;

    @Valid
    @NotNull(message = "Address cannot be empty, required fields: country, city, street")
    private AddressDto address;

    @Pattern(
            regexp = "^(8|\\+7)\\(\\d{3}\\)\\d{3}-\\d{2}-\\d{2}$",
            message = "Phone number must be in the format: +7(XXX)XXX-XX-XX or 8(XXX)XXX-XX-XX"
    )
    @JsonProperty("phone_number")
    @Schema(description = "Supplier phone number", example = "+7(123)456-78-90 or 8(123)456-78-90")
    private String phoneNumber;
}
