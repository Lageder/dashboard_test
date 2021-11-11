package com.example.securingweb.model.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.LocalDateTime;

@Table
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DayOverview {

    @PrimaryKeyColumn(name = "symptom", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private String symptom;

    @PrimaryKeyColumn(name = "visit_time", ordinal = 1, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    private LocalDateTime visitTime;

    @Column
    private int fee;
}
