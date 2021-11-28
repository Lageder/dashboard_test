package com.example.securingweb.service;

import com.example.securingweb.model.db.DailyPrescription;
import com.example.securingweb.model.pre.defined.Prescription;
import com.example.securingweb.model.ui.daily.GuestGraph;
import com.example.securingweb.repository.PrescriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PrescriptionService {

    private final PrescriptionRepository repository;

    public List<GuestGraph> getDailyPrescription() {
        List<GuestGraph> ret = new ArrayList<>();
        for (Prescription prescription : Prescription.values()) {
            int sum = 0;
            for (int i = 0; i < 7; i++) {
                LocalDateTime now = LocalDateTime.now().minusDays(i);
                LocalDateTime from = now.truncatedTo(ChronoUnit.DAYS);
                LocalDateTime to = from.plusDays(1);
                sum += Optional.ofNullable(repository.countWithRange(prescription.name(), from, to)).orElse(0);
            }
            ret.add(GuestGraph.builder()
                    .label(prescription.name())
                    .value(String.valueOf(sum))
                    .build());
        }
        return ret;
    }

    public void insert(DailyPrescription prescription) {
        repository.insert(prescription);
    }

}
