package edu.school21.services;

import edu.school21.models.Client;
import edu.school21.repositories.ClientRepository;
import edu.school21.utils.PatchMappingUtils;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ClientService {
    private ClientRepository clientRepository;
    private PatchMappingUtils patchMappingUtils;

    public ClientService(ClientRepository clientRepository,
                         PatchMappingUtils patchMappingUtils) {
        this.clientRepository = clientRepository;
        this.patchMappingUtils = patchMappingUtils;
    }

    public Client saveClient(Client client) {
        return clientRepository.save(client);
    }

    public Client findById(UUID id) throws EntityNotFoundException {
        return clientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Client with id: %s not found".formatted(id)));
    }

    public Client putUpdateClient(UUID id, Client client) {
        try {
            Client existingClient = findById(id);
            client.setId(id);
            client.setRegistrationDate(existingClient.getRegistrationDate());
            client.getAddress().setId(existingClient.getAddress().getId());
            return clientRepository.save(client);
        } catch (EntityNotFoundException e) {
            return clientRepository.save(client);
        }
    }

    public Client patchUpdateClient(Client client) {
        Client existingClient = findById(client.getId());
        patchMappingUtils.mappingClient(existingClient, client);
        return clientRepository.save(existingClient);
    }

    public List<Client> findClientsByNameAndSurname(final String name,
                                                    final String surname) {
        return clientRepository.findByNameAndSurname(name, surname);
    }

    public Page<Client> findAll(Pageable pageable) {
        return clientRepository.findAll(pageable);
    }

    public List<Client> findAll() {
        return clientRepository.findAll();
    }

    public void deleteClient(UUID id) {
        existsById(id);
        clientRepository.deleteById(id);
    }

    private void existsById(UUID id) {
        if (!clientRepository.existsById(id)) {
            throw new EntityNotFoundException("Client with id: %s not found".formatted(id));
        }
    }
}
