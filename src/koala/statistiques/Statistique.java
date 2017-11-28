package koala.statistiques;


import koala.db.ConnectionManager;
import koala.gestionPlats.plat.Plat;
import koala.gestionPlats.plat.PlatManager;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Statistique {
    public static Connection connection = ConnectionManager.getInstance().getConnection();


    public static int platCounterAll(int idPlat) throws SQLException {
        String sql = "SELECT commandeunitaire.quantite " +
                "FROM commandeunitaire " +
                "INNER JOIN commande ON commandeunitaire.idCommande = commande.idCommande " +
                "WHERE commandeunitaire.idPlat = " + idPlat;

        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);

        int somme = 0;

        while (rs.next()) {
            somme += rs.getInt("quantite");
        }

        return somme;
    }

    public static int platCounterInterval(int idPlat, LocalDateTime startingDate, LocalDateTime endingDate) throws SQLException {
        Timestamp sd = Timestamp.valueOf(startingDate);
        Timestamp ed = Timestamp.valueOf(endingDate);

        String sql = "SELECT commandeunitaire.*, commande.dateCommande " +
                "FROM commandeunitaire " +
                "INNER JOIN commande ON commandeunitaire.idCommande = commande.idCommande " +
                "WHERE commandeunitaire.idPlat = " + idPlat + " " +
                "AND commande.dateCommande <= CAST('" + ed + "' AS DATETIME) " +
                "AND commande.dateCommande >= CAST('" + sd + "' AS DATETIME)";

        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);

        int somme = 0;

        while (rs.next()) {
            somme += rs.getInt("quantite");
        }

        return somme;
    }

    public static int platCounterToday(int idPlat) throws SQLException {
        String sql = "SELECT commandeunitaire.*, commande.dateCommande " +
                "FROM commandeunitaire " +
                "INNER JOIN commande ON commandeunitaire.idCommande = commande.idCommande " +
                "WHERE commandeunitaire.idPlat = " + idPlat + " " +
                "AND DATE(dateCommande) = DATE(NOW())";

        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);

        int somme = 0;

        while (rs.next()) {
            somme += rs.getInt("quantite");
        }
//        statement.close();
//        rs.close();

        return somme;
    }

    public static int platCounterAday(int idPlat, LocalDateTime dateTime) throws SQLException {
        String sql = "SELECT commandeunitaire.quantite " +
                "FROM commandeunitaire " +
                "INNER JOIN commande ON commandeunitaire.idCommande = commande.idCommande " +
                "WHERE commandeunitaire.idPlat = " + idPlat + " " +
                "AND DATE(commande.dateCommande) = CAST('" + Timestamp.valueOf(dateTime) + "' AS DATE )";
//        System.out.println(Timestamp.valueOf(dateTime));

        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);

        int somme = 0;

        while (rs.next()) {
            somme += rs.getInt("quantite");
        }

        return somme;
    }

    public static int platCounterLastDays(int idPlat, int daysOffset) throws SQLException {
        String sql = "SELECT commandeunitaire.quantite " +
                "FROM commandeunitaire " +
                "INNER JOIN commande ON commandeunitaire.idCommande = commande.idCommande " +
                "WHERE commandeunitaire.idPlat = " + idPlat + " " +
                "AND DATE(dateCommande) <= DATE(NOW()) " +
                "AND DATE(dateCommande) >= SUBDATE(DATE(NOW()), INTERVAL " + daysOffset + " DAY)";

        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);

        int somme = 0;

        while (rs.next()) {
            somme += rs.getInt("quantite");
        }

        return somme;
    }


    public static int categorieCounterAll(int idCategorie) throws SQLException {
        String sql = "SELECT commandeunitaire.*, commande.dateCommande, plat.idCategorie " +
                "FROM commandeunitaire " +
                "INNER JOIN commande ON commandeunitaire.idCommande = commande.idCommande " +
                "INNER JOIN plat ON commandeunitaire.idPlat = plat.idPlat " +
                "WHERE plat.idCategorie = " + idCategorie;

        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);

        int somme = 0;

        while (rs.next()) {
            somme += rs.getInt("quantite");
        }

        return somme;
    }

    public static int categorieCouterInterval(int idCategorie, LocalDateTime startingDate, LocalDateTime endingDate) throws SQLException {
        Timestamp sd = Timestamp.valueOf(startingDate);
        Timestamp ed = Timestamp.valueOf(endingDate);

        String sql = "SELECT commandeunitaire.*, commande.dateCommande, plat.idCategorie " +
                "FROM commandeunitaire " +
                "INNER JOIN commande ON commandeunitaire.idCommande = commande.idCommande " +
                "INNER JOIN plat ON commandeunitaire.idPlat = plat.idPlat " +
                "WHERE plat.idCategorie = " + idCategorie + " " +
                "AND commande.dateCommande <= CAST('" + ed + "' AS DATETIME) " +
                "AND commande.dateCommande >= CAST('" + sd + "' AS DATETIME)";

        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);

        int somme = 0;

        while (rs.next()) {
            somme += rs.getInt("quantite");
        }

        return somme;
    }

    public static int categorieCounterToday(int idCategorie) throws SQLException {
        String sql = "SELECT commandeunitaire.*, commande.dateCommande, plat.idCategorie " +
                "FROM commandeunitaire " +
                "INNER JOIN commande ON commandeunitaire.idCommande = commande.idCommande " +
                "INNER JOIN plat ON commandeunitaire.idPlat = plat.idPlat " +
                "WHERE plat.idCategorie = " + idCategorie + " " +
                "AND DATE(dateCommande) = DATE(NOW())";

        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);

        int somme = 0;

        while (rs.next()) {
            somme += rs.getInt("quantite");
        }

        return somme;
    }

    public static int categorieCounterLastDays(int idCategorie, int daysOffset) throws SQLException {
        String sql = "SELECT commandeunitaire.*, commande.dateCommande, plat.idCategorie " +
                "FROM commandeunitaire " +
                "INNER JOIN commande ON commandeunitaire.idCommande = commande.idCommande " +
                "INNER JOIN plat ON commandeunitaire.idPlat = plat.idPlat " +
                "WHERE plat.idCategorie = " + idCategorie + " " +
                "AND DATE(dateCommande) <= DATE(NOW()) " +
                "AND DATE(dateCommande) >= SUBDATE(DATE(NOW()), INTERVAL " + daysOffset + " DAY)";

        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);

        int somme = 0;

        while (rs.next()) {
            somme += rs.getInt("quantite");
        }

        return somme;
    }


    public static double recetteInterval(LocalDateTime startingDate, LocalDateTime endingDate) throws SQLException {
        List<Plat> liste;
        liste = PlatManager.loadTableToList();

        double recette = 0;

        for (Plat p: liste
                ) {
            recette += p.getPrix() * Statistique.platCounterInterval(p.getIdPlat(), startingDate, endingDate);
        }

        return recette;
    }

    public static double recetteToday(List<Plat> liste) throws SQLException {

        double recette = 0;

        for (Plat p: liste
             ) {
            recette += p.getPrix() * Statistique.platCounterToday(p.getIdPlat());
        }

        return recette;
    }

    public static double recetteAday(LocalDateTime dateTime, List<Plat> liste) throws SQLException {

        double recette = 0;

        for (Plat p: liste
             ) {
            recette += p.getPrix() * Statistique.platCounterAday(p.getIdPlat(), dateTime);
        }

        return recette;
    }

    public static double recetteLastDays(int dayOffset) throws SQLException {
        List<Plat> liste;
        liste = PlatManager.loadTableToList();

        double recette = 0;

        for (Plat p: liste
             ) {
            recette += p.getPrix() * Statistique.platCounterLastDays(p.getIdPlat(), dayOffset);
        }

        return recette;
    }


    //compareTo(Plat p) method is defined in the class 'Plat'

    public static List<Plat> platTopInCategory(int idCategorie) throws SQLException {
        List<Plat> listeAll = PlatManager.loadTableToList();
        List<Plat> listeCategorie = new ArrayList<>();

        for (Plat p: listeAll
             ) {
            if (p.getIdCategorie() == idCategorie)
                listeCategorie.add(p);
        }

        Collections.sort(listeCategorie, Collections.reverseOrder());

        return listeCategorie;
    }

    public static List<Plat> platTopSubInCategory(int idCategorie, int fromIndex, int toIndex) {
        try {
            return Statistique.platTopInCategory(idCategorie).subList(fromIndex, toIndex);
        } catch (Exception e) {
            System.err.println("ghiles exception !");
        }

        return null;
    }

}
