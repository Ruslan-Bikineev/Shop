package edu.school21.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.school21.constants.Gender;
import edu.school21.validators.BirthDate;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID id;

    @NotBlank(message = "name: cannot be empty")
    @Size(min = 1, max = 255, message = "Name must be between 1 and 255 characters")
    @Schema(description = "Client name", example = "John")
    private String name;

    @NotBlank(message = "surname: cannot be empty")
    @Size(min = 1, max = 255, message = "Surname must be between 1 and 255 characters")
    @Schema(description = "Client surname", example = "Doe")
    private String surname;

    @NotNull(message = "cannot be empty")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Europe/Moscow")
    @BirthDate(message = "The birth date must be greater or equal than 18")
    @Schema(description = "Client birthday date", example = "2000-01-01")
    private LocalDate birthday;

    @NotNull(message = "gender: cannot be empty")
    @Schema(description = "Client gender", example = "male or female")
    private Gender gender;

    @JsonProperty(value = "registration_date", access = JsonProperty.Access.READ_ONLY)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss:SS", timezone = "Europe/Moscow")
    private Timestamp registrationDate;

    @Valid
    @NotNull(message = "Address cannot be empty, required fields: country, city, street")
    private AddressDto address;
}
