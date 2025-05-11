package edu.school21.controllers;

import edu.school21.dto.ErrorInfoDto;
import edu.school21.dto.SupplierDto;
import edu.school21.models.Supplier;
import edu.school21.services.SupplierService;
import edu.school21.utils.MappingUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Tag(name = "Supplier", description = "Supplier operations")
@Validated
@RestController
@RequestMapping("/api/v1/suppliers")
public class SupplierController {
    private MappingUtils mappingUtils;
    private SupplierService supplierService;

    public SupplierController(MappingUtils mappingUtils,
                              SupplierService supplierService) {
        this.mappingUtils = mappingUtils;
        this.supplierService = supplierService;
    }

    @Operation(summary = "Get supplier by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = SupplierDto.class))
                    }),
            @ApiResponse(responseCode = "404", description = "Supplier not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorInfoDto.class))
                    })
    })
    @GetMapping("/{id}")
    public SupplierDto getSupplier(@PathVariable("id") UUID id) {
        Supplier supplier = supplierService.findById(id);
        return mappingUtils.convertToDto(supplier);
    }

    @Operation(summary = "Get all suppliers with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(type = "array", implementation = SupplierDto.class))
                    }),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorInfoDto.class))
                    })
    })
    @GetMapping
    public List<SupplierDto> getSuppliers(
            @RequestParam(required = false)
            @Min(value = 1, message = "must be greater than 0")
            @Max(value = 25, message = "must be less than 26")
            Integer limit,
            @RequestParam(required = false)
            @Min(value = 0, message = "must be positive")
            Integer offset) {
        if (limit == null || offset == null) {
            return supplierService.findAll().stream()
                    .map(mappingUtils::convertToDto)
                    .toList();
        }
        return supplierService.findAll(PageRequest.of(offset, limit)).stream()
                .map(mappingUtils::convertToDto)
                .toList();
    }

    @Operation(summary = "Create supplier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Adds a new supplier to the database " +
                    "and returns the created supplier.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = SupplierDto.class))
                    }),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorInfoDto.class))
                    })
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SupplierDto createSupplier(@Valid @RequestBody SupplierDto supplierDto) {
        Supplier supplier = mappingUtils.convertToEntity(supplierDto);
        supplier = supplierService.saveSupplier(supplier);
        return mappingUtils.convertToDto(supplier);
    }

    @Operation(summary = "Update supplier by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update exists supplier" +
                    "in database and returns the updated supplier.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = SupplierDto.class))
                    }),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorInfoDto.class))
                    }),
            @ApiResponse(responseCode = "404", description = "Not found supplier",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorInfoDto.class))
                    })
    })
    @PatchMapping("/{id}")
    public SupplierDto patchUpdateSupplier(
            @PathVariable("id") UUID id,
            @RequestBody SupplierDto supplierDto) {
        Supplier supplier = mappingUtils.convertToEntity(supplierDto);
        supplier = supplierService.patchUpdateSupplier(id, supplier);
        return mappingUtils.convertToDto(supplier);
    }

    @Operation(summary = "Create or update supplier by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update exists supplier or adds a new supplier " +
                    "in database and returns the updated or created supplier.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = SupplierDto.class))
                    }),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorInfoDto.class))
                    })
    })
    @PutMapping("/{id}")
    public SupplierDto putUpdateSupplier(
            @PathVariable("id") UUID id,
            @Valid @RequestBody SupplierDto supplierDto) {
        Supplier supplier = mappingUtils.convertToEntity(supplierDto);
        supplier = supplierService.putUpdateSupplier(id, supplier);
        return mappingUtils.convertToDto(supplier);
    }

    @Operation(summary = "Delete supplier by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Supplier and its products successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Not found supplier",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorInfoDto.class))
                    })
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSupplier(@PathVariable("id") UUID id) {
        supplierService.deleteSupplier(id);
    }
}
