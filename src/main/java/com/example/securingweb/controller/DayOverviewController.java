package com.example.securingweb.controller;

import com.example.securingweb.model.db.DayOverview;
import com.example.securingweb.model.pre.defined.Symptoms;
import com.example.securingweb.repository.DayOverviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Random;

//@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api/day")
@RequiredArgsConstructor
public class DayOverviewController {

    private final DayOverviewRepository repository;

    @PostMapping("/test")
    public ResponseEntity<DayOverview> test() {
        Random r = new Random();

        try {
            DayOverview data = repository.save(new DayOverview(
                    randomSymptom(), LocalDateTime.now().minusHours(r.nextInt(10)), r.nextInt(10000)
            ));
            return new ResponseEntity<>(data, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String randomSymptom() {
        Random r = new Random();
        int temp = r.nextInt() % Symptoms.values().length;
        switch (temp) {
            case 0:
                return Symptoms.BAD_COUGH.name();
            case 1:
                return Symptoms.HEADACHE.name();
            case 2:
                return Symptoms.CHEST_CONGESTION.name();
            case 3:
                return Symptoms.RUNNY_NOSE.name();
            case 4:
            default:
                return Symptoms.ETC.name();
        }
    }

}
