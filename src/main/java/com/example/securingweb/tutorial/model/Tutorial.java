package com.example.securingweb.model.database;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.UUID;

@Table
public class Tutorial {
    @PrimaryKey
    private UUID id;

    private String title;
    private String description;
    
}
