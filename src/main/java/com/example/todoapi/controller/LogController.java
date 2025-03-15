package com.example.todoapi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/logs")
public class LogController {

    private static final String LOG_FILE = "logs/application.log";

    @GetMapping
    public ResponseEntity<List<String>> getLogs(@RequestParam(defaultValue = "100") int lines) {
        try {
            Path logPath = Paths.get(LOG_FILE);
            if (!Files.exists(logPath)) {
                return ResponseEntity.ok(Collections.singletonList("Arquivo de log ainda não existe"));
            }

            List<String> logLines = Files.readAllLines(logPath);
            int startIndex = Math.max(0, logLines.size() - lines);
            return ResponseEntity.ok(logLines.subList(startIndex, logLines.size()));
        } catch (IOException e) {
            return ResponseEntity.ok(Collections.singletonList("Erro ao ler arquivo de log: " + e.getMessage()));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<String>> searchLogs(@RequestParam String term) {
        try {
            Path logPath = Paths.get(LOG_FILE);
            if (!Files.exists(logPath)) {
                return ResponseEntity.ok(Collections.singletonList("Arquivo de log ainda não existe"));
            }

            List<String> matchingLines = Files.lines(logPath)
                    .filter(line -> line.toLowerCase().contains(term.toLowerCase()))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(matchingLines);
        } catch (IOException e) {
            return ResponseEntity.ok(Collections.singletonList("Erro ao ler arquivo de log: " + e.getMessage()));
        }
    }
} 