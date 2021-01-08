package ru.veretennikov.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class Heartbeat {

    @Value("${app.info}")
    private String info;

    @GetMapping(value = "/info", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    ResponseEntity<Map<String, String>> ping() {
        Map<String, String> appProperties = Map.of("app info", info);
        return ResponseEntity.ok(appProperties);
    }

}
