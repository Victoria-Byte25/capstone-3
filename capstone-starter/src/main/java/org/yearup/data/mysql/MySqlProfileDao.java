package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.ProfileDao;
import org.yearup.models.Profile;

import javax.sql.DataSource;
import java.sql.*;

@Component
public class MySqlProfileDao extends MySqlDaoBase implements ProfileDao
{
    public MySqlProfileDao(DataSource dataSource)
    {
        super(dataSource);
    }

    @Override
    public Profile create(Profile profile)
    {
        String sql = "INSERT INTO profiles (user_id, first_name, last_name, phone, email, address, city, state, zip) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = getConnection())
        {
            PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setInt(1, profile.getUserId());
            ps.setString(2, profile.getFirstName());
            ps.setString(3, profile.getLastName());
            ps.setString(4, profile.getPhone());
            ps.setString(5, profile.getEmail());
            ps.setString(6, profile.getAddress());
            ps.setString(7, profile.getCity());
            ps.setString(8, profile.getState());
            ps.setString(9, profile.getZip());

            ps.executeUpdate();
            return profile;
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Profile findByUsername(String username)
    {
        String sql = "SELECT * FROM profiles WHERE user_id = (SELECT user_id FROM users WHERE username = ?)";

        try (Connection connection = getConnection())
        {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next())
            {
                Profile profile = new Profile();
                profile.setUserId(rs.getInt("user_id"));
                profile.setFirstName(rs.getString("first_name"));
                profile.setLastName(rs.getString("last_name"));
                profile.setPhone(rs.getString("phone"));
                profile.setEmail(rs.getString("email"));
                profile.setAddress(rs.getString("address"));
                profile.setCity(rs.getString("city"));
                profile.setState(rs.getString("state"));
                profile.setZip(rs.getString("zip"));
                return profile;
            }

            return null;
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Profile profile)
    {
        String sql = """
                UPDATE profiles
                SET first_name = ?, last_name = ?, phone = ?, address = ?, city = ?, state = ?, zip = ?
                WHERE email = ?
                """;

        jdbcTemplate.update(sql,
                profile.getFirstName(),
                profile.getLastName(),
                profile.getPhone(),
                profile.getAddress(),
                profile.getCity(),
                profile.getState(),
                profile.getZip(),
                profile.getEmail()
        );
    }
}