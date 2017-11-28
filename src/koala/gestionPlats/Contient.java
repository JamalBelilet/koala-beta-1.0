package koala.gestionPlats;


import koala.db.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Contient {
    private int idIngredient;
    private int idPlat;
    private double quantite;

    public static Connection connection = ConnectionManager.getInstance().getConnection();

    public static boolean ajouterIngredientPlat(int idIngredient, int idPlat, double quantite) {
        String sql = "INSERT INTO `contient`(`idIngredient`, `idPlat`, `quantite`) " +
                "VALUES (?, ?, ?)";

        try (
                PreparedStatement stmt = connection.prepareStatement(sql);
        ){
            stmt.setInt(1,idIngredient);
            stmt.setInt(2, idPlat);
            stmt.setDouble(3, quantite);

        } catch (SQLException e) {
            System.err.println(e);
            return false;
        }

        return true;
    }

    //todo:many things


    public int getIdIngredient() {
        return idIngredient;
    }

    public void setIdIngredient(int idIngredient) {
        this.idIngredient = idIngredient;
    }

    public int getIdPlat() {
        return idPlat;
    }

    public void setIdPlat(int idPlat) {
        this.idPlat = idPlat;
    }

    public double getQuantite() {
        return quantite;
    }

    public void setQuantite(double quantite) {
        this.quantite = quantite;
    }
}