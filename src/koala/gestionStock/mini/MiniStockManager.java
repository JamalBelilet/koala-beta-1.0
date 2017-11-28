package koala.gestionStock.mini;

import koala.db.ConnectionManager;
import koala.gestionCommandes.commandeUnitaire.CommandeUnitaire;
import koala.gestionEmployes.Magasinier;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by Asus on 4/4/2017.
 */
public class MiniStockManager {
    public static Connection connection = ConnectionManager.getInstance().getConnection();

    public static void updateIngredient(int idingredient, int idCuisinier, int qte) {
        try {


            Statement statement = connection.createStatement();
            statement.executeUpdate("Update miniStock set quantite="+qte+" where idIngredient ="+idingredient+" and idCuisiner = "+idCuisinier);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private static IngredientAndQuantite currentRowToObject(ResultSet rs) throws Exception {

        return new IngredientAndQuantite(
                rs.getInt("miniStock.idIngredient"),
                rs.getInt("miniStock.quantite")
        );

    }


    public static MiniStock getMiniStock(int idCuisinier) {
        MiniStock miniStock = new MiniStock();
        miniStock.setIdCuisinier(idCuisinier);

        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM miniStock WHERE idCuisiner = ?");
            statement.setInt(1, idCuisinier);

            ResultSet resultSet= statement.executeQuery();

            while (resultSet.next()) {
                miniStock.ingredientAndQuantites.add(currentRowToObject(resultSet));
            }


        } catch (Exception ex) {

            ex.printStackTrace();
        }

        return miniStock;
    }

    public static void update( CommandeUnitaire commandeUnitaire) {

        MiniStock miniStock = MiniStockManager.getMiniStock(commandeUnitaire.getIdCuisiner());
        for (IngredientAndQuantite iq :
                commandeUnitaire.getPlat().ingredientAndQuantites) {
            for (IngredientAndQuantite iqAtMini :
                    miniStock.ingredientAndQuantites) {
                if (iq.getIdIngredient() == iqAtMini.getIdIngredient()) {
//                    iqAtMini.setQuantite(iqAtMini.getQuantite() - iq.getQuantite()* commandeUnitaire.getQuantite());
                    updateIngredient(iq.getIdIngredient(), commandeUnitaire.getIdCuisiner(), iqAtMini.getQuantite() - iq.getQuantite()* commandeUnitaire.getQuantite());


                    if (iqAtMini.getQuantite() - iq.getQuantite()* commandeUnitaire.getQuantite() < 0) {
                        Magasinier.Notification notification = new Magasinier.Notification(-1, 1, iq.getIdIngredient(), commandeUnitaire.getIdCuisiner(), false, false);
//                        notification.setContent(
//                                "cuisinier : "
//                                + CommandeUnitaireManager.getCuisiner(commandeUnitaire.getIdCuisiner()).getNom()
//                                + " manque d ingredient : "
//                                + Ingredient.getIngredient(iq.getIdIngredient()).nom
//                                + " \n quantite actuelle : "
//                                + (iqAtMini.getQuantite() - iq.getQuantite()* commandeUnitaire.getQuantite())
//
//                        );
                        Magasinier.NotificationManager.loadObjectToRow(notification);





                                //TODO FIND THE ACTIVE MAG
                    }


                }
            }
        }

        //TODO negative result handling;
    }


}
