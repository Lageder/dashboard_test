package com.example.securingweb.repository;

import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.select.Select;
import com.example.securingweb.model.db.DailyPrescription;
import lombok.RequiredArgsConstructor;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.ZoneId;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.literal;

@Repository
@RequiredArgsConstructor
public class PrescriptionRepository {

    private final CassandraOperations operations;

    public Integer countWithRange(String name, LocalDateTime from, LocalDateTime to) {
        Select query = QueryBuilder.selectFrom("prescription").countAll()
                .whereColumn("name").isEqualTo(literal(name))
                .whereColumn("deploy_time").isGreaterThanOrEqualTo(literal(toMs(from)))
                .whereColumn("deploy_time").isLessThan(literal(toMs(to)));
        SimpleStatement statement = query.build();
        return operations.selectOne(statement, Integer.class);
    }

    public DailyPrescription insert(DailyPrescription prescription) {
        return operations.insert(prescription);
    }

    private long toMs(LocalDateTime t) {
        return t.atZone(ZoneId.of("UTC")).toInstant().toEpochMilli();
    }

}
