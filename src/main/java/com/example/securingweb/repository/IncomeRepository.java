package com.example.securingweb.repository;

import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.select.Select;
import com.datastax.oss.driver.api.querybuilder.select.Selector;
import com.example.securingweb.model.db.TodayOverview;
import lombok.RequiredArgsConstructor;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.ZoneId;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.literal;

@Repository
@RequiredArgsConstructor
public class IncomeRepository {

    private final CassandraOperations operations;

    public Integer getIncomeByTimeRange(String symptom, LocalDateTime from, LocalDateTime to) {
        Select query = QueryBuilder.selectFrom("dayoverview").function("SUM", Selector.column("fee"))
                .whereColumn("symptom").isEqualTo(literal(symptom))
                .whereColumn("visit_time").isGreaterThanOrEqualTo(literal(toMs(from)))
                .whereColumn("visit_time").isLessThan(literal(toMs(to)));
        SimpleStatement statement = query.build();
        return operations.selectOne(statement, Integer.class);
    }

    public Integer countPeople(String symptom, LocalDateTime from, LocalDateTime to) {
        Select query = QueryBuilder.selectFrom("dayoverview").countAll()
                .whereColumn("symptom").isEqualTo(literal(symptom))
                .whereColumn("visit_time").isGreaterThanOrEqualTo(literal(toMs(from)))
                .whereColumn("visit_time").isLessThan(literal(toMs(to)));
        SimpleStatement statement = query.build();
        return operations.selectOne(statement, Integer.class);
    }



    public TodayOverview insert(TodayOverview overview) {
        return operations.insert(overview);
    }

    private long toMs(LocalDateTime t) {
        return t.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

}
