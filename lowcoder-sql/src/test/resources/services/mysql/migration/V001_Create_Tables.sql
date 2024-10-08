CREATE TABLE persons (
    id int NOT NULL,
    lastname varchar(255) NOT NULL,
    firstname varchar(255),
    age int,
    CONSTRAINT PK_Persons PRIMARY KEY (id)
) ENGINE=INNODB;

CREATE TABLE orders (
    id int NOT NULL,
    number int NOT NULL,
    person_id int,
    person_id2 int,
    CONSTRAINT pk_orders PRIMARY KEY (id),
    CONSTRAINT fk_person_order
        FOREIGN KEY (person_id)
        REFERENCES persons(id)
        ON DELETE RESTRICT
        ON UPDATE SET NULL,
    CONSTRAINT fk_person_order2
        FOREIGN KEY (person_id2)
        REFERENCES persons(id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
) ENGINE=INNODB;

CREATE TABLE auto_generation_id (
    id int NOT NULL AUTO_INCREMENT,
    CONSTRAINT pk_auto_generation_id PRIMARY KEY (id)
) ENGINE=INNODB;