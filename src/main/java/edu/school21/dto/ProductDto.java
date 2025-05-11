package edu.school21.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID id;

    @NotBlank(message = "name: cannot be empty")
    @Size(min = 1, max = 255, message = "Name must be between 1 and 255 characters")
    private String name;

    @NotBlank(message = "category: cannot be empty")
    @Size(min = 1, max = 255, message = "Category must be between 1 and 255 characters")
    private String category;

    @NotNull(message = "price: cannot be empty")
    @Min(value = 0, message = "price: must be greater than 0")
    private Double price;

    @JsonProperty("available_stock")
    @NotNull(message = "available_stock: cannot be empty")
    @Min(value = 0, message = "available_stock: must be greater than 0")
    private Integer availableStock;

    @JsonProperty("last_update_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss:SS", timezone = "Europe/Moscow")
    private Timestamp lastUpdateDate;

    @JsonProperty("supplier_id")
    @NotNull(message = "supplier_id: cannot be empty")
    private UUID supplierId;

    @JsonProperty("image_id")
    @NotNull(message = "image_id: cannot be empty")
    private UUID imageId;
}
