--system_user
CREATE SEQUENCE system_user_SEQ START WITH 1 INCREMENT BY 1;

CREATE TABLE system_user
(
    id            BIGINT       NOT NULL PRIMARY KEY,
    user_name     VARCHAR(255) NOT NULL,
    user_password VARCHAR(255) NOT NULL,
    user_role     VARCHAR(255) NOT NULL,

    CONSTRAINT system_user_user_name_uk UNIQUE (user_name)
);


-- INSERT INTO system_user (id, user_name, user_password, user_role)
-- VALUES (nextval('system_user_SEQ'), 'admin2', 'password2', 'admin');