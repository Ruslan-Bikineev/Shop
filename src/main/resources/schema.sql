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
    address_id        uuid REFERENCES addresses (id),
    name              VARCHAR(255) NOT NULL,
    gender            VARCHAR(255) NOT NULL,
    surname           VARCHAR(255) NOT NULL,
    birthday          DATE         NOT NULL,
    registration_date TIMESTAMP        DEFAULT now()::TIMESTAMP(2)
);

CREATE TABLE IF NOT EXISTS suppliers
(
    id           uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    address_id   uuid REFERENCES addresses (id),
    name         VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20)  NOT NULL
);

CREATE TABLE IF NOT EXISTS images
(
    id    uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    image bytea NOT NULL
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


