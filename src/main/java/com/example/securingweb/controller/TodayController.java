package com.example.securingweb.controller;

import com.example.securingweb.model.db.DailyPrescription;
import com.example.securingweb.model.db.TodayOverview;
import com.example.securingweb.model.pre.defined.Prescription;
import com.example.securingweb.model.pre.defined.Symptoms;
import com.example.securingweb.model.ui.Feedback;
import com.example.securingweb.model.ui.IncomeOverview;
import com.example.securingweb.model.ui.TodayGraph;
import com.example.securingweb.model.ui.TotalIncome;
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
import java.time.temporal.ChronoUnit;
import java.util.*;

//@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api/today")
@RequiredArgsConstructor
public class TodayController {

    private final IncomeRepository incomeRepository;

    private final PrescriptionRepository prescriptionRepository;

    private SimpleDateFormat hourSdf = new SimpleDateFormat("HH:mm");

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    @GetMapping("/income/overview")
    public ResponseEntity<List<IncomeOverview>> getIncome() {
        List<IncomeOverview> list = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            LocalDateTime now = LocalDateTime.now().minusHours(i);
            LocalDateTime from = now.truncatedTo(ChronoUnit.HOURS);
            LocalDateTime to = from.plusHours(1);
//            System.out.println("now : " + sdf.format(Date.from(now.atZone(ZoneId.of("UTC")).toInstant())));
            int sum = 0;
            for (Symptoms symptom : Symptoms.values()) {
                sum += incomeRepository.getIncomeByTimeRange(symptom.name(), from, to);
            }
            list.add(IncomeOverview.builder()
                    .hour(hourSdf.format(Date.from(from.atZone(ZoneId.systemDefault()).toInstant())))
                    .income(sum)
                    .build());
        }
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping("/guest/info")
    public ResponseEntity<List<GuestGraph>> getGuestInfo() {
        List<GuestGraph> ret = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime from = now.truncatedTo(ChronoUnit.HOURS);
        LocalDateTime to = from.plusHours(1);
        for (Symptoms symptom : Symptoms.values()) {
            Integer count = Optional.ofNullable(incomeRepository.countPeople(symptom.name(), from, to))
                    .orElse(0);
            ret.add(GuestGraph.builder()
                    .label(symptom.name())
                    .value(String.valueOf(count))
                    .build());
        }
        return new ResponseEntity<>(ret, HttpStatus.OK);
    }

    @GetMapping("/feedback")
    public ResponseEntity<Feedback> getFeedback() {
        Random r = new Random();
        int total = 0;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime from = now.truncatedTo(ChronoUnit.DAYS);
        LocalDateTime to = from.plusDays(1);
        for (Symptoms symptom : Symptoms.values()) {
            total += Optional.ofNullable(incomeRepository.countPeople(symptom.name(), from, to)).orElse(0);
        }
        double negative = r.nextInt(total / 10);
        double positive = total - negative;
        Feedback ret = Feedback.builder()
                .positive(positive / total)
                .negative(negative / total)
                .progress(positive / total)
                .total(total)
                .build();
        return new ResponseEntity<>(ret, HttpStatus.OK);
    }

    @GetMapping("/total/income")
    public ResponseEntity<TotalIncome> getTotalIncome() {
        int value = 0;
        int people = 0;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime from = now.truncatedTo(ChronoUnit.DAYS);
        LocalDateTime to = from.plusDays(1);
        for (Symptoms symptom : Symptoms.values()) {
            value += Optional.ofNullable(incomeRepository.getIncomeByTimeRange(symptom.name(), from, to)).orElse(0);
            people += Optional.ofNullable(incomeRepository.countPeople(symptom.name(), from, to)).orElse(0);
        }
        TotalIncome ret = TotalIncome.builder()
                .value(value)
                .progress(1.0)
                .total(people)
                .build();
        return new ResponseEntity<>(ret, HttpStatus.OK);
    }

    @PostMapping("/test/trigger")
    public ResponseEntity<Void> trigger() {
        try {
            Random r = new Random();
            for (int i = 0; i < 100000; i++) {
                Symptoms symptom = randomSymptom();
                Prescription prescription = randomPrescription(symptom);
                LocalDateTime time = LocalDateTime.now()
                        .minusHours(r.nextInt(23))
                        .plusDays(r.nextInt(14));
                TodayOverview temp = new TodayOverview(symptom.name(), time, r.nextInt(200));
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
