CREATE SEQUENCE persons_id_seq
  minvalue 1
  increment 1;

-- Table: persons i.e John Doe, Jane Doe, etc
CREATE TABLE persons(
    id int NOT NULL DEFAULT nextval('persons_id_seq'),
    name varchar(255) NOT NULL,
    last_name varchar(255) NOT NULL,
    CONSTRAINT pk_persons PRIMARY KEY (id)
);

CREATE SEQUENCE departments_id_seq
  minvalue 1
  increment 1;

-- Table: departments i.e India Office, Brazil Office, etc
CREATE TABLE departments(
    id int NOT NULL DEFAULT nextval('departments_id_seq'),
    name varchar(255) NOT NULL,
    CONSTRAINT pk_departments PRIMARY KEY (id)
);

CREATE SEQUENCE roles_id_seq
  minvalue 1
  increment 1;

-- Table: roles i.e Software Engineer, QA Engineer, etc
CREATE TABLE roles(
    id int NOT NULL DEFAULT nextval('roles_id_seq'),
    name varchar(255) NOT NULL,
    CONSTRAINT pk_roles PRIMARY KEY (id)
);

CREATE SEQUENCE role_groups_id_seq
  minvalue 1
  increment 1;

-- Table: role groups i.e Engineering, DevOps, etc
CREATE TABLE role_groups(
    id int NOT NULL DEFAULT nextval('role_groups_id_seq'),
    name varchar(255) NOT NULL,
    CONSTRAINT pk_role_groups PRIMARY KEY (id)
);

CREATE SEQUENCE work_levels_id_seq
  MINVALUE 1
  INCREMENT 1;

-- Table: work levels i.e Junior, Senior, etc
CREATE TABLE work_levels(
    id int NOT NULL DEFAULT nextval('work_levels_id_seq'),
    name varchar(255) NOT NULL,
    CONSTRAINT pk_work_levels PRIMARY KEY (id)
);

CREATE SEQUENCE employment_types_id_seq
  MINVALUE 1
  INCREMENT 1;

-- Table: employment_types i.e Full Time, Part Time, Contractor, etc
CREATE TABLE employment_types(
    id int NOT NULL DEFAULT nextval('employment_types_id_seq'),
    name varchar(255) NOT NULL,
    CONSTRAINT pk_employment_types PRIMARY KEY (id)
);

CREATE SEQUENCE companies_id_seq
  MINVALUE 1
  INCREMENT 1;

-- Table: companies i.e Microsoft, Google, etc
CREATE TABLE companies(
    id int NOT NULL DEFAULT nextval('companies_id_seq'),
    name varchar(255) NOT NULL,
    CONSTRAINT pk_companies PRIMARY KEY (id)
);

CREATE SEQUENCE locations_id_seq
  MINVALUE 1
  INCREMENT 1;

-- Table: locations i.e Bangalore, Mumbai, São Paulo, etc
CREATE TABLE locations(
    id int NOT NULL DEFAULT nextval('locations_id_seq'),
    name varchar(255) NOT NULL,
    CONSTRAINT pk_locations PRIMARY KEY (id)
);

CREATE SEQUENCE funders_id_seq
  MINVALUE 1
  INCREMENT 1;

-- Table: funders i.e Global, Regional, etc
CREATE TABLE funders(
    id int NOT NULL DEFAULT nextval('funders_id_seq'),
    name varchar(255) NOT NULL,
    CONSTRAINT pk_funders PRIMARY KEY (id)
);

CREATE SEQUENCE teams_id_seq
  MINVALUE 1
  INCREMENT 1;

CREATE TABLE teams(
    id int NOT NULL DEFAULT nextval('teams_id_seq'),
    name varchar(255) NOT NULL,
    manager_id int NOT NULL,
    company_id int NOT NULL,
    CONSTRAINT pk_teams PRIMARY KEY (id),
    CONSTRAINT fk_manager_squad
        FOREIGN KEY (manager_id)
        REFERENCES persons(id)
        ON DELETE RESTRICT
        ON UPDATE SET NULL,
    CONSTRAINT fk_teams_companies
        FOREIGN KEY (company_id)
        REFERENCES companies(id)
        ON DELETE RESTRICT
        ON UPDATE SET NULL
);

CREATE SEQUENCE resources_id_seq
  MINVALUE 1
  INCREMENT 1;

CREATE TABLE resources(
    id int NOT NULL DEFAULT nextval('resources_id_seq'),
    person_id int NOT NULL,
    department_id int NOT NULL,
    role_id int NOT NULL,
    role_group_id int NOT NULL,
    work_level_id int NOT NULL,
    type_id int NOT NULL,
    company_id int NOT NULL,
    join_date date NOT NULL,
    leaver_date date NOT NULL,
    monthly_rate decimal NOT NULL,

    CONSTRAINT pk_resources PRIMARY KEY (id),
    CONSTRAINT fk_persons_resources
        FOREIGN KEY (person_id)
        REFERENCES persons(id)
        ON DELETE RESTRICT
        ON UPDATE SET NULL,
    CONSTRAINT fk_departments_resources
        FOREIGN KEY (department_id)
        REFERENCES departments(id)
        ON DELETE RESTRICT
        ON UPDATE SET NULL,
    CONSTRAINT fk_roles_resources
        FOREIGN KEY (role_id)
        REFERENCES roles(id)
        ON DELETE RESTRICT
        ON UPDATE SET NULL,
    CONSTRAINT fk_role_groups_resources
        FOREIGN KEY (role_group_id)
        REFERENCES role_groups(id)
        ON DELETE RESTRICT
        ON UPDATE SET NULL,
    CONSTRAINT fk_work_levels_resources
        FOREIGN KEY (work_level_id)
        REFERENCES work_levels(id)
        ON DELETE RESTRICT
        ON UPDATE SET NULL,
    CONSTRAINT fk_employment_types_resources
        FOREIGN KEY (type_id)
        REFERENCES employment_types(id)
        ON DELETE RESTRICT
        ON UPDATE SET NULL,
    CONSTRAINT fk_companies_resources
        FOREIGN KEY (company_id)
        REFERENCES companies(id)
        ON DELETE RESTRICT
        ON UPDATE SET NULL
);

create sequence allocations_id_seq
  minvalue 1
  increment 1;

CREATE TABLE allocations(
    id int NOT NULL DEFAULT nextval('allocations_id_seq'),
    resource_id int NOT NULL,
    funder_id int NOT NULL,
    team_id int NOT NULL,
    percentage decimal NOT NULL,
    start_date date NOT NULL,
    end_date date NOT NULL,

    CONSTRAINT pk_allocations PRIMARY KEY (id),
    CONSTRAINT fk_resources_allocations
        FOREIGN KEY (resource_id)
        REFERENCES resources(id)
        ON DELETE RESTRICT
        ON UPDATE SET NULL,
    CONSTRAINT fk_funders_allocations
        FOREIGN KEY (funder_id)
        REFERENCES funders(id)
        ON DELETE RESTRICT
        ON UPDATE SET NULL,
    CONSTRAINT fk_teams_allocations
        FOREIGN KEY (team_id)
        REFERENCES teams(id)
        ON DELETE RESTRICT
        ON UPDATE SET NULL
);