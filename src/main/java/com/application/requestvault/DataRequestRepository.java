package com.application.requestvault;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class DataRequestRepository {

    private final JdbcTemplate jdbcTemplate;

    public DataRequestRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private DataRequest mapRow(ResultSet row, int rowNumber)
            throws SQLException {

        return new DataRequest(
                row.getLong("id"),
                row.getString("customer_id"),
                row.getObject("from_date", LocalDate.class),
                row.getObject("to_date", LocalDate.class)
        );
    }

    public List<DataRequest> findAll() {
        String sql = """
        SELECT id, customer_id, from_date, to_date
        FROM data_requests
        ORDER BY id
        """;

        return jdbcTemplate.query(sql, this::mapRow);
    }

    public Optional<DataRequest> findById(long id) {
        String sql = """
        SELECT id, customer_id, from_date, to_date
        FROM data_requests
        WHERE id = ?
        """;

        return jdbcTemplate.query(sql, this::mapRow, id)
                .stream()
                .findFirst();
    }

    public DataRequest create(CreateDataRequest input) {
        String sql = """
        INSERT INTO data_requests (customer_id, from_date, to_date)
        VALUES (?, ?, ?)
        RETURNING id, customer_id, from_date, to_date
        """;

        return jdbcTemplate.queryForObject(
                sql,
                this::mapRow,
                input.customerId(),
                input.fromDate(),
                input.toDate()
        );
    }

}