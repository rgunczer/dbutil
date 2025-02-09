package com.poc.dbutil.controller;

import com.poc.dbutil.service.DatabaseService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@RequiredArgsConstructor
@Controller
public class DatabaseController {

    private final DatabaseService databaseService;

    @GetMapping("/")
    public String listDatabases(Model model) {
        final var databases = databaseService.getDatabaseNames();
        databases.add(0, ""); // make the rendered UI display an empty default value in a dropdown

        model.addAttribute("databases", databases);
        model.addAttribute("isBlueprintScriptReady", databaseService.isBlueprintScriptPathReady());
        model.addAttribute("isTestUsersScriptReady", databaseService.isTestUsersScriptPathReady());

        return "databases";
    }

    @PostMapping("/drop")
    public String dropDatabase(
        @RequestParam final String dbName,
        final Model model
    ) {
        final var result = databaseService.dropDatabase(dbName);
        model.addAttribute("result", result);

        return "result";
    }

    @PostMapping("/create-init")
    public String createAndInitialize(
        @RequestParam final String dbName,
        final Model model
    ) {
        final var result = databaseService.createNewDatabaseAndInitialize(dbName);
        model.addAttribute("result", result);

        return "result";
    }

    @GetMapping(value = "/flyway-schemas", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<String> getFlywaySchemas(
        @RequestParam String database
    ) {
        log.trace("getFlywaySchemas for [" + database + "]");
        return databaseService.getListOfMigrationsFrom(database);
    }

}
