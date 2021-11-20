package com.example.securingweb.controller;

import com.example.securingweb.model.pre.defined.Prescription;
import com.example.securingweb.model.pre.defined.Symptoms;
import com.example.securingweb.model.ui.daily.GuestGraph;
import com.example.securingweb.repository.IncomeRepository;
import com.example.securingweb.repository.PrescriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

@RestController
@RequestMapping("/api/daily")
@RequiredArgsConstructor
public class DailyController {

    private final IncomeRepository incomeRepository;

    private final PrescriptionRepository prescriptionRepository;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    @GetMapping("/guest/overview")
    public ResponseEntity<List<GuestGraph>> getGuestInfo() {
        List<GuestGraph> ret = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            LocalDateTime now = LocalDateTime.now().minusDays(i);
            LocalDateTime from = now.truncatedTo(ChronoUnit.DAYS);
            LocalDateTime to = from.plusDays(1);
            int sum = 0;
            for (Symptoms symptom : Symptoms.values()) {
                sum += Optional.ofNullable(incomeRepository.countPeople(symptom.name(), from, to)).orElse(0);
            }
            ret.add(GuestGraph.builder()
                    .label(sdf.format(Date.from(from.atZone(ZoneId.systemDefault()).toInstant())))
                    .value(String.valueOf(sum))
                    .build());
        }
        return new ResponseEntity<>(ret, HttpStatus.OK);
    }

    @GetMapping("/income/overview")
    public ResponseEntity<List<GuestGraph>> getIncomeOverview() {
        List<GuestGraph> ret = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            LocalDateTime now = LocalDateTime.now().minusDays(i);
            LocalDateTime from = now.truncatedTo(ChronoUnit.DAYS);
            LocalDateTime to = from.plusDays(1);
            int sum = 0;
            for (Symptoms symptom : Symptoms.values()) {
                sum += Optional.ofNullable(incomeRepository.getIncomeByTimeRange(symptom.name(), from, to)).orElse(0);
            }
            ret.add(GuestGraph.builder()
                    .label(sdf.format(Date.from(from.atZone(ZoneId.systemDefault()).toInstant())))
                    .value(String.valueOf(sum))
                    .build());
        }
        return new ResponseEntity<>(ret, HttpStatus.OK);
    }

    @GetMapping("/top5/symptoms")
    public ResponseEntity<List<GuestGraph>> getTop5Symptoms() {
        List<GuestGraph> ret = new ArrayList<>();
        for (Symptoms symptom : Symptoms.values()) {
            int sum = 0;
            for (int i = 0; i < 7; i++) {
                LocalDateTime now = LocalDateTime.now().minusDays(i);
                LocalDateTime from = now.truncatedTo(ChronoUnit.DAYS);
                LocalDateTime to = from.plusDays(1);
                sum += Optional.ofNullable(incomeRepository.countPeople(symptom.name(), from, to)).orElse(0);
            }
            ret.add(GuestGraph.builder()
                    .label(symptom.name())
                    .value(String.valueOf(sum))
                    .build());
        }
        return new ResponseEntity<>(ret, HttpStatus.OK);
    }

    @GetMapping("/prescription")
    public ResponseEntity<List<GuestGraph>> getDailyPrescription() {
        List<GuestGraph> ret = new ArrayList<>();
        for (Prescription prescription : Prescription.values()) {
            int sum = 0;
            for (int i = 0; i < 7; i++) {
                LocalDateTime now = LocalDateTime.now().minusDays(i);
                LocalDateTime from = now.truncatedTo(ChronoUnit.DAYS);
                LocalDateTime to = from.plusDays(1);
                sum += Optional.ofNullable(prescriptionRepository.countWithRange(prescription.name(), from, to)).orElse(0);
            }
            ret.add(GuestGraph.builder()
                    .label(prescription.name())
                    .value(String.valueOf(sum))
                    .build());
        }
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
