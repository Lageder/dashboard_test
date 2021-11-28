package com.example.securingweb.controller;

import com.example.securingweb.model.ui.daily.GuestGraph;
import com.example.securingweb.service.IncomeService;
import com.example.securingweb.service.PrescriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/api/daily")
@RequiredArgsConstructor
public class DailyController {

    private final IncomeService incomeService;

    private final PrescriptionService prescriptionService;

    @GetMapping("/guest/overview")
    public ResponseEntity<List<GuestGraph>> getGuestInfo() {
        try {
            List<GuestGraph> ret = incomeService.getGuestInfo();
            return new ResponseEntity<>(ret, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/income/overview")
    public ResponseEntity<List<GuestGraph>> getIncomeOverview() {
        try {
            List<GuestGraph> ret = incomeService.getIncomeOverview();
            return new ResponseEntity<>(ret, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/top5/symptoms")
    public ResponseEntity<List<GuestGraph>> getTop5Symptoms() {
        try {
            List<GuestGraph> ret = incomeService.getTop5Symptoms();
            return new ResponseEntity<>(ret, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/prescription")
    public ResponseEntity<List<GuestGraph>> getDailyPrescription() {
        try {
            List<GuestGraph> ret = prescriptionService.getDailyPrescription();
            return new ResponseEntity<>(ret, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
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
