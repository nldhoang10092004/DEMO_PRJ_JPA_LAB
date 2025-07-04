/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package productDAO;

import dao.DBConnection;
import java.sql.SQLException;
import java.util.List;
import model.User;
import java.sql.*;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Product;

/**
 *
 * @author LOQ
 */
public class ProductDAO implements IProductDAO{
    private static final String INSERT_PRODUCT = "INSERT INTO Product (name, price, description, stock) VALUES (?, ?, ?, ?)";
    private static final String SELECT_PRODUCT_BY_ID = "SELECT * FROM Product Where id = ?";
    private static final String SELECT_ALL_PRODUCT = "SELECT * FROM Product";
    private static final String UPDATE_PRODUCT = "UPDATE Product SET name = ?, price = ?, description = ?, stock = ? WHERE id = ?";
    private static final String DELETE_PRODUCT = "DELETE FROM Product WHERE id = ?";
    private static final String UPDATE_STOCK = "UPDATE Product SET stock = ? WHERE id = ?";
    @Override
    public void insertProduct(Product pro) {
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement(INSERT_PRODUCT);
            ps.setString(1, pro.getName());
            ps.setDouble(2, pro.getPrice());
            ps.setString(3, pro.getDescription());
            ps.setInt(4, pro.getStock());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public Product selectProduct(int id) {
        Product pro = null;
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(SELECT_PRODUCT_BY_ID)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String name = rs.getString("name");
                double price = rs.getDouble("price");
                String description = rs.getString("description");
                int stock = rs.getInt("stock");
                String import_date = rs.getString("import_date");
                pro = new Product(id, name, price, description, stock, import_date);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pro;
    }
    
    @Override
    public List<Product> selectAllProducts() {
        List<Product> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement(SELECT_ALL_PRODUCT);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                double price = rs.getDouble("price");
                String description = rs.getString("description");
                int stock = rs.getInt("stock");
                String import_date = rs.getString("import_date");
                list.add(new Product(id, name, price, description, stock, import_date));
            }
        } catch (SQLException ex) {
            Logger.getLogger(ProductDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }
    
    @Override
    public boolean deleteProduct(int id) {
        boolean delete = false;
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement(DELETE_PRODUCT);
            ps.setInt(1, id);
            delete = ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println(e);
        }
        return delete;
    }
    
    @Override
    public boolean updateProduct(Product pro) throws SQLException {
        boolean updated;
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(UPDATE_PRODUCT)) {
             ps.setString(1, pro.getName());
            ps.setDouble(2, pro.getPrice());
            ps.setString(3, pro.getDescription());
            ps.setInt(4, pro.getStock());
            ps.setInt(5, pro.getId());
            updated = ps.executeUpdate() > 0;
        }
        return updated;
    }

    @Override
    public boolean updateStock(int productId, int newStock){
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(UPDATE_STOCK)) {
            ps.setInt(1, newStock);
            ps.setInt(2, productId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e){
            System.out.println(e.getMessage());
            return false;
        }
    }
}
