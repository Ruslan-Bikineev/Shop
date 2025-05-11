package edu.school21.controllers;

import edu.school21.dto.ClientDto;
import edu.school21.dto.ErrorInfoDto;
import edu.school21.models.Client;
import edu.school21.services.ClientService;
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
import jakarta.validation.constraints.NotBlank;
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

@Tag(name = "Client", description = "Client operations")
@Validated
@RestController
@RequestMapping("/api/v1/clients")
public class ClientController {
    private MappingUtils mappingUtils;
    private ClientService clientService;

    public ClientController(MappingUtils mappingUtils,
                            ClientService clientService) {
        this.mappingUtils = mappingUtils;
        this.clientService = clientService;
    }

    @Operation(summary = "Get all clients with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(type = "array", implementation = ClientDto.class))
                    }),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorInfoDto.class))
                    })
    })
    @GetMapping
    public List<ClientDto> getClients(
            @RequestParam(required = false)
            @Min(value = 1, message = "must be greater than 0")
            @Max(value = 25, message = "must be less than 26")
            Integer limit,
            @RequestParam(required = false)
            @Min(value = 0, message = "must be positive")
            Integer offset) {
        if (limit == null || offset == null) {
            return clientService.findAll().stream()
                    .map(mappingUtils::convertToDto)
                    .toList();
        }
        return clientService.findAll(PageRequest.of(offset, limit)).stream()
                .map(mappingUtils::convertToDto)
                .toList();
    }

    @Operation(summary = "Get all clients by name and surname")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(type = "array", implementation = ClientDto.class))
                    }),
            @ApiResponse(responseCode = "400", description = "name or surname cannot be empty",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorInfoDto.class))
                    })
    })
    @GetMapping("/search")
    public List<ClientDto> findClientsByNameAndSurname(
            @RequestParam("name")
            @NotBlank(message = "cannot be empty") String name,
            @RequestParam("surname")
            @NotBlank(message = "cannot be empty") String surname) {
        List<Client> clients = clientService.findClientsByNameAndSurname(name, surname);
        return clients.stream().map(mappingUtils::convertToDto).toList();
    }

    @Operation(summary = "Create client")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Adds a new client to the database " +
                    "and returns the created client.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ClientDto.class))
                    }),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorInfoDto.class))
                    })
    })
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public ClientDto createClient(@Valid @RequestBody ClientDto clientDto) {
        Client client = mappingUtils.convertToEntity(clientDto);
        client = clientService.saveClient(client);
        return mappingUtils.convertToDto(client);
    }

    @Operation(summary = "Update client by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update exists client" +
                    "in database and returns the updated client.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ClientDto.class))
                    }),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorInfoDto.class))
                    }),
            @ApiResponse(responseCode = "404", description = "Client not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorInfoDto.class))
                    })
    })
    @PatchMapping("/{id}")
    public ClientDto patchUpdateClient(
            @PathVariable("id") UUID id,
            @RequestBody ClientDto clientDto) {
        clientDto.setId(id);
        Client client = mappingUtils.convertToEntity(clientDto);
        client = clientService.patchUpdateClient(client);
        return mappingUtils.convertToDto(client);
    }

    @Operation(summary = "Create or update client by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update exists client or adds a new client " +
                    "in database and returns the updated or created client.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ClientDto.class))
                    }),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorInfoDto.class))
                    })
    })
    @PutMapping("/{id}")
    public ClientDto putUpdateClient(
            @PathVariable("id") UUID id,
            @Valid @RequestBody ClientDto clientDto) {
        Client client = mappingUtils.convertToEntity(clientDto);
        client = clientService.putUpdateClient(id, client);
        return mappingUtils.convertToDto(client);
    }

    @Operation(summary = "Delete client by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Client successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Client not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorInfoDto.class))
                    })
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteClient(@PathVariable("id") UUID id) {
        clientService.deleteClient(id);
    }
}
