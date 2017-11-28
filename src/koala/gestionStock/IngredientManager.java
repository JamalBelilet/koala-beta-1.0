package koala.gestionStock;


import koala.db.ConnectionManager;

import java.sql.*;
import java.time.LocalDate;

public class IngredientManager {


    public static Connection connection = ConnectionManager.getInstance().getConnection();


    public static Ingredient loadCurrentRowToObject(ResultSet rs) throws SQLException {
        Ingredient ingredient = new Ingredient();

        ingredient.setIdIngredient(rs.getInt("idIngredient"));
        ingredient.setNom(rs.getString("nom"));
        ingredient.setDescription(rs.getString("description"));
        ingredient.setCalorie(rs.getDouble("calorie"));
        ingredient.setProteine(rs.getDouble("proteine"));
        ingredient.setGlucide(rs.getDouble("glucide"));
        ingredient.setLipide(rs.getDouble("lipide"));
        ingredient.setGluten(rs.getBoolean("gluten"));

        return ingredient;
    }

    public static int addIngredient(String nom, String description, double calorie, double proteine, double glucide, double lipide, boolean gluten, int idMagasin, int quantitie, double prixAchat, LocalDate datePeremption){

        try(
                Statement stmt=connection.createStatement();
        ) {
            System.out.println("INSERT INTO Ingredient (nom, description, calorie, proteine, glucide, lipide, gluten) VALUES ('"+nom+"', '"+description+"', "+calorie+", "+proteine+", "+glucide+", "+lipide+", "+gluten+" )");
            stmt.executeUpdate("INSERT INTO Ingredient (nom, description, calorie, proteine, glucide, lipide, gluten) VALUES ('"+nom+"', '"+description+"', "+calorie+", "+proteine+", "+glucide+", "+lipide+", "+gluten+" )");
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }

        int id = -1;

        try(
                Statement stmt=connection.createStatement();
                ResultSet rs=stmt.executeQuery("SELECT * FROM Ingredient WHERE nom='"+nom + "'");
        ) {
            if (rs.next()) id=rs.getInt("idIngredient");
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }

        try(
                Statement stmt=connection.createStatement();
        ) {
            System.out.println("__________________________________________");
            System.out.println("INSERT INTO stock (idIngredient, idMagasin, quantite, prixAchat, datePeromption) VALUES ('"+id+"', '"+idMagasin+"', "+quantitie+", "+prixAchat+", CAST( " + Date.valueOf(datePeremption)+") AS DATE)");
            System.out.println("__________________________________________");
            stmt.executeUpdate("INSERT INTO stock (idIngredient, idMagasin, quantitie, prixAchat, datePeremption) VALUES ('"+id+"', '"+idMagasin+"', "+quantitie+", "+prixAchat+",  '" + Date.valueOf(datePeremption)+"')");
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
        return id;

    }

    public static boolean Delete ( int idingredient) {

        try (
                Statement stmt = connection.createStatement();
        ){
            stmt.executeUpdate("DELETE FROM stock WHERE idIngredient="+idingredient);
            stmt.executeUpdate("DELETE FROM contient WHERE idIngredient="+idingredient);
            stmt.executeUpdate("DELETE FROM magasiniernotification WHERE idIngredient="+idingredient);
            stmt.executeUpdate("DELETE FROM miniStock WHERE idIngredient="+idingredient);
            stmt.executeUpdate("DELETE FROM Ingredient WHERE idingredient=" +idingredient);
            return true;
        } catch(Exception ex){
            ex.printStackTrace();
        }return false;


    }

}
