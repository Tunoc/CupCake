/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package persistence;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import logic.Bottom;
import logic.Cupcake;
import logic.IProduct;
import logic.Topping;

/**
 * This class has the purpose of mapping Products from the database to jave objects. General data, such as Cupcakes and intances where all tables are being checked,
 * can be collected through this class. Data regarding Bottoms or Toppings, make use of {@link logic.Bottom} and {@link logic.Topping}
 * @author rando
 * @author Benjamin Paepke
 */
public class ProductMapper {
    protected String table = "", product_id = "", product_name = "", product_price ="";
    private SQLConnection connection;

    /**
     * Constructor of a ProductMapper
     * @param connection is the connection to the database
     */
    public ProductMapper(SQLConnection connection) {
        this.connection = connection;
    }

    /**
     * Gets all cupcakes from database
     * @return an ArrayList of cupcakes
     * @throws ProductException if cupcakes cannot be fetched from database
     */
    public ArrayList<Cupcake> getAllProducts() throws ProductException {
        ArrayList<Cupcake> cupcakes = new ArrayList<>();
        String sql = "SELECT * FROM bottoms, toppings";
        try {
            PreparedStatement ps = connection.getConnection().prepareStatement(sql);
            ResultSet rs = connection.selectQuery(ps);
            while(rs.next()){
                cupcakes.add(findCupcakeFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new ProductException("Error when fetching all Cupcakes");
        }
        return cupcakes;
    }

    /**
     * Gets cupcake from a given ID
     * @param id the id of the cupcake
     * @return the cupcake with the given id
     * @throws ProductException if anything goes wrong while trying to fetch cupcake
     */
    public Cupcake getProductFromID(int id) throws ProductException {
        String sql = "SELECT * FROM Cupcakes WHERE cupcake_id = ?";
        Cupcake cupcake = null;
        try {
            PreparedStatement ps = connection.getConnection().prepareStatement(sql);
            ps.setInt(1, id);
            cupcake = findCupcakeFromResultSet(connection.selectQuery(ps));
        } catch (SQLException e) {
            throw new ProductException("Error when fetching Cupcake");
        }
        return cupcake;
    }
    
//    public boolean deleteProductFromID(IProduct product) throws ProductException{
//        String sql = "SELECT * FROM cupcakes WHERE cupcake_id = ?";
//        try{
//            PreparedStatement ps = connection.getConnection().prepareStatement(sql);
//            ps.setLong(1, product.getId());
//            ResultSet rs = connection.selectQuery(ps);
//            if(!rs.next()){
//                throw new ProductException("User dosn't exist in database");
//            } else {
//                connection.getConnection().setAutoCommit(false);
//                try{
//                    //Delete from cupcakes
//                    //SELECT * FROM cupcakes INNER JOIN toppings USING (topping_id) WHERE topping_id = 10;
//                    String deleteCupcake = "DELETE FROM cupcakes WHERE cupcake_id = ?";
//                    PreparedStatement cupcakePS = connection.getConnection().prepareStatement(deleteCupcake);
//                    cupcakePS.setLong(1, product.getId());
//                    if(!connection.executeQuery(cupcakePS)){
//                        throw new SQLException("Product could not be deleted");
//                    }
//                    //Delete from topping/bottom
//                    String deleteTopBot = "DELETE FROM " + ;
//                    //Commit all transactions
//                    connection.getConnection().commit();
//                } catch (SQLException ex) {
//                    connection.getConnection().rollback();
//                    throw new ProductException("Product could not be deleted");
//                } finally {
//                    connection.getConnection().setAutoCommit(true);
//                }
//            }
//        }catch (SQLException e){
//            throw new ProductException("Product could not be deleted");
//        }
////        String[] topOrBot = topOrBot(validation);
////        String table = topOrBot[0]; //Toppings/Bottoms
////        String row = topOrBot[1]; //topping/bottom
////        String sql = "DELETE FROM " + table + " WHERE " + row + "_id = ?";
////        try {
////            PreparedStatement ps = connection.getConnection().prepareStatement(sql);
////            ps.setInt(1, id);
////            return connection.executeQuery(ps); //If sucsess <-True
////        } catch (SQLException e) {
////            throw new ProductException("Error when fetching Cupcake");
////        }
//////TODO(Tobias): Update All Cupcakes?
////        //Update Cupcakes Topping id + Alle Bottom id
////        //Update Cupcakes Bottom id + Alle Topping id
//        return false;
//        
//    }

    /**
     * Finds cupcake from a given Resultset
     * @param rs the resultset from where you want to find the cupcake
     * @return the cupcake from the resultset
     * @throws SQLException if anything goes wrong while trying to find cupcake
     */
    private Cupcake findCupcakeFromResultSet(ResultSet rs) throws SQLException {
        //Cupcake Topping object creation
        int topID = rs.getInt("topping_id");
        int topPrice = rs.getInt("topping_price");
        String topName = rs.getString("topping_name");
        String topPic = rs.getString("topping_picture");
        Topping top = new Topping(topPrice, topName);
        top.setId(topID);
        //Cupcake Bottom object creation
        int botID = rs.getInt("bottom_id");
        int botPrice = rs.getInt("bottom_price");
        String botName = rs.getString("bottom_name");
        String botPic = rs.getString("bottom_picture");
        Bottom bot = new Bottom(botPrice, botName);
        bot.setId(botID);
        //Completed Cupcake object return
        return new Cupcake(bot, top);
    }

    /**
     * Creates a product, either {@link logic.Bottom} or {@link logic.Topping}
     * @param product The topping to be created.
     * @throws ProductException If anything goes wrong in the creation of the topping.
     */
    public void createProduct(IProduct product) throws ProductException {
        String sql = "SELECT * FROM "+table+" where "+product_name+" = ?";
        try {
            PreparedStatement statement = connection.getConnection().prepareStatement(sql);
            statement.setString(1,product.getName());
            ResultSet rs = connection.selectQuery(statement);
            if(rs.next()){
                throw new ProductException("Product already exists");
            }
            else {
                sql = "INSERT INTO "+table+" ("+product_name+", "+product_price+") VALUES (?,?)";
                statement = connection.getConnection().prepareStatement(sql);
                statement.setString(1,product.getName());
                statement.setInt(2, product.getPrice());
                if(connection.executeQuery(statement)) {
                    int id = connection.lastID();
                    product.setId(id);
                }
                else {
                    throw new ProductException("Could not create product");
                }
            }
        } catch (SQLException e) {
            throw new ProductException("Connection failed");
        }
    }

    /**
     * Updates a product either {@link logic.Bottom} or {@link logic.Topping}
     * @param product The bottom or topping to be updated
     * @throws ProductException If anything goes wrong while updating product
     */
    public void updateProduct(IProduct product) throws ProductException{
        String sql = "SELECT * FROM "+table+" where "+product_name+" = ?";
        try{
            PreparedStatement ps = connection.getConnection().prepareStatement(sql);
            ps.setString(1,product.getName());
            ResultSet rs = connection.selectQuery(ps);
            if(!rs.next()){
                throw new ProductException("Product doesn't exist in database");
            }
            else{
                sql = "UPDATE "+table+" SET "+product_name+"= ?, "+product_price+" = ? WHERE "+product_id+" = ?";
                ps = connection.getConnection().prepareStatement(sql);
                ps.setString(1,product.getName());
                ps.setInt(2,product.getPrice());
                ps.setInt(3,product.getId());
                if(!connection.executeQuery(ps)){
                    throw new SQLException("Product could not be updated");
                }
            }
        }catch(SQLException e){
            throw new ProductException("Product could not be updated");
        }
    }
}



