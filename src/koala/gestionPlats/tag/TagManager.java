package koala.gestionPlats.tag;


import koala.db.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TagManager {

    public static Connection connection = ConnectionManager.getInstance().getConnection();

    public static Tag getTag(int idTagParameter) throws SQLException {
        String sql = "SELECT * FROM tag WHERE idTag = ?";
        ResultSet rs = null;

        try (
                PreparedStatement stmt = connection.prepareStatement(sql);
        ){
            stmt.setInt(1, idTagParameter);
            rs = stmt.executeQuery();

            if (rs.next()) {
                Tag tag = new Tag();

                tag.setIdTag(idTagParameter);
                tag.setNom(rs.getString("nom"));
                tag.setDescription(rs.getString("description"));

                return tag;
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

    public static Tag getTag(String nomParameter) throws SQLException {
        String sql = "SELECT idTag FROM tag WHERE nom = ?";
        ResultSet rs = null;
        int id = -1;

        try (
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            stmt.setString(1, nomParameter);
            rs = stmt.executeQuery();

            if (rs.next())
                id = rs.getInt("idTag");

            return getTag(id);
        } catch (SQLException e) {
            System.err.println(e);
            return null;
        } finally {
            if (rs != null) {
                rs.close();
            }
        }
    }

    public static List<Tag> loadTableToList() throws SQLException {
        List<Tag> list = new ArrayList<>();

        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery("SELECT * FROM tag");

        while (rs.next()) {
            list.add(loadCurrentRowToObject(rs));
        }

        return list;
    }

    public static Tag loadCurrentRowToObject(ResultSet rs) throws SQLException {
        Tag tag = new Tag();

        tag.setIdTag(rs.getInt("idTag"));
        tag.setNom(rs.getString("nom"));
        tag.setDescription(rs.getString("description"));

        return tag;
    }

    public static boolean insert(Tag tag) throws SQLException {

        String sql = "INSERT INTO `tag`" +
                "(`nom`, `description`) " +
                "VALUES (?, ?)";

        ResultSet keys = null;
        try (
                PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ){
            stmt.setString(1, tag.getNom());
            stmt.setString(2, tag.getDescription());

            int affected = stmt.executeUpdate();

            if (affected == 1) {
                keys = stmt.getGeneratedKeys();
                keys.next();
                int newKey = keys.getInt(1);
                tag.setIdTag(newKey);
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

    public static boolean update(Tag tag) throws Exception {

        String sql = "UPDATE `tag` SET " +
                "`nom` = ?,`description` = ? " +
                "WHERE `idTag` = ?";
        try (
                PreparedStatement stmt = connection.prepareStatement(sql);
        ){
            stmt.setString(1, tag.getNom());
            stmt.setString(2, tag.getDescription());
            stmt.setInt(3, tag.getIdTag());

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

    public static boolean delete(int idTag) throws Exception {
        String sql = "DELETE FROM tag WHERE idTag = ?";

        try (
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            stmt.setInt(1, idTag);

            int affected = stmt.executeUpdate();
            return (affected == 1);

        } catch (SQLException e) {
            System.err.println(e);
            return false;
        }
    }



}