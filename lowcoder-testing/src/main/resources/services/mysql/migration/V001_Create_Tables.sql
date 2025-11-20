CREATE TABLE persons (
    id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE departments (
    id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE roles (
    id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE role_groups (
    id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE work_levels (
    id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE employment_types (
    id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE companies (
    id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE locations (
    id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE funders (
    id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE teams (
    id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    manager_id INT NULL,
    company_id INT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_manager_squad
        FOREIGN KEY (manager_id) REFERENCES persons(id)
        ON DELETE RESTRICT ON UPDATE SET NULL,
    CONSTRAINT fk_teams_companies
        FOREIGN KEY (company_id) REFERENCES companies(id)
        ON DELETE RESTRICT ON UPDATE SET NULL
);

CREATE TABLE resources (
    id INT NOT NULL AUTO_INCREMENT,
    person_id INT NULL,
    department_id INT NULL,
    role_id INT NULL,
    role_group_id INT NULL,
    work_level_id INT NULL,
    type_id INT NULL,
    company_id INT NULL,

    join_date DATE NOT NULL,
    leaver_date DATE NOT NULL,
    monthly_rate DECIMAL(10,2) NOT NULL,

    PRIMARY KEY (id),
    CONSTRAINT fk_persons_resources
        FOREIGN KEY (person_id) REFERENCES persons(id)
        ON DELETE RESTRICT ON UPDATE SET NULL,
    CONSTRAINT fk_departments_resources
        FOREIGN KEY (department_id) REFERENCES departments(id)
        ON DELETE RESTRICT ON UPDATE SET NULL,
    CONSTRAINT fk_roles_resources
        FOREIGN KEY (role_id) REFERENCES roles(id)
        ON DELETE RESTRICT ON UPDATE SET NULL,
    CONSTRAINT fk_role_groups_resources
        FOREIGN KEY (role_group_id) REFERENCES role_groups(id)
        ON DELETE RESTRICT ON UPDATE SET NULL,
    CONSTRAINT fk_work_levels_resources
        FOREIGN KEY (work_level_id) REFERENCES work_levels(id)
        ON DELETE RESTRICT ON UPDATE SET NULL,
    CONSTRAINT fk_employment_types_resources
        FOREIGN KEY (type_id) REFERENCES employment_types(id)
        ON DELETE RESTRICT ON UPDATE SET NULL,
    CONSTRAINT fk_companies_resources
        FOREIGN KEY (company_id) REFERENCES companies(id)
        ON DELETE RESTRICT ON UPDATE SET NULL
);

CREATE TABLE allocations (
    id INT NOT NULL AUTO_INCREMENT,
    resource_id INT NULL,
    funder_id INT NULL,
    team_id INT NULL,
    percentage DECIMAL(10,2) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,

    PRIMARY KEY (id),
    CONSTRAINT fk_resources_allocations
        FOREIGN KEY (resource_id) REFERENCES resources(id)
        ON DELETE RESTRICT ON UPDATE SET NULL,
    CONSTRAINT fk_funders_allocations
        FOREIGN KEY (funder_id) REFERENCES funders(id)
        ON DELETE RESTRICT ON UPDATE SET NULL,
    CONSTRAINT fk_teams_allocations
        FOREIGN KEY (team_id) REFERENCES teams(id)
        ON DELETE RESTRICT ON UPDATE SET NULL
);
