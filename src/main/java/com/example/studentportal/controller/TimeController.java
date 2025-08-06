package com.example.studentportal.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
public class TimeController {

    @GetMapping("/api/time")
    public Map<String, String> getTime() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Manila"));
        String time = now.format(DateTimeFormatter.ofPattern("HH:mm:ss"));

        Map<String, String> response = new HashMap<>();
        response.put("time", time);
        return response;
    }
}
