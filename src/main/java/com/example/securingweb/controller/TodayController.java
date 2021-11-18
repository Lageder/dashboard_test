package com.example.securingweb.controller;

import com.example.securingweb.model.db.TodayOverview;
import com.example.securingweb.model.pre.defined.Symptoms;
import com.example.securingweb.model.ui.TodayGraph;
import com.example.securingweb.repository.IncomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

//@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api/today")
@RequiredArgsConstructor
public class TodayController {

    private final IncomeRepository repository;

    @GetMapping("/income/overview")
    public ResponseEntity<List<TodayGraph>> getIncome() {
        LocalDateTime from = LocalDateTime.of(2021,11,12,2,0,0);
        LocalDateTime to = LocalDateTime.of(2021,11,12,3,0,0);
        Integer etcFee = Optional.ofNullable(repository.getIncomeByTimeRange(Symptoms.ETC.name(), from, to))
                .orElse(Integer.parseInt("0"));
        Integer etcFee2 = Optional.ofNullable(repository.getIncomeByTimeRange(Symptoms.ETC.name(), from.minusDays(1), to.minusDays(1)))
                .orElse(Integer.parseInt("0"));
        List<TodayGraph> list = new ArrayList<>();
        list.add(new TodayGraph("etc", etcFee, etcFee2));
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping("/guest/info")
    public ResponseEntity<String> getGuestInfo() {
        Map<String, Integer> map = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime from = now.truncatedTo(ChronoUnit.DAYS);
        LocalDateTime to = from.plusDays(1);
        for (Symptoms symptom : Symptoms.values()) {
            map.put(symptom.name(), Optional.ofNullable(repository.countPeople(symptom.name(), from, to)).orElse(0));
        }
        // todo : Add json parsing
        // ***
        String ret = "";
        return new ResponseEntity<>(ret, HttpStatus.OK);
    }

    @GetMapping("/feedback")
    public ResponseEntity<String> getFeedback() {
        Random r = new Random();
        int total = 16000;
        int pos = 10000 + r.nextInt(5000);
        // todo : Add json parsing
        String ret = "";
        return new ResponseEntity<>(ret, HttpStatus.OK);
    }

    @GetMapping("/total/income")
    public ResponseEntity<String> getTotalIncome() {
        int total = 0;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime from = now.truncatedTo(ChronoUnit.DAYS);
        LocalDateTime to = from.plusDays(1);
        for (Symptoms symptom : Symptoms.values()) {
            total += Optional.ofNullable(repository.getIncomeByTimeRange(symptom.name(), from, to)).orElse(0);
        }
        // todo : Add json parsing
        String ret = "";
        return new ResponseEntity<>(ret, HttpStatus.OK);
    }

    @PostMapping("/test")
    public ResponseEntity<TodayOverview> test() {
        Random r = new Random();
        try {
            TodayOverview data = repository.insert(new TodayOverview(
                    randomSymptom(), LocalDateTime.now().minusHours(r.nextInt(10)), r.nextInt(10000)
            ));
            return new ResponseEntity<>(data, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/test/trigger")
    public ResponseEntity<Void> trigger() {
        try {
            Random r = new Random();
            for (int i = 0; i < 10000; i++) {
                TodayOverview temp = new TodayOverview(randomSymptom(), LocalDateTime.now().minusDays(r.nextInt(14)), r.nextInt(5000));
                repository.insert(temp);
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
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
