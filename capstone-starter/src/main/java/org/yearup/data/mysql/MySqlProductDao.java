package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.models.Product;
import org.yearup.data.ProductDao;

import javax.sound.sampled.SourceDataLine;
import javax.sql.DataSource;
import java.sql.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class MySqlProductDao extends MySqlDaoBase implements ProductDao
{
    public MySqlProductDao(DataSource dataSource)
    {
        super(dataSource);
    }

    @Override
    public List<Product> search(Integer categoryId, BigDecimal minPrice, BigDecimal maxPrice, String color) {
        List<Product> products = new ArrayList<>();

        StringBuilder sql = new StringBuilder("SELECT * FROM products WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (categoryId != null && categoryId > 0) {
            sql.append(" AND category_id = ?");
            params.add(categoryId);
        }

        if (minPrice != null) {
            sql.append(" AND price >= ?");
            params.add(minPrice);
        }

        if (maxPrice != null) {
            sql.append(" AND price <= ?");
            params.add(maxPrice);
        }

        if (color != null && !color.isEmpty()) {
            sql.append(" AND LOWER(color) = ?");
            params.add(color.trim().toLowerCase());
        }

        try (Connection conn = getConnection()) {
            System.out.println("Generated SQL: " + sql);
            System.out.println("Parameters: " + params);

            PreparedStatement stmt = prepareStatement(conn, sql.toString(), params);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Product product = mapRow(rs);
                products.add(product);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error searching products", e);
        }

        return products;
    }

    @Override
    public Product getById(int productId)
    {
        String sql = "SELECT * FROM products WHERE product_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setInt(1, productId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next())
            {
                return mapRow(rs);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            throw new RuntimeException("Error finding product by ID", e);
        }

        return null;
    }

    @Override
    public Product create(Product product)
    {
        String sql = """
                INSERT INTO products 
                (name, price, category_id, description, color, stock, featured, image_url)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
        {
            stmt.setString(1, product.getName());
            stmt.setBigDecimal(2, product.getPrice());
            stmt.setInt(3, product.getCategoryId());
            stmt.setString(4, product.getDescription());
            stmt.setString(5, product.getColor());
            stmt.setInt(6, product.getStock());
            stmt.setBoolean(7, product.isFeatured());
            stmt.setString(8, product.getImageUrl());

            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next())
            {
                int id = keys.getInt(1);
                return getById(id);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            throw new RuntimeException("Error creating product", e);
        }

        return null;
    }

    @Override
    public void update(int productId, Product product)
    {
        String sql = """
                UPDATE products 
                SET name = ?, price = ?, category_id = ?, description = ?, color = ?, stock = ?, featured = ?, image_url = ? 
                WHERE product_id = ?
                """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setString(1, product.getName());
            stmt.setBigDecimal(2, product.getPrice());
            stmt.setInt(3, product.getCategoryId());
            stmt.setString(4, product.getDescription());
            stmt.setString(5, product.getColor());
            stmt.setInt(6, product.getStock());
            stmt.setBoolean(7, product.isFeatured());
            stmt.setString(8, product.getImageUrl());
            stmt.setInt(9, productId);

            stmt.executeUpdate();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            throw new RuntimeException("Error updating product", e);
        }
    }

    @Override
    public void delete(int productId)
    {
        String sql = "DELETE FROM products WHERE product_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setInt(1, productId);
            stmt.executeUpdate();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            throw new RuntimeException("Error deleting product", e);
        }
    }

    @Override
    public List<Product> listByCategoryId(int categoryId)
    {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE category_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setInt(1, categoryId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next())
            {
                Product product = mapRow(rs);
                products.add(product);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            throw new RuntimeException("Error retrieving products by category ID", e);
        }

        return products;
    }

    private PreparedStatement prepareStatement(Connection conn, String sql, List<Object> params) throws SQLException
    {
        PreparedStatement stmt = conn.prepareStatement(sql);
        for (int i = 0; i < params.size(); i++)
        {
            stmt.setObject(i + 1, params.get(i));
        }
        return stmt;
    }

    protected static Product mapRow(ResultSet row) throws SQLException
    {
        int productId = row.getInt("product_id");
        String name = row.getString("name");
        BigDecimal price = row.getBigDecimal("price");
        int categoryId = row.getInt("category_id");
        String description = row.getString("description");
        String color = row.getString("color");
        int stock = row.getInt("stock");
        boolean isFeatured = row.getBoolean("featured");
        String imageUrl = row.getString("image_url");

        return new Product(productId, name, price, categoryId, description, color, stock, isFeatured, imageUrl);
    }
}
