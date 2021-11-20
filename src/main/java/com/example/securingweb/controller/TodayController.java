package com.example.securingweb.controller;

import com.example.securingweb.model.db.DailyPrescription;
import com.example.securingweb.model.db.TodayOverview;
import com.example.securingweb.model.pre.defined.Prescription;
import com.example.securingweb.model.pre.defined.Symptoms;
import com.example.securingweb.model.ui.TodayGraph;
import com.example.securingweb.repository.IncomeRepository;
import com.example.securingweb.repository.PrescriptionRepository;
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

    private final IncomeRepository incomeRepository;

    private final PrescriptionRepository prescriptionRepository;

    @GetMapping("/income/overview")
    public ResponseEntity<List<TodayGraph>> getIncome() {
        LocalDateTime from = LocalDateTime.of(2021,11,12,2,0,0);
        LocalDateTime to = LocalDateTime.of(2021,11,12,3,0,0);
        Integer etcFee = Optional.ofNullable(incomeRepository.getIncomeByTimeRange(Symptoms.ETC.name(), from, to))
                .orElse(Integer.parseInt("0"));
        Integer etcFee2 = Optional.ofNullable(incomeRepository.getIncomeByTimeRange(Symptoms.ETC.name(), from.minusDays(1), to.minusDays(1)))
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
            map.put(symptom.name(), Optional.ofNullable(incomeRepository.countPeople(symptom.name(), from, to)).orElse(0));
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
            total += Optional.ofNullable(incomeRepository.getIncomeByTimeRange(symptom.name(), from, to)).orElse(0);
        }
        // todo : Add json parsing
        String ret = "";
        return new ResponseEntity<>(ret, HttpStatus.OK);
    }

    @PostMapping("/test/trigger")
    public ResponseEntity<Void> trigger() {
        try {
            Random r = new Random();
            for (int i = 0; i < 1000; i++) {
                Symptoms symptom = randomSymptom();
                Prescription prescription = randomPrescription(symptom);
                LocalDateTime time = LocalDateTime.now().minusDays(r.nextInt(14));
                TodayOverview temp = new TodayOverview(symptom.name(), time, r.nextInt(5000));
                DailyPrescription dailyPrescription = new DailyPrescription(prescription.name(), time);
                incomeRepository.insert(temp);
                prescriptionRepository.insert(dailyPrescription);
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private Symptoms randomSymptom() {
        Random r = new Random();
        int temp = r.nextInt(5000) % Symptoms.values().length;
        switch (temp) {
            case 0:
                return Symptoms.BAD_COUGH;
            case 1:
                return Symptoms.HEADACHE;
            case 2:
                return Symptoms.CHEST_CONGESTION;
            case 3:
                return Symptoms.RUNNY_NOSE;
            case 4:
            default:
                return Symptoms.ETC;
        }
    }

    private Prescription randomPrescription(Symptoms symptoms) {
        Random r = new Random();
        int temp = r.nextInt();
        switch (symptoms) {
            case HEADACHE:
                temp %= 2;
                return (temp == 0) ? Prescription.ACETAMINOPHEN : Prescription.IBUPROFEN;
            case RUNNY_NOSE:
                temp %= 2;
                return (temp == 0) ? Prescription.CHLORPHENAMINE : Prescription.CARBINOXAMINE;
            case BAD_COUGH:
                temp %= 2;
                return (temp == 0) ? Prescription.DEXTROMETHORPHAN : Prescription.NOSCARPINE;
            case CHEST_CONGESTION:
                temp %= 2;
                return (temp == 0) ? Prescription.GINKGOLIDES : Prescription.TERPENE_LACTONES;
            case ETC:
            default:
                return Prescription.GUAIFENESIN;
        }
    }

}
