package koala.gestionPlats.platProposer;

import koala.db.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlatProposerManager {

    public static Connection connection = ConnectionManager.getInstance().getConnection();

    public static PlatProposer getPlatProposer(int idPlatProposerParameter) throws SQLException {
        String sql = "SELECT * FROM platProposer WHERE idPlatProposer = ?";
        ResultSet rs = null;

        try (
                PreparedStatement stmt = connection.prepareStatement(sql);
        ){
            stmt.setInt(1, idPlatProposerParameter);
            rs = stmt.executeQuery();

            if (rs.next()) {
                PlatProposer pp = new PlatProposer();

                pp.setIdPlatProposer(idPlatProposerParameter);
                pp.setIdCompte(rs.getInt("idCompte"));
                pp.setNom(rs.getString("nom"));
                pp.setDescription(rs.getString("description"));
                pp.setImage(rs.getBlob("image"));
                pp.setDate(rs.getDate("date"));

                return pp;
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

    public static PlatProposer getPlatProposer(String nomParameter) throws SQLException {
        String sql = "SELECT idPlatProposer FROM platProposer WHERE nom = ?";
        ResultSet rs = null;
        int id = -1;

        try (
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            stmt.setString(1, nomParameter);
            rs = stmt.executeQuery();

            if (rs.next())
                id = rs.getInt("idPlatProposer");

            return getPlatProposer(id);
        } catch (SQLException e) {
            System.err.println(e);
            return null;
        } finally {
            if (rs != null) {
                rs.close();
            }
        }
    }

    public static List<PlatProposer> loadTableToList() throws SQLException {
        List<PlatProposer> list = new ArrayList<>();

        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery("SELECT * FROM platProposer");

        while (rs.next()) {
            list.add(loadCurrentRowToObject(rs));
        }

        return list;
    }

    private static PlatProposer loadCurrentRowToObject(ResultSet rs) throws SQLException {
        PlatProposer pp = new PlatProposer();

        pp.setIdPlatProposer(rs.getInt("idPlatProposer"));
        pp.setNom(rs.getString("nom"));
        pp.setDescription(rs.getString("description"));
        pp.setImage(rs.getBlob("image"));
        pp.setDate(rs.getDate("date"));
        pp.setIdCompte(rs.getInt("idCompte"));

        return pp;
    }

    public static boolean insert(PlatProposer pp) throws SQLException {
        String sql = " INSERT INTO `platProposer`" +
                "(`nom`, `description`, `image`, `date`, `idCompte`) " +
                "VALUES (?,?,?,?,?)";

        ResultSet keys = null;
        try (
                PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ){
            stmt.setString(1, pp.getNom());
            stmt.setString(2, pp.getDescription());
            stmt.setBlob(3, pp.getImage());
            stmt.setDate(4, pp.getDate());
            stmt.setInt(5, pp.getIdCompte());

            int affected = stmt.executeUpdate();

            if (affected == 1) {
                keys = stmt.getGeneratedKeys();
                keys.next();
                int newKey = keys.getInt(1);
                pp.setIdPlatProposer(newKey);
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

    public static boolean delete(int idPlatProposer) throws Exception {
        String sql = "DELETE FROM platProposer WHERE idPlatProposer = ?";

        try (
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            stmt.setInt(1, idPlatProposer);

            int affected = stmt.executeUpdate();
            return (affected == 1);

        } catch (SQLException e) {
            System.err.println(e);
            return false;
        }
    }

}