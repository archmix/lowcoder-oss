CREATE SEQUENCE person_id_seq
  minvalue 1
  increment 1;

CREATE TABLE persons(
    id int NOT NULL DEFAULT nextval('person_id_seq'),
    name varchar(255) NOT NULL,
    last_name varchar(255) NOT NULL,
    CONSTRAINT pk_persons PRIMARY KEY (id)
);

CREATE SEQUENCE department_id_seq
  minvalue 1
  increment 1;

CREATE TABLE departments(
    id int NOT NULL DEFAULT nextval('department_id_seq'),
    name varchar(255) NOT NULL,
    CONSTRAINT pk_departments PRIMARY KEY (id)
);

CREATE SEQUENCE role_id_seq
  minvalue 1
  increment 1;

CREATE TABLE roles(
    id int NOT NULL DEFAULT nextval('role_id_seq'),
    name varchar(255) NOT NULL,
    CONSTRAINT pk_roles PRIMARY KEY (id)
);

CREATE SEQUENCE role_group_id_seq
  minvalue 1
  increment 1;

CREATE TABLE role_groups(
    id int NOT NULL DEFAULT nextval('role_group_id_seq'),
    name varchar(255) NOT NULL,
    CONSTRAINT pk_role_groups PRIMARY KEY (id)
);

CREATE SEQUENCE work_level_id_seq
  MINVALUE 1
  INCREMENT 1;

CREATE TABLE work_levels(
    id int NOT NULL DEFAULT nextval('work_level_id_seq'),
    name varchar(255) NOT NULL,
    CONSTRAINT pk_work_levels PRIMARY KEY (id)
);

CREATE SEQUENCE type_id_seq
  MINVALUE 1
  INCREMENT 1;

CREATE TABLE types(
    id int NOT NULL DEFAULT nextval('type_id_seq'),
    name varchar(255) NOT NULL,
    CONSTRAINT pk_types PRIMARY KEY (id)
);

CREATE SEQUENCE company_id_seq
  MINVALUE 1
  INCREMENT 1;

CREATE TABLE companies(
    id int NOT NULL DEFAULT nextval('company_id_seq'),
    name varchar(255) NOT NULL,
    CONSTRAINT pk_companies PRIMARY KEY (id)
);

CREATE SEQUENCE locations_id_seq
  MINVALUE 1
  INCREMENT 1;

CREATE TABLE locations(
    id int NOT NULL DEFAULT nextval('locations_id_seq'),
    name varchar(255) NOT NULL,
    CONSTRAINT pk_locations PRIMARY KEY (id)
);

CREATE SEQUENCE funder_id_seq
  MINVALUE 1
  INCREMENT 1;

CREATE TABLE funders(
    id int NOT NULL DEFAULT nextval('funder_id_seq'),
    name varchar(255) NOT NULL,
    CONSTRAINT pk_funders PRIMARY KEY (id)
);

CREATE SEQUENCE organization_id_seq
  MINVALUE 1
  INCREMENT 1;

CREATE TABLE organizations(
    id int NOT NULL DEFAULT nextval('organization_id_seq'),
    name varchar(255) NOT NULL,
    CONSTRAINT pk_organizations PRIMARY KEY (id)
);

CREATE SEQUENCE team_id_seq
  MINVALUE 1
  INCREMENT 1;

CREATE TABLE teams(
    id int NOT NULL DEFAULT nextval('team_id_seq'),
    name varchar(255) NOT NULL,
    manager_id int NOT NULL,
    organization_id int NOT NULL,
    CONSTRAINT pk_teams PRIMARY KEY (id),
    CONSTRAINT fk_manager_squad
        FOREIGN KEY (manager_id)
        REFERENCES persons(id)
        ON DELETE RESTRICT
        ON UPDATE SET NULL,
    CONSTRAINT fk_teams_organizations
        FOREIGN KEY (organization_id)
        REFERENCES organizations(id)
        ON DELETE RESTRICT
        ON UPDATE SET NULL
);

CREATE SEQUENCE resource_id_seq
  MINVALUE 1
  INCREMENT 1;

CREATE TABLE resources(
    id int NOT NULL DEFAULT nextval('resource_id_seq'),
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
    CONSTRAINT fk_types_resources
        FOREIGN KEY (type_id)
        REFERENCES types(id)
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