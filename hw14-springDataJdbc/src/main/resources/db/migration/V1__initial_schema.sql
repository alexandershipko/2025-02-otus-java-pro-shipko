-- Client table
CREATE TABLE client
(
    id   BIGSERIAL   NOT NULL PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

-- Address table
CREATE TABLE address
(
    id        BIGSERIAL    NOT NULL PRIMARY KEY,
    street    VARCHAR(255) NOT NULL,
    client_id BIGINT NOT NULL,

    FOREIGN KEY (client_id) REFERENCES client (id)
);

-- Phone table
CREATE TABLE phone
(
    id        BIGSERIAL   NOT NULL PRIMARY KEY,
    number    VARCHAR(20) NOT NULL,
    client_id BIGINT NOT NULL,

    FOREIGN KEY (client_id) REFERENCES client (id)
);
