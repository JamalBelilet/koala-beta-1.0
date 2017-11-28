package koala.gestionStock;


import koala.db.ConnectionManager;

import java.sql.*;
import java.time.LocalDate;



public class StockManager {

    public static Connection connection = ConnectionManager.getInstance().getConnection();



    public static int isDisponible (int idIngredient){

            try {
                Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery("SELECT quantite from Stock WHERE idIngredient =" + idIngredient);

                int c = rs.getInt("quantite");
                return c ;




            } catch (Exception ex) {
                ex.printStackTrace();
            }return 0;

    }

    public static void initialisation(int idingredient, int idMagasin, int qte) {
        try {


            Statement statement = connection.createStatement();
            statement.executeUpdate("Update stock set quantitie="+qte+" where idIngredient ="+idingredient+" and idMagasin = "+idMagasin);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }


    public static void update(int magasin, int idIngredient, int quantitie, double prixAchat, LocalDate datePeremption) {
        String sql =
                "UPDATE Stock SET " +
                        "quantitie = ?, prixAchat = ?, " +
                        "datePeremption = ?" +
                        "WHERE idMagasin= ? and idIngredient = ?";
        try (
                PreparedStatement stmt = connection.prepareStatement(sql);
        ){
            stmt.setInt(1, quantitie);
            stmt.setDouble(2, prixAchat);
            stmt.setDate(3, Date.valueOf(datePeremption));

            stmt.setInt(4, magasin);
            stmt.setInt(5, idIngredient);

            stmt.executeUpdate();
        }
        catch(SQLException e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
        }

    }




//    public static boolean update(Stock stock, int oldIdMag, int oldIdIng) throws Exception {
//
//        String sql =
//                "UPDATE Stock SET " +
//                        "idMagasin  = ?, idIngredient = ?, quantite = ?, prixAchat = ?, " +
//                        "datePeremption = ?" +
//                        "WHERE idMagasin= ? and idIngredient = ?";
//        try (
//                PreparedStatement stmt = connection.prepareStatement(sql);
//        ){
//            stmt.setInt(1, stock.getIdMagasin());
//            stmt.setInt(2, stock.getIdIngredient());
//            stmt.setInt(3, stock.getQuantitie());
//            stmt.setDouble(4, stock.getPrixAchat());
//            stmt.setDate(5, Date.valueOf(stock.getDatePeremption()));
//
//
//            stmt.setInt(6, oldIdMag);
//            stmt.setInt(7, oldIdIng);
//
//            int affected = stmt.executeUpdate();
//            if (affected == 1) {
//                return true;
//            } else {
//                return false;
//            }
//        }
//        catch(SQLException e) {
//            return false;
//        }
//    }




    public static void MAJStock(int idIngredient) {

        try {
            Statement statement = connection.createStatement();
            statement.execute("CREATE TRIGGER MAJminiStock_Stock AFTER UPDATE ON miniStock FOR EACH ROW "
                    + "BEGIN "
                    + "UPDATE Stock set Stock.quantitie =quantitie+quantite"//???? OLD quantite
                    + "WHERE idIngredient="+idIngredient+")"
                    + "END;");
            statement.execute("CREATE TRIGGER MAJminiStock_Stock AFTER UPDATE ON miniStock FOR EACH ROW "
                    + "BEGIN "
                    + "UPDATE Stock set Stock.quantitie =quantitie-NEW.quantite"//???? OLD quantite
                    + "WHERE idIngredient="+idIngredient+")"
                    + "END;");

        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }



//    public static ResultSet getStockJoinIngredient(int magasin) {
//
//        String sql =
//                "SELECT s.idMagasin, s.idIngredient, i.nom, s.quantitie, s.prixAchat, s.datePeremption, i.description, i.calorie, i.proteine, i.glucide, i.lipide, i.gluten FROM stock as s JOIN ingredient as i WHERE idMagasin = ? and s.idIngredient = i.idIngredient";
//        try (
//                PreparedStatement stmt = connection.prepareStatement(sql);
//        ){
//            stmt.setInt(1, magasin);
////            stmt.setInt(2, idIng);
//
//            ResultSet rs = stmt.executeQuery();
//
//            return rs;
//        }
//        catch(SQLException e) {
//            System.err.println(e);
//        }
//        return null;
//
//    }



    private static IngredientPropritiete currentRowToObject(ResultSet rs) throws Exception {

        return new IngredientPropritiete(
                rs.getInt("stock.idIngredient"),
                rs.getInt("stock.quantitie"),
                rs.getDouble("stock.prixAchat"),
                rs.getDate("stock.datePeremption").toLocalDate()
        );

    }


    public static Stock getStock(int idMagasin) {
        Stock stock = new Stock();
        stock.setIdMagasin(idMagasin);

        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM stock WHERE idMagasin = ?");
            statement.setInt(1, idMagasin);

            ResultSet resultSet= statement.executeQuery();

            while (resultSet.next()) {
                stock.ingredientPropreties.add(currentRowToObject(resultSet));
            }


        } catch (Exception ex) {

            ex.printStackTrace();
        }

        return stock;
    }



}