package com.poc.dbutil.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.poc.dbutil.config.CharacterConfig;

import jakarta.annotation.PostConstruct;

import java.io.IOException;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.sql.Statement;

import java.util.List;

import javax.sql.DataSource;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;


@Slf4j
@RequiredArgsConstructor
@Service
public class DatabaseService {

    private final JdbcTemplate jdbcTemplate;
    private final DataSource dataSource;

    @Value("${script.path.sql_db_blueprint}")
    private String blueprintScriptPath;

    @Getter
    private boolean blueprintScriptPathReady;

    @Value("${script.path.sql_test_users}")
    private String testUsersScriptPath;

    @Getter
    private boolean testUsersScriptPathReady;

    private CharacterConfig characterConfig;


    public List<String> getDatabaseNames() {
        // database_id > 4 = skip system databases
        return jdbcTemplate.queryForList("SELECT name FROM sys.databases WHERE database_id > 4", String.class);
    }

    public String dropDatabase(
        final String dbName
    ) {
        final var useMaster = "USE MASTER";
        final var setSingleUser = "ALTER DATABASE [" + dbName + "] SET SINGLE_USER WITH ROLLBACK IMMEDIATE";
        final var dropDatabase = "DROP DATABASE [" + dbName + "]";

        try (final var connection = dataSource.getConnection()) {
            final var statement = connection.createStatement();

            statement.execute(useMaster);
            statement.execute(setSingleUser);
            statement.execute(dropDatabase);

            return "Database [" + dbName + "] dropped successfully";
        } catch (Exception ex) {
            return "Error dropping database: " + ex.getMessage();
        }
    }

    public String createNewDatabaseAndInitialize(
        final String newDatabaseName
    ) {
        log.info("begin");
        final var createdDatabaseSql = "CREATE DATABASE " + newDatabaseName;

        try (final var connection = dataSource.getConnection()) {
            final var statement = connection.createStatement();

            log.info("create database");
            statement.execute(createdDatabaseSql);

            log.info("use database");
            final var useDatabaseSql = "USE " + newDatabaseName;
            statement.execute(useDatabaseSql);

            // blueprint
            final var utf16le = StandardCharsets.UTF_16LE;
            executeScript(statement, blueprintScriptPath, utf16le);

            // test-users
            final var utf8 = StandardCharsets.UTF_8;
            executeScript(statement, testUsersScriptPath, utf8);

            log.info("end");
            return "Database created [" + newDatabaseName + "] and initialized successfully";

        } catch (Exception ex) {
             return "Error creating database and/or initializing: " + ex.getMessage();
        }
    }

    private void executeScript(
        final Statement statement,
        final String pathToSqlScript,
        final Charset charset
    ) throws Exception {
        final var path = Paths.get(pathToSqlScript);

        log.info("readString");
        var script = Files.readString(path, charset);

        log.info("remove BOM if present");
        if (script.startsWith("\uFEFF")) {
            script = script.substring(1);
        }

        log.info("execute init");

        // split the script into batches and execute each batch
        final var batches = script.split("(?i)\\bGO\\b");
        for (final var batch : batches) {
            if (!batch.trim().isEmpty()) {
                statement.execute(batch);
            }
        }
    }

    private boolean isPathToScriptValid(
        final String humanReadableName,
        final String pathToScript
    ) {
        final var path = Paths.get(pathToScript);

        if (Files.exists(path) && Files.isRegularFile(path)) {
            return true;
        }

        log.warn(
            "Script [" + humanReadableName + "] at path [" + pathToScript + "] does NOT exists"
        );

        return false;
    }

    @PostConstruct
    public void init() throws IOException {
        final var objectMapper = new ObjectMapper();
        characterConfig = objectMapper.readValue(
            new ClassPathResource("static/characters.json").getFile(),
            CharacterConfig.class
        );

        blueprintScriptPathReady = isPathToScriptValid("Blueprint", blueprintScriptPath);
        testUsersScriptPathReady = isPathToScriptValid("Test Users", testUsersScriptPath);
    }

}
