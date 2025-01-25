CREATE TABLE IF NOT EXISTS addresses
(
    id      uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    country VARCHAR(50)  NOT NULL,
    city    VARCHAR(50)  NOT NULL,
    street  VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS clients
(
    id                uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    name              VARCHAR(255) NOT NULL,
    surname           VARCHAR(255) NOT NULL,
    birthday          DATE         NOT NULL,
    gender            VARCHAR(255) NOT NULL,
    registration_date TIMESTAMP        DEFAULT now()::TIMESTAMP(2),
    address_id        uuid REFERENCES addresses (id)
);

CREATE TABLE IF NOT EXISTS images
(
    id    uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    image bytea NOT NULL
);

CREATE TABLE IF NOT EXISTS suppliers
(
    id           uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    address_id   uuid REFERENCES addresses (id),
    name         VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20)  NOT NULL
);

CREATE TABLE IF NOT EXISTS products
(
    id               uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    supplier_id      uuid         NOT NULL REFERENCES suppliers (id),
    image_id         uuid         NOT NULL REFERENCES images (id),
    name             VARCHAR(255) NOT NULL,
    category         VARCHAR(255) NOT NULL,
    price            DECIMAL      NOT NULL CHECK (price > 0),
    available_stock  INTEGER      NOT NULL CHECK ( available_stock >= 0 ),
    last_update_date TIMESTAMP        DEFAULT now()::TIMESTAMP(2)
);

-- Вставка тестовых данных в таблицу addresses
INSERT INTO addresses (country, city, street)
VALUES ('USA', 'New York', '5th Avenue, 101'),
       ('Canada', 'Toronto', 'Queen Street, 55'),
       ('UK', 'London', 'Baker Street, 221B'),
       ('Germany', 'Berlin', 'Unter den Linden, 1'),
       ('France', 'Paris', 'Champs-Élysées, 120');

-- Вставка тестовых данных в таблицу clients
INSERT INTO clients (name, surname, birthday, gender, address_id)
VALUES ('John', 'Doe', '1990-05-15', 'male', (SELECT id FROM addresses WHERE street = '5th Avenue, 101')),
       ('Jane', 'Smith', '1985-11-20', 'female', (SELECT id FROM addresses WHERE street = 'Queen Street, 55')),
       ('Sherlock', 'Holmes', '1980-01-06', 'male', (SELECT id FROM addresses WHERE street = 'Baker Street, 221B')),
       ('Hans', 'Muller', '1992-09-10', 'male', (SELECT id FROM addresses WHERE street = 'Unter den Linden, 1')),
       ('Marie', 'Curie', '1975-07-22', 'female', (SELECT id FROM addresses WHERE street = 'Champs-Élysées, 120'));

-- Вставка тестовых данных в таблицу suppliers
INSERT INTO suppliers (address_id, name, phone_number)
VALUES ((SELECT id FROM addresses WHERE street = '5th Avenue, 101'), 'Supplier A', '8(123)456-78-90'),
       ((SELECT id FROM addresses WHERE street = 'Queen Street, 55'), 'Supplier B', '+7(234)567-89-01'),
       ((SELECT id FROM addresses WHERE street = 'Baker Street, 221B'), 'Supplier C', '+8(345)678-90-12'),
       ((SELECT id FROM addresses WHERE street = 'Unter den Linden, 1'), 'Supplier D', '+7(456)789-01-23'),
       ((SELECT id FROM addresses WHERE street = 'Champs-Élysées, 120'), 'Supplier E', '8(567)890-12-34');

-- Вставка тестовых данных в таблицу images
INSERT INTO images (image)
VALUES (decode('aW1hZ2Ux', 'base64')),
       (decode('aW1hZ2Uy', 'base64')),
       (decode('aW1hZ2Uz', 'base64')),
       (decode('aW1hZ2U0', 'base64')),
       (decode('aW1hZ2U1', 'base64'));

-- Вставка тестовых данных в таблицу products
INSERT INTO products (supplier_id, image_id, name, category, price, available_stock)
VALUES ((SELECT id FROM suppliers WHERE name = 'Supplier A'), (SELECT id FROM images WHERE image = 'image1'),
        'Product A', 'Electronics', 99.99, 10),
       ((SELECT id FROM suppliers WHERE name = 'Supplier B'), (SELECT id FROM images WHERE image = 'image2'),
        'Product B', 'Clothing', 49.99, 5),
       ((SELECT id FROM suppliers WHERE name = 'Supplier C'), (SELECT id FROM images WHERE image = 'image3'),
        'Product C', 'Toys', 19.99, 15),
       ((SELECT id FROM suppliers WHERE name = 'Supplier D'), (SELECT id FROM images WHERE image = 'image4'),
        'Product D', 'Books', 24.99, 8),
       ((SELECT id FROM suppliers WHERE name = 'Supplier E'), (SELECT id FROM images WHERE image = 'image5'),
        'Product E', 'Sports', 79.99, 3);
