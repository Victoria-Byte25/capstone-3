package org.yearup.data.mysql;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.yearup.data.ShoppingCartDao;
import org.yearup.models.ShoppingCartItem;
import org.yearup.models.Product;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sql.DataSource;
import java.util.List;

@Component
public class MySqlShoppingCartDao extends MySqlDaoBase implements ShoppingCartDao
{
    public MySqlShoppingCartDao(DataSource dataSource)
    {
        super(dataSource);
    }

    @Override
    public List<ShoppingCartItem> getCartItems(int userId)
    {
        String sql = "SELECT p.*, sc.quantity " +
                "FROM cart_item sc " +
                "JOIN products p ON p.product_id = sc.product_id " +
                "WHERE sc.user_id = ?";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            ShoppingCartItem item = new ShoppingCartItem();
            Product product = new Product();

            product.setProductId(rs.getInt("product_id"));
            product.setName(rs.getString("name"));
            product.setPrice(rs.getBigDecimal("price"));
            product.setDescription(rs.getString("description"));

            item.setProduct(product);
            item.setQuantity(rs.getInt("quantity"));

            return item;
        }, userId);
    }

    @Override
    public void addProduct(int userId, int productId)
    {
        String sql = """
                INSERT INTO shopping_cart (user_id, product_id, quantity)
                VALUES (?, ?, 1)
                ON DUPLICATE KEY UPDATE quantity = quantity + 1;
                """;

        jdbcTemplate.update(sql, userId, productId);
    }

    @Override
    public void updateQuantity(int userId, int productId, int quantity)
    {
        String sql = "UPDATE shopping_cart SET quantity = ? WHERE user_id = ? AND product_id = ?";
        jdbcTemplate.update(sql, quantity, userId, productId);
    }

    @Override
    public void clearCart(int userId)
    {
        String sql = "DELETE FROM shopping_cart WHERE user_id = ?";
        jdbcTemplate.update(sql, userId);
    }

    @Override
    public void addToCart(int userId, int productId)
    {
        String sql = """
        INSERT INTO cart_item (user_id, product_id, quantity)
        VALUES (?, ?, 1)
        ON DUPLICATE KEY UPDATE quantity = quantity + 1;
    """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setInt(1, userId);
            stmt.setInt(2, productId);
            stmt.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Failed to add to cart", e);
        }
    }
}

