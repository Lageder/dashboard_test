package com.example.securingweb.controller;

import com.example.securingweb.model.pre.defined.Symptoms;
import com.example.securingweb.repository.IncomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/daily")
@RequiredArgsConstructor
public class DailyController {

    private final IncomeRepository repository;

    @GetMapping("/guest/overview")
    public ResponseEntity<String> getGuestInfo() {
        Map<String, Integer> map = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime from = now.truncatedTo(ChronoUnit.DAYS);
        LocalDateTime to = from.plusDays(1);
        for (Symptoms symptom : Symptoms.values()) {
            // find the number of customer
            map.put(symptom.name(), Optional.ofNullable(repository.countPeople(symptom.name(),from, to)).orElse(0));
        }
        // todo : Add json parsing
        // ***
        String ret = "";
        return new ResponseEntity<>(ret, HttpStatus.OK);
    }

    @GetMapping("/income/overview")
    public ResponseEntity<String> getIncomeOverview() {
        // todo : Add json parsing
        // ***
        String ret = "";
        return new ResponseEntity<>(ret, HttpStatus.OK);
    }

    @GetMapping("/top5/symptoms")
    public ResponseEntity<String> getTop5Symptoms() {
        // todo : Add json parsing
        // ***
        String ret = "";
        return new ResponseEntity<>(ret, HttpStatus.OK);
    }

    @GetMapping("/prescription")
    public ResponseEntity<String> getDailyPrescription() {
        // todo : Add json parsing
        // ***
        String ret = "";
        return new ResponseEntity<>(ret, HttpStatus.OK);
    }
}
