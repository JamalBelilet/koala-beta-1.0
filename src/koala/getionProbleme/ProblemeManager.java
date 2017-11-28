package koala.getionProbleme;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import koala.db.ConnectionManager;


public class ProblemeManager {
	
	public static Connection connection = ConnectionManager.getInstance().getConnection();
	
    public static Probleme getProbleme(int idProblemeParameter) throws SQLException {
        String sql = "SELECT * FROM probleme WHERE idProbleme = ?";
        ResultSet rs = null;

        try (
                PreparedStatement stmt = connection.prepareStatement(sql);
        ){
            stmt.setInt(1, idProblemeParameter);
            rs = stmt.executeQuery();

            if (rs.next()) {
                Probleme pp = new Probleme();

                pp.setIdProbleme(idProblemeParameter);
                pp.setIdCompte(rs.getInt("idCompte"));
                pp.setDescription(rs.getString("description"));
                pp.setDegre(rs.getInt("degre"));
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

    private static Probleme loadCurrentRowToObject(ResultSet rs) throws SQLException {
        Probleme pp = new Probleme();

        pp.setIdProbleme(rs.getInt("idProbleme"));
        pp.setIdCompte(rs.getInt("idCompte"));
        pp.setDescription(rs.getString("description"));
        pp.setDegre(rs.getInt("degre"));
        pp.setDate(rs.getDate("date"));

        return pp;
    }
    
    public static List<Probleme> loadTableToList() throws SQLException {
        List<Probleme> list = new ArrayList<>();

        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery("SELECT * FROM probleme");

        while (rs.next()) {
            list.add(loadCurrentRowToObject(rs));
        }

        return list;
    }

    public static boolean insert(Probleme pp) throws SQLException {
        String sql = " INSERT INTO `probleme`" +
                "(`idCompte`, `description`, `degre`, `date`) " +
                "VALUES (?,?,?,?)";

        ResultSet keys = null;
        try (
                PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ){
            stmt.setInt(1, pp.getIdCompte());
            stmt.setString(2, pp.getDescription());
            stmt.setInt(3, pp.getDegre());
            stmt.setDate(4, pp.getDate());

            int affected = stmt.executeUpdate();

            if (affected == 1) {
                keys = stmt.getGeneratedKeys();
                keys.next();
                int newKey = keys.getInt(1);
                pp.setIdProbleme(newKey);
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

    public static boolean delete(int idProbleme) throws Exception {
        String sql = "DELETE FROM probleme WHERE idProbleme = ?";

        try (
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            stmt.setInt(1, idProbleme);

            int affected = stmt.executeUpdate();
            return (affected == 1);

        } catch (SQLException e) {
            System.err.println(e);
            return false;
        }
    }

}
