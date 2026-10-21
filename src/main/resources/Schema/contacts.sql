-- Contacts owned by an account. owner stores that account's username.
CREATE TABLE IF NOT EXISTS contacts (
    id INT NOT NULL AUTO_INCREMENT,
    firstname VARCHAR(100) NOT NULL,
    lastname VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL,
    owner VARCHAR(100) NOT NULL,
    PRIMARY KEY (id),
    KEY idx_contacts_owner (owner),
    CONSTRAINT fk_contacts_owner
        FOREIGN KEY (owner) REFERENCES `user` (username)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
