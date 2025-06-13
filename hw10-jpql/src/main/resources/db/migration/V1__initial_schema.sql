--address
CREATE SEQUENCE address_SEQ START WITH 1 INCREMENT BY 1;

CREATE TABLE address
(
    id     BIGINT NOT NULL PRIMARY KEY,
    street VARCHAR(255) NOT NULL
);


--client
CREATE SEQUENCE client_SEQ START WITH 1 INCREMENT BY 1;

CREATE TABLE client
(
    id            BIGINT NOT NULL PRIMARY KEY,
    name          VARCHAR(50) NOT NULL,
    address_id    BIGINT,
    FOREIGN KEY (address_id) REFERENCES address(id) ON DELETE CASCADE
);


--phone
CREATE SEQUENCE phone_SEQ START WITH 1 INCREMENT BY 1;

CREATE TABLE phone
(
    id        BIGINT NOT NULL PRIMARY KEY,
    number    VARCHAR(20) NOT NULL,
    client_id BIGINT,
    FOREIGN KEY (client_id) REFERENCES client(id) ON DELETE CASCADE
);
