# Manage databases on SQL Server
- create new or drop existing
- initialize selected DB with blueprint (SQL)
- read the current db "version" Flyway schema
- insert test users
- provide various "themes" for test data generation
- or generate full random with Faker

Tech stack:
----------
- Java 21
- Spring-Boot 3.4.x
- WebApp with Thymeleaf
- Vanilla JS

.env file
----------
```
DBUTIL_DB_USER_NAME=<db-user>
DBUTIL_DB_PASSWORD=<db-password>
DBUTIL_DB_PATH=<db-path>

DBUTIL_PATH_TO_BLUEPRINT=<full-path-to-blueprint.sql>
DBUTIL_PATH_TO_TEST_USERS=<full-path-to-add-test-users.sql>

DBUTIL_DB_SCHEMA_TABLE_NAME=<schema_table_name>
```
