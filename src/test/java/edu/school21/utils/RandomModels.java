package edu.school21.utils;

import com.github.javafaker.Faker;
import edu.school21.constants.Gender;
import edu.school21.models.Address;
import edu.school21.models.Category;
import edu.school21.models.Client;
import edu.school21.models.Image;
import edu.school21.models.Product;
import edu.school21.models.Supplier;
import org.springframework.boot.test.context.TestComponent;

import java.time.ZoneId;
import java.util.Locale;
import java.util.Random;

@TestComponent
public class RandomModels {
    private Locale locale = new Locale("ru");
    private Faker faker = new Faker(locale);

    public Client getRandomClient() {
        Client client = new Client();
        client.setName(faker.name().firstName());
        client.setSurname(faker.name().lastName());
        client.setBirthday(faker.date().birthday()
                .toInstant().atZone(ZoneId.systemDefault())
                .toLocalDate());
        client.setGender(faker.options().option(Gender.MALE, Gender.FEMALE));
        client.setAddress(getRandomAddress());
        return client;
    }

    public Supplier getRandomSupplier() {
        Supplier supplier = new Supplier();
        supplier.setName(faker.name().firstName());
        supplier.setAddress(getRandomAddress());
        supplier.setPhoneNumber(faker.phoneNumber().phoneNumber());
        return supplier;
    }

    public Address getRandomAddress() {
        Address address = new Address();
        address.setCountry(faker.country().name());
        address.setCity(faker.address().city());
        address.setStreet(faker.address().streetAddress());
        return address;
    }

    public Image getRandomImage() {
        Image image = new Image();
        byte[] byteArray = new byte[20];
        Random random = new Random();
        random.nextBytes(byteArray);
        image.setImage(byteArray);
        return image;
    }

    public Category getRandomCategory() {
        Category category = new Category();
        category.setName(faker.commerce().department());
        return category;
    }

    public Product getRandomProduct() {
        Product product = new Product();
        product.setName(faker.commerce().productName());
        product.setCategory(getRandomCategory());
        product.setPrice(Double.valueOf(faker.commerce().price().replace(',', '.')));
        product.setAvailableStock(Integer.valueOf(faker.number().numberBetween(10, 100)));
        product.setSupplier(getRandomSupplier());
        product.setImage(getRandomImage());
        return product;
    }
}
