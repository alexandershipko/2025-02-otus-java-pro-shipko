-- Address table
CREATE TABLE address
(
    id     BIGSERIAL    NOT NULL PRIMARY KEY,
    street VARCHAR(255) NOT NULL
);

-- Client table
CREATE TABLE client
(
    id         BIGSERIAL   NOT NULL PRIMARY KEY,
    name       VARCHAR(50) NOT NULL,
    address_id BIGINT,
    FOREIGN KEY (address_id) REFERENCES address (id) ON DELETE CASCADE
);

-- Phone table
CREATE TABLE phone
(
    id        BIGSERIAL   NOT NULL PRIMARY KEY,
    number    VARCHAR(20) NOT NULL,
    client_id BIGINT,
    FOREIGN KEY (client_id) REFERENCES client (id) ON DELETE CASCADE
);
