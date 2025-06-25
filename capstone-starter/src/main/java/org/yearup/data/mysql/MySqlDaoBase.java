package org.yearup.data.mysql;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.stream.Collectors;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;

public abstract class MySqlDaoBase
{
    protected JdbcTemplate jdbcTemplate;
    protected DataSource dataSource;

    public MySqlDaoBase(DataSource dataSource)
    {
        this.dataSource = dataSource;
        this.jdbcTemplate =new JdbcTemplate(dataSource);
    }

    protected Connection getConnection() throws SQLException
    {
        return dataSource.getConnection();
    }

    protected PreparedStatement preparedStatement(Connection conn, String sql, List<Object> params) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(sql);
        for (int i = 0; i < params.size(); i++) {
            stmt.setObject(i + 1, params.get(i));
        }
        return stmt;
    }
}
