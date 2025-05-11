package edu.school21.services;

import edu.school21.models.Supplier;
import edu.school21.repositories.SupplierRepository;
import edu.school21.utils.PatchMappingUtils;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class SupplierService {
    private PatchMappingUtils patchMappingUtils;
    private SupplierRepository supplierRepository;

    public SupplierService(PatchMappingUtils patchMappingUtils,
                           SupplierRepository supplierRepository) {
        this.patchMappingUtils = patchMappingUtils;
        this.supplierRepository = supplierRepository;
    }

    public Supplier saveSupplier(Supplier supplier) {
        return supplierRepository.save(supplier);
    }

    public Supplier findById(UUID id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Supplier with id: %s not found".formatted(id)));
    }

    public List<Supplier> findAll() {
        return supplierRepository.findAll();
    }

    public Page<Supplier> findAll(Pageable pageable) {
        return supplierRepository.findAll(pageable);
    }

    public void deleteSupplier(UUID id) {
        existsById(id);
        supplierRepository.deleteById(id);
    }

    public Supplier putUpdateSupplier(UUID id, Supplier supplier) {
        try {
            Supplier existingSupplier = findById(id);
            supplier.setId(id);
            supplier.getAddress().setId(existingSupplier.getAddress().getId());
            return supplierRepository.save(supplier);
        } catch (EntityNotFoundException e) {
            return supplierRepository.save(supplier);
        }
    }

    public Supplier patchUpdateSupplier(UUID id, Supplier supplier) {
        Supplier existingSupplier = findById(id);
        patchMappingUtils.mappingSupplier(existingSupplier, supplier);
        return supplierRepository.save(existingSupplier);
    }

    private void existsById(UUID id) {
        if (!supplierRepository.existsById(id)) {
            throw new EntityNotFoundException("Supplier with id: %s not found".formatted(id));
        }
    }
}
