# How to contribute with Lowcoder

1. Fork this repository
2. Import InteliJ [code style](https://raw.githubusercontent.com/archmix/community/master/intellij_code_style.xml)
3. Open your Pull Request

Your name will be published in this repository as a contributor. All kind of contribution is welcomed. If you need assistance, please join us on [slack](https://archmix.org/slack)

# User Guide
You can find documentation [here](https://docs.archmix.org) 

# License
https://github.com/archmix/community/blob/master/LICENSE.md

# CODE OF CONDUCT
https://github.com/archmix/community/blob/master/CODE_OF_CONDUCT.md

# WHAT LOWCODER IS?
Lowcoder is a set of lowcode functionalities that reads the database schema (reverse engineering) and publish an api based on it. In addition, it provides a set of interfaces as plugins so that it is possible to customize data processing.

##It is a modular solution
Because it is a modular solution, you can use only the modules you want to have in the final product. The entire solution uses annotation processing to discover which module is available and you even can create your own, just follow the specs (todo).

1. The [#lowcoder-graphql](tree/main/lowcoder-graphql) module publishes a graphql api based on database schema.
2. The [#lowcoder-openapi](tree/main/lowcoder-openapi) module publishes an open api based on database schema.
3. The [#lowcoder-core]((tree/main/lowcoder-core)) module is the core solution based on vert.x http service.
4. The [#lowcoder-vertx]((tree/main/lowcoder-vertx)) module is the low level implementation for vert.x and utilities.
5. The [#lowcoder-sql]((tree/main/lowcoder-vertx)) module is the low level sql implementation for database schema metadata processing and SQL DML instructions generation. 
6. The [#lowcoder-container]((tree/main/lowcoder-container)) is the application that you need to deploy in order to have the entire solution up and running.

#What is the MVA?
The minimal viable architecture will consist in:

1. When application starts, a full metadata scan on database will be done.
2. It will be generated an api for each scanned table.
3. It will be possible to create custom data processing based on plug-ins specifications.
4. The reactive programming model must be used. 