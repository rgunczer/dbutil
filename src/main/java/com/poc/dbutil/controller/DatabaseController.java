package com.poc.dbutil.controller;

import com.poc.dbutil.service.DatabaseService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@RequiredArgsConstructor
@Controller
public class DatabaseController {

    private final DatabaseService databaseService;

    @GetMapping("/")
    public String listDatabases(Model model) {
        model.addAttribute("databases", databaseService.getDatabaseNames());

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

}
