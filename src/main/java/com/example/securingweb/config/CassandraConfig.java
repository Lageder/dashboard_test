package com.example.securingweb.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.config.CassandraEntityClassScanner;
import org.springframework.data.cassandra.config.SchemaAction;
import org.springframework.data.cassandra.core.convert.CassandraCustomConversions;
import org.springframework.data.cassandra.core.cql.keyspace.CreateKeyspaceSpecification;
import org.springframework.data.cassandra.core.cql.keyspace.KeyspaceOption;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.*;

public class CassandraConfig extends AbstractCassandraConfiguration {

    @Value("${spring.data.cassandra.keyspace-name}")
    protected String keyspace;

    @Value("${spring.data.cassandra.contact-points}")
    protected String contactPoint;

    @Override
    protected String getKeyspaceName() {
        return keyspace;
    }

    @Override
    public SchemaAction getSchemaAction() {
        return SchemaAction.CREATE_IF_NOT_EXISTS;
    }

    @Override
    protected List<CreateKeyspaceSpecification> getKeyspaceCreations() {
        return Collections.singletonList(CreateKeyspaceSpecification
                .createKeyspace(keyspace)
                .ifNotExists()
                .with(KeyspaceOption.DURABLE_WRITES, true)
                .withSimpleReplication());
    }

    @Override
    protected String getContactPoints() {
        return contactPoint;
    }

    @Override
    public String[] getEntityBasePackages() {
        return new String[]{"com.example.securingweb.model"};
    }

    @Override
    protected Set<Class<?>> getInitialEntitySet() throws ClassNotFoundException {
        return CassandraEntityClassScanner.scan(getEntityBasePackages());
    }

    @ReadingConverter
    public enum LocalDateTimeReadConverter implements Converter<Instant, LocalDateTime> {
        INSTANT;

        @Override
        public LocalDateTime convert(Instant instant) {
            return LocalDateTime.ofInstant(instant, ZoneOffset.systemDefault());
        }
    }

    @WritingConverter
    public enum LocalDateTimeWriteConverter implements Converter<LocalDateTime, Instant> {
        INSTANT;


        @Override
        public Instant convert(LocalDateTime source) {
            return source.atZone(ZoneId.systemDefault()).toInstant();
        }
    }

    @Bean
    public CassandraCustomConversions customConversions() {
        Set<Converter<?, ?>> converters = new HashSet<>();
        converters.add(LocalDateTimeReadConverter.INSTANT);
        converters.add(LocalDateTimeWriteConverter.INSTANT);
        return new CassandraCustomConversions(new ArrayList<>(converters));
    }
}