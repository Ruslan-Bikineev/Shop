package edu.school21.controllers;

import edu.school21.dto.ErrorInfoDto;
import edu.school21.dto.ProductDto;
import edu.school21.models.Product;
import edu.school21.services.ProductService;
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
import org.springframework.http.MediaType;
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

@Tag(name = "Products", description = "Products operations")
@Validated
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    private MappingUtils mappingUtils;
    private ProductService productService;

    public ProductController(MappingUtils mappingUtils,
                             ProductService productService) {
        this.mappingUtils = mappingUtils;
        this.productService = productService;
    }

    @Operation(summary = "Get product by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductDto.class))
                    }),
            @ApiResponse(responseCode = "404", description = "Supplier not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorInfoDto.class))
                    })
    })
    @GetMapping("/{id}")
    public ProductDto findById(@PathVariable("id") UUID id) {
        Product product = productService.findById(id);
        return mappingUtils.convertToDto(product);
    }

    @Operation(summary = "Get all products with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(type = "array", implementation = ProductDto.class))
                    }),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorInfoDto.class))
                    })
    })
    @GetMapping
    public List<ProductDto> findAll(
            @RequestParam(required = false)
            @Min(value = 1, message = "must be greater than 0")
            @Max(value = 25, message = "must be less than 26")
            Integer limit,
            @RequestParam(required = false)
            @Min(value = 0, message = "must be positive")
            Integer offset) {
        if (limit == null || offset == null) {
            return productService.findAll().stream()
                    .map(mappingUtils::convertToDto)
                    .toList();
        }
        return productService.findAll(PageRequest.of(offset, limit)).stream()
                .map(mappingUtils::convertToDto)
                .toList();
    }

    @Operation(summary = "Get product image by product id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success",
                    content = {@Content(mediaType = "application/ocet-streama",
                            schema = @Schema(implementation = byte[].class))
                    }),
            @ApiResponse(responseCode = "404", description = "Not found ima",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorInfoDto.class))
                    })
    })
    @GetMapping(value = "/image/{id}",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public byte[] getImage(@PathVariable("id") UUID id) {
        return productService.getImageByProductId(id);
    }

    @Operation(summary = "Create product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Add new product to database and returns the created product.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(type = "array", implementation = ProductDto.class))
                    }),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorInfoDto.class))
                    })
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDto createProduct(@Valid @RequestBody ProductDto productDto) {
        Product product = mappingUtils.convertToEntity(productDto);
        product = productService.saveProduct(product);
        return mappingUtils.convertToDto(product);
    }

    @Operation(summary = "Create or update product by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update exists product or adds a new product " +
                    "in database and returns the updated or created product.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductDto.class))
                    }),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorInfoDto.class))
                    })
    })
    @PutMapping("/{id}")
    public ProductDto putUpdateProduct(@PathVariable("id") UUID id,
                                       @Valid @RequestBody ProductDto productDto) {
        Product product = mappingUtils.convertToEntity(productDto);
        product = productService.putUpdateProduct(id, product);
        return mappingUtils.convertToDto(product);

    }

    @Operation(summary = "Update product by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update exists product" +
                    "in database and returns the updated product.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductDto.class))
                    }),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorInfoDto.class))
                    }),
            @ApiResponse(responseCode = "404", description = "Not found product",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorInfoDto.class))
                    })
    })
    @PatchMapping("/{id}")
    public ProductDto patchUpdateProduct(@PathVariable("id") UUID id,
                                         @RequestBody ProductDto productDto) {
        Product product = mappingUtils.convertToEntity(productDto);
        product = productService.patchUpdateProduct(id, product);
        return mappingUtils.convertToDto(product);
    }

    @Operation(summary = "Reducing the amount of products in stock by id product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reducing the product amount" +
                    "in database and returns the updated product.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductDto.class))
                    }),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorInfoDto.class))
                    }),
            @ApiResponse(responseCode = "404", description = "Not found product",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorInfoDto.class))
                    })
    })
    @PatchMapping("/amount/{id}")
    public ProductDto patchReductionAvailableProduct(
            @PathVariable("id") UUID id,
            @Min(value = 0, message = "amount: must be greater than 0")
            @RequestParam("amount") Integer amount) {
        Product product = productService.patchReductionAvailableProduct(id, amount);
        return mappingUtils.convertToDto(product);
    }

    @Operation(summary = "Delete product by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Product successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Not found product",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorInfoDto.class))
                    })
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable("id") UUID id) {
        productService.deleteById(id);
    }
}
