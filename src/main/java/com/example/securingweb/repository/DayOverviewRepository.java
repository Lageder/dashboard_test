package com.example.securingweb.repository;

import com.example.securingweb.model.db.DayOverview;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.List;

public interface DayOverviewRepository extends CassandraRepository<DayOverview, String> {

    List<DayOverview> findBySymptom(String symptom);
}
