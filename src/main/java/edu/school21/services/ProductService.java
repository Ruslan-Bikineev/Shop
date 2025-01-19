package edu.school21.services;

import edu.school21.exceptions.InsufficientStockException;
import edu.school21.models.Image;
import edu.school21.models.Product;
import edu.school21.repositories.ProductRepository;
import edu.school21.utils.ImageUtils;
import edu.school21.utils.PatchMappingUtils;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProductService {
    private ImageUtils imageUtils;
    private ProductRepository productRepository;
    private PatchMappingUtils patchMappingUtils;

    public ProductService(ImageUtils imageUtils,
                          ProductRepository productRepository,
                          PatchMappingUtils patchMappingUtils) {
        this.imageUtils = imageUtils;
        this.productRepository = productRepository;
        this.patchMappingUtils = patchMappingUtils;
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Page<Product> findAll(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public Product findById(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Product with id: %s not found".formatted(id)));
    }

    public byte[] getImageByProductId(UUID id) {
        Image image = findById(id).getImage();
        return imageUtils.decompressImage(image.getImage());
    }


    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    public void deleteById(UUID id) {
        existsById(id);
        productRepository.deleteById(id);
    }

    public void existsById(UUID id) {
        if (!productRepository.existsById(id)) {
            throw new EntityNotFoundException("Product with id: %s not found".formatted(id));
        }
    }

    public Product putUpdateProduct(UUID id, Product product) {
        try {
            existsById(id);
            product.setId(id);
            return productRepository.save(product);
        } catch (EntityNotFoundException e) {
            return productRepository.save(product);
        }
    }

    public Product patchReductionAvailableProduct(UUID id, Integer amount) {
        Product product = findById(id);
        if (product.getAvailableStock() < amount) {
            throw new InsufficientStockException(("Insufficient stock for product with id: %s. " +
                    "Available stock: %d, requested: %d").formatted(id, product.getAvailableStock(), amount));
        }
        product.setAvailableStock(product.getAvailableStock() - amount);
        return productRepository.save(product);
    }

    public Product patchUpdateProduct(UUID id, Product product) {
        Product existingProduct = findById(id);
        patchMappingUtils.mappingProduct(existingProduct, product);
        return productRepository.save(existingProduct);
    }
}
