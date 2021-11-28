package com.example.securingweb.controller;

import com.example.securingweb.model.db.DailyPrescription;
import com.example.securingweb.model.db.TodayOverview;
import com.example.securingweb.model.pre.defined.Prescription;
import com.example.securingweb.model.pre.defined.Symptoms;
import com.example.securingweb.model.ui.Feedback;
import com.example.securingweb.model.ui.IncomeOverview;
import com.example.securingweb.model.ui.TotalIncome;
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

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/api/today")
@RequiredArgsConstructor
public class TodayController {

    private final IncomeService incomeService;

    private final PrescriptionService prescriptionService;

    @GetMapping("/income/overview")
    public ResponseEntity<List<IncomeOverview>> getIncome() {
        try {
            List<IncomeOverview> list = incomeService.getTodayIncome();
            return new ResponseEntity<>(list, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/guest/info")
    public ResponseEntity<List<GuestGraph>> getGuestInfo() {
        try {
            List<GuestGraph> ret = incomeService.getTodayGuestInfo();
            return new ResponseEntity<>(ret, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/feedback")
    public ResponseEntity<Feedback> getFeedback() {
        try {
            Feedback ret = incomeService.getFeedback();
            return new ResponseEntity<>(ret, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/total/income")
    public ResponseEntity<TotalIncome> getTotalIncome() {
        try {
            TotalIncome ret = incomeService.getTodayTotalIncome();
            return new ResponseEntity<>(ret, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
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
                incomeService.insert(temp);
                prescriptionService.insert(dailyPrescription);
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
