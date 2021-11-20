package com.example.securingweb.controller;

import com.example.securingweb.model.pre.defined.Symptoms;
import com.example.securingweb.model.ui.daily.GuestGraph;
import com.example.securingweb.repository.IncomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

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


    @PostMapping("/test/trigger")
    public ResponseEntity<List<GuestGraph>> trigger() {
        try {
            Random r = new Random();
            List<GuestGraph> guestGraphs = new ArrayList<>();
            for (int i = 0; i < 1000; i++) {
                Integer value = r.nextInt(1000);
                ZonedDateTime date = ZonedDateTime.now().minusDays(value);
                guestGraphs.add(GuestGraph.builder()
                    .label(date.format(DateTimeFormatter.ofPattern("YYYY-MM-dd'T'HH:mm:ss.SSSXXX")))
                    .value(value.toString())
                    .build()
                );
            }
            return ResponseEntity.ok(guestGraphs);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
