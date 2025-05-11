package edu.school21.utils;

import edu.school21.models.Client;
import edu.school21.models.Product;
import edu.school21.models.Supplier;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class PatchMappingUtils {
    private ModelMapper modelMapper = new ModelMapper();

    public PatchMappingUtils() {
        modelMapper.getConfiguration().setSkipNullEnabled(true);
    }

    /**
     * Метод копирует поля из sourceClient в destinationClient пропуская поля null
     *
     * @param destinationClient - конечный объект
     * @param sourceClient      - объект для копирования
     * @return Client - конечный объект
     */
    public Client mappingClient(Client destinationClient, Client sourceClient) {
        if (sourceClient.getAddress() != null) {
            modelMapper.map(sourceClient.getAddress(), destinationClient.getAddress());
        }
        modelMapper.map(sourceClient, destinationClient);
        return destinationClient;
    }

    /**
     * Метод копирует поля из sourceSupplier в destinationSupplier пропуская поля null
     *
     * @param destinationSupplier - конечный объект
     * @param sourceSupplier      - объект для копирования
     * @return Supplier - конечный объект
     */
    public Supplier mappingSupplier(Supplier destinationSupplier, Supplier sourceSupplier) {
        if (sourceSupplier.getAddress() != null) {
            modelMapper.map(sourceSupplier.getAddress(), destinationSupplier.getAddress());
        }
        modelMapper.map(sourceSupplier, destinationSupplier);
        return destinationSupplier;
    }

    /**
     * Метод копирует поля из sourceProduct в destinationProduct пропуская поля null
     *
     * @param destinationProduct - конечный объект
     * @param sourceProduct      - объект для копирования
     * @return Supplier - конечный объект
     */
    public Product mappingProduct(Product destinationProduct, Product sourceProduct) {
        modelMapper.map(sourceProduct, destinationProduct);
        return destinationProduct;
    }
}
