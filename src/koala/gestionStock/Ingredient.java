package koala.gestionStock;

import koala.db.ConnectionManager;

import java.sql.*;

/**
 * Created by Jamal on 3/7/2017.
 */
public class Ingredient {
    public int idIngredient;
    public String nom;
    public String description;
    public double calorie,proteine,glucide,lipide;
    public boolean gluten;



    public static Connection connection = ConnectionManager.getInstance().getConnection();

    public boolean Add ( int idingredient,String nom,String description, double calorie,double proteine,double glucide,double lipide,boolean gluten){

        try (
                Statement stmt = connection.createStatement();
          ){
            stmt.executeUpdate("INSERT into Ingredient VALUES ("+ idingredient + ", " + nom + ", " + description + ", "+calorie+", "+proteine+", "+glucide+", "+lipide+","+glucide+")");
            return true;
        } catch(Exception ex){
            ex.printStackTrace();
        }
        return false;

    }




    public static Ingredient getIngredient ( int idingredient){

        Ingredient ingredient = new Ingredient();
        try (
                Statement stmt = connection.createStatement();
          ){
             ResultSet resultSet = stmt.executeQuery("SELECT * from ingredient WHERE idIngredient = "+ idingredient);
             resultSet.next();
             ingredient.idIngredient = idingredient;
             ingredient.nom = resultSet.getString("nom");
             ingredient.description = resultSet.getString("description");
             ingredient.calorie = resultSet.getDouble("calorie");
             ingredient.proteine = resultSet.getDouble("proteine");
             ingredient.glucide = resultSet.getDouble("glucide");
             ingredient.lipide = resultSet.getDouble("lipide");
             ingredient.gluten = resultSet.getBoolean("gluten");
             return ingredient;


        } catch(Exception ex){
            ex.printStackTrace();
        }
        return null;

    }

    public boolean Delete ( int idingredient) {

        try (
                Statement stmt = connection.createStatement();
        ){
            stmt.executeUpdate("DELETE FROM Ingredient WHERE idingredient=" +idingredient +")");
            return true;
        } catch(Exception ex){
            ex.printStackTrace();
        }return false;


    }

    public static boolean update(Ingredient ing) throws Exception {

        String sql =
                "UPDATE plat SET " +
                        "idCategorie = ?, nom = ?, prix = ?, dateIntronisation = ?, " +
                        "description = ?, image = ?, tempsPreparation = ?, disponible = ?" +
                        "WHERE idPlat = ?";
        try (
                PreparedStatement stmt = connection.prepareStatement(sql);
        ){
            stmt.setString(1, ing.nom);
            stmt.setString(2, ing.description);
            stmt.setDouble(3, ing.calorie);
            stmt.setDouble(4, ing.proteine);
            stmt.setDouble(5, ing.glucide);
            stmt.setDouble(6, ing.lipide);
            stmt.setBoolean(7, ing.gluten);

            int affected = stmt.executeUpdate();
            if (affected == 1) {
                return true;
            } else {
                return false;
            }
        }
        catch(SQLException e) {
            System.err.println(e);
            return false;
        }
    }

    public static boolean updateIngredientTable ( int oldId, int idIngredient,String nom,String description, double calorie,double proteine,double glucide,double lipide,boolean gluten){
        try (
                Statement stmt = connection.createStatement()
        ){
           if (idIngredient!=-1)
               stmt.executeUpdate("update Ingredient set idIngredient=" +idIngredient+" WHERE idIngredient = " + oldId);
           if (nom!=null)
               stmt.executeUpdate("update Ingredient set nom='" +nom+"' WHERE idIngredient = " + oldId);
           if (description!=null)
               stmt.executeUpdate("update Ingredient set description='" +description +"' WHERE idIngredient = " + oldId);
           if (calorie!=-1)
               stmt.executeUpdate("update Ingredient set calorie=" +calorie +" WHERE idIngredient = " + oldId);
           if (proteine!=-1)
               stmt.executeUpdate("update Ingredient set proteine=" +proteine +" WHERE idIngredient = " + oldId);
           if (glucide!=-1)
               stmt.executeUpdate("update Ingredient set glucide=" +glucide +" WHERE idIngredient = " + oldId);
           if (lipide!=-1)
               stmt.executeUpdate("update Ingredient set lipide=" +lipide +" WHERE idIngredient = " + oldId);
           if (!gluten)
               stmt.executeUpdate("update Ingredient set gluten=" +gluten+" WHERE idIngredient = " + oldId);

            return true;
        } catch(Exception ex){
            ex.printStackTrace();
        }return false;
    }


    public int getIdIngredient() {
        return idIngredient;
    }

    public void setIdIngredient(int idIngredient) {
        this.idIngredient = idIngredient;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getCalorie() {
        return calorie;
    }

    public void setCalorie(double calorie) {
        this.calorie = calorie;
    }

    public double getProteine() {
        return proteine;
    }

    public void setProteine(double proteine) {
        this.proteine = proteine;
    }

    public double getGlucide() {
        return glucide;
    }

    public void setGlucide(double glucide) {
        this.glucide = glucide;
    }

    public double getLipide() {
        return lipide;
    }

    public void setLipide(double lipide) {
        this.lipide = lipide;
    }

    public boolean isGluten() {
        return gluten;
    }

    public void setGluten(boolean gluten) {
        this.gluten = gluten;
    }
}
