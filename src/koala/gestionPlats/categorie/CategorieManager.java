package koala.gestionPlats.categorie;


import koala.db.ConnectionManager;
import koala.gestionPlats.plat.PlatManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategorieManager {

    public static Connection connection = ConnectionManager.getInstance().getConnection();

    public static Categorie getCategorie(int idCategorieParameter) throws SQLException {
        String sql = "SELECT * FROM categorie WHERE idCategorie = ?";
        ResultSet rs = null;

        try (
                PreparedStatement stmt = connection.prepareStatement(sql);
        ){
            stmt.setInt(1, idCategorieParameter);
            rs = stmt.executeQuery();

            if (rs.next()) {
                Categorie categorie = new Categorie();

                categorie.setIdCategorie(idCategorieParameter);
                categorie.setNom(rs.getString("nom"));
                categorie.setImage(rs.getBlob("image"));
                categorie.setDescription(rs.getString("description"));

                return categorie;
            } else {
                return null;
            }

        } catch (SQLException e) {
            System.err.println(e);
            return null;
        } finally {
            if (rs != null) {
                rs.close();
            }
        }
    }

    public static Categorie getCategorie(String nomParameter) throws SQLException {
        String sql = "SELECT idCategorie FROM categorie WHERE nom = ?";
        ResultSet rs = null;
        int id = -1;

        try (
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            stmt.setString(1, nomParameter);
            rs = stmt.executeQuery();

            if (rs.next())
                id = rs.getInt("idCategorie");

            return getCategorie(id);
        } catch (SQLException e) {
            System.err.println(e);
            return null;
        } finally {
            if (rs != null) {
                rs.close();
            }
        }
    }

    public static List<Categorie> loadTableToList() throws SQLException {
        List<Categorie> list = new ArrayList<>();

        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery("SELECT * FROM categorie");

        while (rs.next()) {
            list.add(loadCurrentRowToObject(rs));
        }

        if (statement != null)
            statement.close();

        if (rs != null)
            rs.close();

        return list;
    }

    private static Categorie loadCurrentRowToObject(ResultSet rs) throws SQLException {
        Categorie categorie = new Categorie();

        categorie.setIdCategorie(rs.getInt("idCategorie"));
        categorie.setNom(rs.getString("nom"));
        categorie.setImage(rs.getBlob("image"));
        categorie.setDescription(rs.getString("description"));

        return categorie;
    }

    public static boolean insert(Categorie categorie) throws SQLException {
        String sql = "INSERT INTO `categorie`" +
                " (`nom`, `image`, `description`) " +
                "VALUES (?, ?, ?)";

        ResultSet keys = null;
        try (
                PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ){
            stmt.setString(1, categorie.getNom());
            stmt.setBlob(2, categorie.getImage());
            stmt.setString(3, categorie.getDescription());

            int affected = stmt.executeUpdate();

            if (affected == 1) {
                keys = stmt.getGeneratedKeys();
                keys.next();
                int newKey = keys.getInt(1);
                categorie.setIdCategorie(newKey);
            } else {
                System.err.println("No rows affected");
                return false;
            }
        } catch (SQLException e) {
            System.err.println(e);
            return false;
        } finally{
            if (keys != null) keys.close();
        }
        return true;
    }

    public static boolean update(Categorie categorie) throws Exception {

        String sql = "UPDATE `categorie` SET " +
                "`nom` = ?,`image` = ?,`description` = ? WHERE idCategorie = ?";
        try (
                PreparedStatement stmt = connection.prepareStatement(sql);
        ){
            stmt.setString(1, categorie.getNom());
            stmt.setBlob(2, categorie.getImage());
            stmt.setString(3, categorie.getDescription());
            stmt.setInt(4, categorie.getIdCategorie());

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

    public static boolean delete(int idCategorie) throws Exception {
        String sql = "DELETE FROM categorie WHERE idCategorie = ?";

        try (
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            stmt.setInt(1, idCategorie);

            int affected = stmt.executeUpdate();
            return (affected == 1);

        } catch (SQLException e) {
            System.err.println(e);
            return false;
        }
    }

    public static boolean deleteWell(int idCategorie, int idDefault) throws Exception {
        String sql = "UPDATE plat SET idCategorie = " + idDefault + " WHERE idCategorie = " + idCategorie;

        Statement s = connection.createStatement();
        s.executeUpdate(sql);

        return CategorieManager.delete(idCategorie);
    }

    public static boolean deleteWithPlats(int idCategorie) throws Exception {
        String sql = "SELECT idPlat FROM plat WHERE idCategorie = " + idCategorie;

        Statement s = connection.createStatement();
        ResultSet rs = s.executeQuery(sql);

        boolean has = false;

        while (rs.next()) {
            has |= PlatManager.hasCommandeUnitaire(rs.getInt("idPlat"));
        }

        if (has) {
            System.err.println("commandeUnitaire Probleme !");
            return false;
        }

        rs = s.executeQuery(sql);

        while (rs.next()) {
            PlatManager.deleteWell(rs.getInt("idPlat"));
        }

        return CategorieManager.delete(idCategorie);
    }

    public static boolean hasCommandeUnitaire(int idCategorie) throws SQLException {
        String sql = "SELECT idPlat FROM plat WHERE idCategorie = " + idCategorie;

        Statement s = connection.createStatement();
        ResultSet rs = s.executeQuery(sql);

        boolean has = false;

        while (rs.next()) {
            has |= PlatManager.hasCommandeUnitaire(rs.getInt("idPlat"));
        }

        return has;
    }

}
