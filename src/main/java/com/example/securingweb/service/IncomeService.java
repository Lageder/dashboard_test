package com.example.securingweb.service;

import com.example.securingweb.model.db.TodayOverview;
import com.example.securingweb.model.pre.defined.Symptoms;
import com.example.securingweb.model.ui.Feedback;
import com.example.securingweb.model.ui.IncomeOverview;
import com.example.securingweb.model.ui.TotalIncome;
import com.example.securingweb.model.ui.daily.GuestGraph;
import com.example.securingweb.repository.IncomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
public class IncomeService {

    private final IncomeRepository repository;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    private SimpleDateFormat hourSdf = new SimpleDateFormat("HH:mm");

    public List<GuestGraph> getGuestInfo() {
        List<GuestGraph> ret = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            LocalDateTime now = LocalDateTime.now().minusDays(i);
            LocalDateTime from = now.truncatedTo(ChronoUnit.DAYS);
            LocalDateTime to = from.plusDays(1);
            int sum = 0;
            for (Symptoms symptom : Symptoms.values()) {
                sum += Optional.ofNullable(repository.countPeople(symptom.name(), from, to)).orElse(0);
            }
            ret.add(GuestGraph.builder()
                    .label(sdf.format(Date.from(from.atZone(ZoneId.systemDefault()).toInstant())))
                    .value(String.valueOf(sum))
                    .build());
        }
        return ret;
    }

    public List<GuestGraph> getIncomeOverview() {
        List<GuestGraph> ret = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            LocalDateTime now = LocalDateTime.now().minusDays(i);
            LocalDateTime from = now.truncatedTo(ChronoUnit.DAYS);
            LocalDateTime to = from.plusDays(1);
            int sum = 0;
            for (Symptoms symptom : Symptoms.values()) {
                sum += Optional.ofNullable(repository.getIncomeByTimeRange(symptom.name(), from, to)).orElse(0);
            }
            ret.add(GuestGraph.builder()
                    .label(sdf.format(Date.from(from.atZone(ZoneId.systemDefault()).toInstant())))
                    .value(String.valueOf(sum))
                    .build());
        }
        return ret;
    }

    public List<GuestGraph> getTop5Symptoms() {
        List<GuestGraph> ret = new ArrayList<>();
        for (Symptoms symptom : Symptoms.values()) {
            int sum = 0;
            for (int i = 0; i < 7; i++) {
                LocalDateTime now = LocalDateTime.now().minusDays(i);
                LocalDateTime from = now.truncatedTo(ChronoUnit.DAYS);
                LocalDateTime to = from.plusDays(1);
                sum += Optional.ofNullable(repository.countPeople(symptom.name(), from, to)).orElse(0);
            }
            ret.add(GuestGraph.builder()
                    .label(symptom.name())
                    .value(String.valueOf(sum))
                    .build());
        }
        return ret;
    }

    public List<IncomeOverview> getTodayIncome() {
        List<IncomeOverview> ret = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            LocalDateTime now = LocalDateTime.now().minusHours(i);
            LocalDateTime from = now.truncatedTo(ChronoUnit.HOURS);
            LocalDateTime to = from.plusHours(1);
            int sum = 0;
            for (Symptoms symptom : Symptoms.values()) {
                sum += repository.getIncomeByTimeRange(symptom.name(), from, to);
            }
            ret.add(IncomeOverview.builder()
                    .hour(hourSdf.format(Date.from(from.atZone(ZoneId.systemDefault()).toInstant())))
                    .income(sum)
                    .build());
        }
        return ret;
    }

    public List<GuestGraph> getTodayGuestInfo() {
        List<GuestGraph> ret = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime from = now.truncatedTo(ChronoUnit.HOURS);
        LocalDateTime to = from.plusHours(1);
        for (Symptoms symptom : Symptoms.values()) {
            Integer count = Optional.ofNullable(repository.countPeople(symptom.name(), from, to))
                    .orElse(0);
            ret.add(GuestGraph.builder()
                    .label(symptom.name())
                    .value(String.valueOf(count))
                    .build());
        }
        return ret;
    }

    public Feedback getFeedback() {
        Random r = new Random();
        int total = 0;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime from = now.truncatedTo(ChronoUnit.DAYS);
        LocalDateTime to = from.plusDays(1);
        for (Symptoms symptom : Symptoms.values()) {
            total += Optional.ofNullable(repository.countPeople(symptom.name(), from, to)).orElse(0);
        }
        double negative = r.nextInt(total / 10);
        double positive = total - negative;
        return Feedback.builder()
                .positive(positive / total)
                .negative(negative / total)
                .progress(positive / total)
                .total(total)
                .build();
    }

    public TotalIncome getTodayTotalIncome() {
        int value = 0;
        int people = 0;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime from = now.truncatedTo(ChronoUnit.DAYS);
        LocalDateTime to = from.plusDays(1);
        for (Symptoms symptom : Symptoms.values()) {
            value += Optional.ofNullable(repository.getIncomeByTimeRange(symptom.name(), from, to)).orElse(0);
            people += Optional.ofNullable(repository.countPeople(symptom.name(), from, to)).orElse(0);
        }
        return TotalIncome.builder()
                .value(value)
                .progress(1.0)
                .total(people)
                .build();
    }

    public void insert(TodayOverview overview) {
        repository.insert(overview);
    }
}
