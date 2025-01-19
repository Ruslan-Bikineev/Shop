package edu.school21.utils;

import edu.school21.dto.ClientDto;
import edu.school21.dto.ProductDto;
import edu.school21.dto.SupplierDto;
import edu.school21.models.Client;
import edu.school21.models.Product;
import edu.school21.models.Supplier;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class MappingUtils {
    private ModelMapper modelMapper = new ModelMapper();

    public ClientDto convertToDto(Client client) {
        ClientDto clientDto = modelMapper.map(client, ClientDto.class);
        return clientDto;
    }

    public Client convertToEntity(ClientDto clientDto) {
        Client client = modelMapper.map(clientDto, Client.class);
        return client;
    }

    public SupplierDto convertToDto(Supplier supplier) {
        SupplierDto supplierDto = modelMapper.map(supplier, SupplierDto.class);
        return supplierDto;
    }

    public Supplier convertToEntity(SupplierDto supplierDto) {
        Supplier supplier = modelMapper.map(supplierDto, Supplier.class);
        return supplier;
    }

    public Product convertToEntity(ProductDto productDto) {
        Product product = modelMapper.map(productDto, Product.class);
        return product;
    }

    public ProductDto convertToDto(Product product) {
        ProductDto productDto = modelMapper.map(product, ProductDto.class);
        return productDto;
    }
}
