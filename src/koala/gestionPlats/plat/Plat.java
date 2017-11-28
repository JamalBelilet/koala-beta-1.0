package koala.gestionPlats.plat;


import koala.db.ConnectionManager;
import koala.gestionPlats.tag.Tag;
import koala.gestionPlats.tag.TagManager;
import koala.gestionStock.Ingredient;
import koala.gestionStock.IngredientManager;
import koala.gestionStock.mini.IngredientAndQuantite;
import koala.statistiques.Statistique;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class Plat implements Comparable<Plat> {

    public static Connection connection = ConnectionManager.getInstance().getConnection();

    //todo:deal with images

    private int idPlat;
    private int idCategorie;
    private String nom;
    private double prix;
    private Date dateIntronisation;
    private String description;
    private Blob image;
    private int tempsPreparation;
    private boolean disponible;
    private int parallele;

    public List<IngredientAndQuantite> ingredientAndQuantites = new ArrayList<>();





    @Override
    public String toString() {
        return nom + " #" + idPlat;
    }

    public double getCalorie() throws SQLException {
        return getCarac("calorie");
    }

    public double getProteine() throws SQLException {
        return getCarac("proteine");
    }

    public double getGlucide() throws SQLException {
        return getCarac("glucide");
    }

    public double getLipide() throws SQLException {
        return getCarac("lipide");
    }

    private double getCarac(String type) throws SQLException {
        String sql = "SELECT * FROM contient WHERE idPlat = ?";
        ResultSet rs = null;

        String sql2 = "SELECT * FROM ingredient WHERE idIngredient = ?";
        ResultSet rs2 = null;

        double d = 0;

        try (
                PreparedStatement stmt = connection.prepareStatement(sql);
                PreparedStatement stmt2 = connection.prepareStatement(sql2);
        ){
            stmt.setInt(1, this.getIdPlat());
            rs = stmt.executeQuery();

            while (rs.next()) {
                stmt2.setInt(1, rs.getInt("idIngredient"));
                rs2 = stmt2.executeQuery();

                if (rs2.next())
                    d += rs2.getDouble(type) * rs.getDouble("quantite") / 100;
            }

            return d;

        } catch (SQLException e) {
            System.err.println(e);
            return 0;
        } finally {
            if (rs != null)
                rs.close();
            if (rs2 != null)
                rs2.close();

        }
    }


    public boolean withGluten() throws SQLException {
        String sql = "SELECT * FROM contient WHERE idPlat = ?";
        ResultSet rs = null;

        String sql2 = "SELECT * FROM ingredient WHERE idIngredient = ?";
        ResultSet rs2 = null;

        boolean b = false;

        try (
                PreparedStatement stmt = connection.prepareStatement(sql);
                PreparedStatement stmt2 = connection.prepareStatement(sql2);
        ){
            stmt.setInt(1, this.getIdPlat());
            rs = stmt.executeQuery();

            while (rs.next()) {
                stmt2.setInt(1, rs.getInt("idIngredient"));
                rs2 = stmt2.executeQuery();

                if (rs2.next())
                    b |= rs2.getBoolean("gluten");
            }

            return b;

        } catch (SQLException e) {
            System.err.println(e);
            return false;
        } finally {
            if (rs != null)
                rs.close();
            if (rs2 != null)
                rs2.close();

        }
    }


    public int getIdPlat() {
        return idPlat;
    }

    public void setIdPlat(int idPlat) {
        this.idPlat = idPlat;
    }

    public int getIdCategorie() {
        return idCategorie;
    }

    public void setIdCategorie(int idCategorie) {
        this.idCategorie = idCategorie;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public Date getDateIntronisation() {
        return dateIntronisation;
    }

    public void setDateIntronisation(Date dateIntronisation) {
        this.dateIntronisation = dateIntronisation;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getTempsPreparation() {
        return tempsPreparation;
    }

    public void setTempsPreparation(int tempsPreparation) {
        this.tempsPreparation = tempsPreparation;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public Blob getImage() {
        return image;
    }

    public void setImage(Blob image) {
        this.image = image;
    }

    public int getParallele() {
        return parallele;
    }

    public void setParallele(int parallele){
        this.parallele=parallele;
    }

    public List<Tag> getTags() throws SQLException {
        String sql = "SELECT tag.* FROM tag " +
                "JOIN tagged ON tag.idTag = tagged.idTag " +
                "WHERE tagged.idPlat = " + this.getIdPlat();

        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);

        List<Tag> list = new ArrayList<>();

        while (rs.next()) {
            list.add(TagManager.loadCurrentRowToObject(rs));
        }

        return list;
    }

    public int compareTo(Plat p) {
        try {
            if (Statistique.platCounterAll(this.getIdPlat()) < Statistique.platCounterAll(p.getIdPlat()))
                return -1;
            else if (Statistique.platCounterAll(this.getIdPlat()) > Statistique.platCounterAll(p.getIdPlat()))
                return 1;
            else if (Statistique.platCounterAll(this.getIdPlat()) == Statistique.platCounterAll(p.getIdPlat()))
                return 0;
            else
                return -1;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public List<Ingredient> getAllIngredients() throws SQLException {
        List<Ingredient> list = new ArrayList<>();

        String sql = "SELECT ingredient.* " +
                "FROM ingredient " +
                "NATURAL JOIN contient " +
                "WHERE contient.idPlat = " + this.idPlat;

        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);

        while (rs.next()) {
            list.add(IngredientManager.loadCurrentRowToObject(rs));
        }

        return list;
    }

    public double getCost() throws SQLException {
        String sql = "SELECT stock.prixAchat, contient.quantite " +
                "FROM stock " +
                "NATURAL JOIN contient " +
                "WHERE contient.idPlat = " + this.idPlat;

        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);

        double somme = 0;

        while (rs.next()) {
            somme += rs.getDouble("quantite") * rs.getDouble("prixAchat");
        }

        return somme;
    }

}