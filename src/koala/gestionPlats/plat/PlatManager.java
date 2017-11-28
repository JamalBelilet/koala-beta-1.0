package koala.gestionPlats.plat;

import koala.db.ConnectionManager;
import koala.gestionStock.Ingredient;
import koala.gestionStock.mini.IngredientAndQuantite;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlatManager {

    public static Connection connection = ConnectionManager.getInstance().getConnection();


    private static IngredientAndQuantite currentRowToObject(ResultSet rs) throws Exception {

        return new IngredientAndQuantite(
                rs.getInt("contient.idIngredient"),
                rs.getInt("contient.quantite")
        );

    }

    private static void initIngredients( Plat plat) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM contient WHERE idPlat = ?");
            statement.setInt(1, plat.getIdPlat());

            ResultSet resultSet= statement.executeQuery();

            plat.ingredientAndQuantites.clear();

            while (resultSet.next()) {
                plat.ingredientAndQuantites.add(currentRowToObject(resultSet));
            }


        } catch (Exception ex) {

            ex.printStackTrace();
        }

    }


    public static Plat getPlat(int idPlatParameter) throws SQLException {
        String sql = "SELECT * FROM plat WHERE idPlat = ?";
        ResultSet rs = null;

        try (
                PreparedStatement stmt = connection.prepareStatement(sql);
        ){
            stmt.setInt(1, idPlatParameter);
            rs = stmt.executeQuery();

            if (rs.next()) {
                Plat plat = new Plat();

                plat.setIdPlat(idPlatParameter);
                plat.setIdCategorie(rs.getInt("idCategorie"));
                plat.setNom(rs.getString("nom"));
                plat.setPrix(rs.getDouble("prix"));
                plat.setDateIntronisation(rs.getDate("dateIntronisation"));
                plat.setDescription(rs.getString("description"));
                plat.setImage(rs.getBlob("image"));
                plat.setTempsPreparation(rs.getInt("tempsPreparation"));
                plat.setDisponible(rs.getBoolean("disponible"));
                plat.setParallele(rs.getInt("parallele"));

                initIngredients(plat);

                return plat;
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

    public static Plat getPlat(String nomParameter) throws SQLException {
        String sql = "SELECT idPlat FROM plat WHERE nom = ?";
        ResultSet rs = null;
        int id = -1;

        try (
                PreparedStatement stmt = connection.prepareStatement(sql);
        ) {
            stmt.setString(1, nomParameter);
            rs = stmt.executeQuery();

            if (rs.next())
                id = rs.getInt("idPlat");

            return getPlat(id);
        } catch (SQLException e) {
            System.err.println(e);
            return null;
        } finally {
            if (rs != null) {
                rs.close();
            }
        }
    }

    public static List<Plat> loadTableToList() throws SQLException {
        List<Plat> list = new ArrayList<>();

        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery("SELECT * FROM plat");

        while (rs.next()) {
            list.add(loadCurrentRowToObject(rs));
        }

        if (statement != null)
            statement.close();

        if (rs != null)
            rs.close();

        return list;
    }

    public static List<Plat> loadTableToListDisponible() throws SQLException {
        List<Plat> list = new ArrayList<>();

        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery("SELECT * FROM plat WHERE plat.disponible = true");

        while (rs.next()) {
            list.add(loadCurrentRowToObject(rs));
        }

        return list;
    }

    public static List<Plat> loadTableToListIndisponible() throws SQLException {
        List<Plat> list = new ArrayList<>();

        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery("SELECT * FROM plat WHERE plat.disponible = false");

        while (rs.next()) {
            list.add(loadCurrentRowToObject(rs));
        }

        return list;
    }

    public static List<Plat> platsDisponiblesByCategorie(int idCategorieParameter) throws SQLException {
        List<Plat> list = new ArrayList<>();

        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery("SELECT * FROM plat WHERE plat.disponible = true " +
                "AND plat.idCategorie=" + idCategorieParameter);

        while (rs.next()) {
            list.add(loadCurrentRowToObject(rs));
        }

        return list;
    }

    public static List<Plat> platsIndisponiblesByCategorie(int idCategorieParameter) throws SQLException {
        List<Plat> list = new ArrayList<>();

        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery("SELECT * FROM plat WHERE plat.disponible = false " +
                "AND plat.idCategorie=" + idCategorieParameter);

        while (rs.next()) {
            list.add(loadCurrentRowToObject(rs));
        }

        return list;
    }

    private static Plat loadCurrentRowToObject(ResultSet rs) throws SQLException {
        Plat plat = new Plat();

        plat.setIdPlat(rs.getInt("idPlat"));
        plat.setIdCategorie(rs.getInt("idCategorie"));
        plat.setNom(rs.getString("nom"));
        plat.setPrix(rs.getDouble("prix"));
        plat.setDateIntronisation(rs.getDate("dateIntronisation"));
        plat.setDescription(rs.getString("description"));
        plat.setImage(rs.getBlob("image"));
        plat.setTempsPreparation(rs.getInt("tempsPreparation"));
        plat.setDisponible(rs.getBoolean("disponible"));

        return plat;
    }

    public static List<Plat> platsByCategorie(int idCategorieParameter) throws SQLException {
        List<Plat> list = new ArrayList<>();

        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery("SELECT * FROM plat WHERE plat.idCategorie=" + idCategorieParameter);

        while (rs.next()) {
            list.add(loadCurrentRowToObject(rs));
        }

        return list;
    }

    public static List<Plat> platsByPrice(double minPrice, double maxPrice) throws SQLException {
        List<Plat> list = new ArrayList<>();

        PreparedStatement statement = connection.prepareStatement("SELECT * FROM plat WHERE prix<=? " +
                                                                                            "AND prix>=?");
        statement.setDouble(1, maxPrice);
        statement.setDouble(2, minPrice);
        ResultSet rs = statement.executeQuery();

        while (rs.next()) {
            list.add(loadCurrentRowToObject(rs));
        }

        return list;
    }

    private static String requeteTags(String... tagParameters) {
        if (tagParameters.length == 0)
            return "SELECT * FROM plat";

        StringBuilder tags = new StringBuilder();

        for (String s: tagParameters)
            tags.append("'" + s + "'" + ",");

        if (tags.equals("") == false)
            tags.deleteCharAt(tags.length() - 1);

        String s = tags.toString();

        String sqlQuery =
                "SELECT p.* " +
                        "FROM plat p " +
                        "WHERE p.idPlat IN (SELECT tg.idPlat " +
                        "FROM tagged tg " +
                        "JOIN tag t ON t.idTag = tg.idTag " +
                        "WHERE t.nom IN (" +
                        s +
                        ") " + //tags liste String
                        "GROUP BY tg.idPlat " +
                        "HAVING COUNT(DISTINCT t.nom) = " + tagParameters.length +
                        ")"; //.length

        return sqlQuery;
    }

    public static List<Plat> platsByTags(String... tagParameters) throws SQLException {
        List<Plat> list = new ArrayList<>();

        String sqlQuery = requeteTags(tagParameters);

        ResultSet rs = null;
        try (
            Statement stmt = connection.createStatement();
                ) {
            rs = stmt.executeQuery(sqlQuery);

            while (rs.next()) {
                list.add(loadCurrentRowToObject(rs));
            }

            return list;

        } catch (SQLException e) {
            System.err.println(e);
            return null;

        } finally {
            if (rs != null) rs.close();
        }
    }

    public static boolean insert(Plat plat) throws SQLException {
        String sql = "INSERT INTO `plat`" +
                "(`idCategorie`, `nom`, `prix`, `dateIntronisation`, " +
                "`description`, `image`, `tempsPreparation`, `disponible`) " +
                "VALUES " +
                "(?,?,?,?,?,?,?,?)";

        ResultSet keys = null;
        try (
            PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ){
            stmt.setInt(1, plat.getIdCategorie());
            stmt.setString(2, plat.getNom());
            stmt.setDouble(3, plat.getPrix());
            stmt.setDate(4, plat.getDateIntronisation());
            stmt.setString(5, plat.getDescription());
            stmt.setBlob(6, plat.getImage());
            stmt.setDouble(7, plat.getTempsPreparation());
            stmt.setBoolean(8, plat.isDisponible());

            int affected = stmt.executeUpdate();

            if (affected == 1) {
                keys = stmt.getGeneratedKeys();
                keys.next();
                int newKey = keys.getInt(1);
                plat.setIdPlat(newKey);
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

    public static boolean update(Plat plat) throws Exception {

        String sql =
                "UPDATE plat SET " +
                        "idCategorie = ?, nom = ?, prix = ?, dateIntronisation = ?, " +
                        "description = ?, image = ?, tempsPreparation = ?, disponible = ? " +
                        "WHERE idPlat = ?";
        try (
                PreparedStatement stmt = connection.prepareStatement(sql);
        ){
            stmt.setInt(1, plat.getIdCategorie());
            stmt.setString(2, plat.getNom());
            stmt.setDouble(3, plat.getPrix());
            stmt.setDate(4, plat.getDateIntronisation());
            stmt.setString(5, plat.getDescription());
            stmt.setBlob(6, plat.getImage());
            stmt.setInt(7, plat.getTempsPreparation());
            stmt.setBoolean(8, plat.isDisponible());
            stmt.setInt(9, plat.getIdPlat());

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

    public static boolean delete(int idPlat) throws Exception {
        String sql = "DELETE FROM plat WHERE idPlat = ?";

        try (
                PreparedStatement stmt = connection.prepareStatement(sql);
                ) {
            stmt.setInt(1, idPlat);

            int affected = stmt.executeUpdate();
            return (affected == 1);

        } catch (SQLException e) {
            System.err.println(e);
            return false;
        }
    }

    public static List<Plat> platsMulticriteres(double minPrice, double maxPrice,
                                                double minCalorie, double maxCalorie,
                                                double minProteine, double maxProteine,
                                                double minGlucide, double maxGlucide,
                                                double minLipide, double maxLipide,
                                                int minTempsPreparation, int maxTempsPreparation,
                                                int idCategorie, boolean gluten,
                                                String... tagParameters) throws SQLException {
        List<Plat> list = new ArrayList<>();

        String sql = requeteTags(tagParameters);

        if (sql.equals("SELECT * FROM plat"))
            sql += " WHERE ";
        else
            sql += " AND ";

        if (idCategorie < 0)
            sql += "prix>=? AND prix<=? AND tempsPreparation>=? AND tempsPreparation<=?";
        else
            sql += "prix>=? AND prix<=? AND tempsPreparation>=? AND tempsPreparation<=? AND idCategorie=?";

        PreparedStatement statement = connection.prepareStatement(sql);

        statement.setDouble(1, minPrice);
        statement.setDouble(2, maxPrice);
        statement.setInt(3, minTempsPreparation);
        statement.setInt(4, maxTempsPreparation);
        if (idCategorie > 0)
             statement.setInt(5, idCategorie);

        ResultSet rs = statement.executeQuery();

        Plat p = new Plat();

        while (rs.next()) {
            p = PlatManager.getPlat(rs.getInt("idPlat"));

            /****** debugging code
            System.out.println("Hi");
            System.out.println(p);
             ******/

            if (
                    (p.getCalorie() >= minCalorie && p.getCalorie() <= maxCalorie)
                    && (p.getProteine() >= minProteine && p.getProteine() <= maxProteine)
                    && (p.getGlucide() >= minGlucide && p.getGlucide() <= maxGlucide)
                    && (p.getLipide() >= minLipide && p.getLipide() <= maxLipide)
                    && (p.withGluten() == gluten)
                    )
                list.add(loadCurrentRowToObject(rs));
        }

        return list;
    }

    public static List<Plat> platsMulticriteresWithString(double minPrice, double maxPrice,
                                                double minCalorie, double maxCalorie,
                                                double minProteine, double maxProteine,
                                                double minGlucide, double maxGlucide,
                                                double minLipide, double maxLipide,
                                                int minTempsPreparation, int maxTempsPreparation,
                                                int idCategorie, boolean gluten,
                                                String s,
                                                String... tagParameters) throws SQLException {
        List<Plat> list = new ArrayList<>();

        String sql = requeteTags(tagParameters);

        if (sql.equals("SELECT * FROM plat"))
            sql += " WHERE ";
        else
            sql += " AND ";

        if (idCategorie < 0)
            sql += "prix>=? AND prix<=? AND tempsPreparation>=? AND tempsPreparation<=?";
        else
            sql += "prix>=? AND prix<=? AND tempsPreparation>=? AND tempsPreparation<=? AND idCategorie=?";

        sql += " AND (nom LIKE '%" + s + "%' OR description LIKE '%" + s + "%')";

        PreparedStatement statement = connection.prepareStatement(sql);

        statement.setDouble(1, minPrice);
        statement.setDouble(2, maxPrice);
        statement.setInt(3, minTempsPreparation);
        statement.setInt(4, maxTempsPreparation);
        if (idCategorie > 0)
            statement.setInt(5, idCategorie);

        ResultSet rs = statement.executeQuery();

        Plat p = new Plat();

        while (rs.next()) {
            p = PlatManager.getPlat(rs.getInt("idPlat"));

            /****** debugging code
             System.out.println("Hi");
             System.out.println(p);
             ******/

            if (
                    (p.getCalorie() >= minCalorie && p.getCalorie() <= maxCalorie)
                            && (p.getProteine() >= minProteine && p.getProteine() <= maxProteine)
                            && (p.getGlucide() >= minGlucide && p.getGlucide() <= maxGlucide)
                            && (p.getLipide() >= minLipide && p.getLipide() <= maxLipide)
                            && (p.withGluten() == gluten)
                    )
                list.add(loadCurrentRowToObject(rs));
        }

        return list;
    }


    public static List<Plat> platsByString(String s) throws SQLException {
        List<Plat> list = new ArrayList<>();

        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery("SELECT * FROM plat WHERE nom LIKE '%" + s +
                "%' OR description LIKE '%" + s + "%'");

        while (rs.next()) {
            list.add(loadCurrentRowToObject(rs));
        }

        return list;
    }

    public static String stringWithoutDigitsAndLeadingOrTrailingWhitespace(String text) {
        String s = text;
        s = s.replaceAll("\\d","");
        s = s.replaceAll("^\\s+|\\s+$", "");

        return s;
    }

    public ArrayList<Ingredient> getIngredient(String nomPlat) throws SQLException {

        Statement stmt = null;
        ResultSet rs =null;
        try {
            stmt = connection.createStatement();
            rs = stmt.executeQuery("select * from plat WHERE nom=\""+nomPlat+"\"");


            int id=-1;
            if(rs.next()){
                id=rs.getInt("idPlat");
            }
            rs=stmt.executeQuery("select idIngredient,idPlat from contient WHERE idPlat=\""+id+"\"");
            ArrayList<Integer> list=new ArrayList<>();
            while(rs.next()){
                list.add(rs.getInt("idIngredient"));
            }

            rs=stmt.executeQuery("select * from Ingredient ");


            ArrayList<Ingredient> ingredients = new ArrayList<Ingredient>();

            while (rs.next()) {
                if(list.contains(rs.getInt("idIngredient"))){
                    Ingredient ingredient=new Ingredient();
                    ingredient.idIngredient = rs.getInt("idIngredient");
                    ingredient.nom = rs.getString("nom");
                    ingredient.calorie = rs.getDouble("calorie");
                    ingredient.proteine = rs.getDouble("proteine");
                    ingredient.description = rs.getString("description");
                    ingredient.lipide = rs.getDouble("lipide");
                    ingredient.glucide = rs.getDouble("glucide");
                    ingredient.gluten  = rs.getBoolean("gluten");
                    ingredients.add(ingredient);

                }
            }

            return ingredients;
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {

            if (rs!=null) {
                rs.close();
            }
            if(stmt!=null){
                stmt.close();
            }
        }

        return null;


    }

    public static boolean deleteWell(int idPlat) throws SQLException {
        String sql = "SELECT * FROM commandeunitaire WHERE idPlat = " + idPlat;

        Statement s = connection.createStatement();
        ResultSet rs = s.executeQuery(sql);

        if (rs.next()) {
            System.err.println("Ce plat est présent dans les commandes unitaires !");
            return false;
        }

        sql = "DELETE FROM tagged WHERE idPlat = ?";

        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, idPlat);
        stmt.executeUpdate();

        sql = "DELETE FROM contient WHERE idPlat = ?";

        stmt = connection.prepareStatement(sql);
        stmt.setInt(1, idPlat);
        stmt.executeUpdate();

        sql = "DELETE FROM plat WHERE idPlat = ?";

        stmt = connection.prepareStatement(sql);
        stmt.setInt(1, idPlat);
        int affected = stmt.executeUpdate();

        return (affected == 1);
    }

    public static boolean hasCommandeUnitaire(int idPlat) throws SQLException {
        String sql = "SELECT * FROM commandeunitaire WHERE idPlat = " + idPlat;

        Statement s = connection.createStatement();
        ResultSet rs = s.executeQuery(sql);

        if (rs.next())
            return true;

        return false;
    }


    //todo:select also by ingredient(s)
    //todo:this script work by only if a plat has at least one ingredient
    //todo:-impro matches ingredients -> contains() and does't contain.
    /**
    SELECT DISTINCT p.*
    FROM plat AS p INNER JOIN contient AS c On p.idPlat = c.idPlat
    INNER JOIN ingredient AS i On c.idIngredient = i.idIngredient
    WHERE i.nom LIKE '%zz%' OR p.nom LIKE '%zz%' OR p.description LIKE '%zz%';
    **/

}