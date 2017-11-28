package koala.gui.caissier.controller;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import koala.gestionClient.Client;
import koala.gestionClient.ClientManager;
import koala.gestionCommandes.commande.Commande;
import koala.gestionCommandes.commande.CommandeManager;
import koala.gestionCommandes.commandeUnitaire.CommandeUnitaire;
import koala.gestionCommandes.commandeUnitaire.CommandeUnitaireManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import static koala.gui.caissier.controller.Controller.connection;

/**
 * Created by bjama on 3/18/2017.
 */
public class ClientUIGenerator {
    public static void showClientsStatus(Button client, EventHandler clientHandler, Pane centerContainer, Pane tilePane, VBox order, Pane searchKey, List<Pane> pagingList, Timer timer, VBox ceo, ImageView searchIco) throws Exception {
        client.setOnAction(clientHandler);

        tilePane.getStyleClass().add("clientCommandes");

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                centerContainer.getChildren().addAll(tilePane);

            }
        });

        ObservableList<Node> searchKeyRecycleBin = searchKey.getChildren();
        for (Node n :
                searchKeyRecycleBin) {
            n.setVisible(false);
            n.setManaged(false);
        }

        HBox clientKey = new HBox();
        ImageView closeClientKey = new ImageView(new Image("/img/liteClose.png"));
        closeClientKey.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {




                for (Node n :
                        searchKeyRecycleBin) {
                    n.setVisible(true);
                    n.setManaged(true);
                }

                if (order.getChildren().size() > 0 ) {
                    order.getParent().setVisible(true);
                    order.getParent().setManaged(true);
                }

                searchKey.getChildren().remove(clientKey);



                centerContainer.getChildren().remove(tilePane);
                pagingList.remove(tilePane);

                pagingList.get(pagingList.size() - 1).setVisible(true);
                pagingList.get(pagingList.size() - 1).setManaged(true);

                timer.cancel();
                timer.purge();


                ceo.setVisible(true);
                ceo.setManaged(true);
                searchIco.setVisible(true);
                searchIco.setManaged(true);
                searchKey.setTranslateY(0);





            }
        });

        Label clientLabel = new Label(
                "Client"
        );

        clientKey.getChildren().addAll(clientLabel, closeClientKey);

        clientKey.getStyleClass().add("searchKey");


        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                searchKey.getChildren().add(clientKey);

            }
        });





        order.getParent().setVisible(false);
        order.getParent().setManaged(false);


        pagingList.get(pagingList.size() - 1).setVisible(false);
        pagingList.get(pagingList.size() - 1).setManaged(false);

        pagingList.add(tilePane);

//        try {
//            tilePane.getChildren().addAll(((Group) FXMLLoader
//                    .load(getClass().getResource("/fxml/commandsStatus.fxml"))).getChildren());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        syncClientStat(tilePane);







    }

    public static void syncClientStat(Pane tilePane) throws Exception {
        ArrayList<Commande> commandes = new ArrayList<>();
        try {
            commandes = CommandeManager.TableToListReverse();


        } catch (NullPointerException nullPoiterException) {
            System.err.println("NullPointerException: Machakil dial mekki !!!!!");
        }

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                tilePane.getChildren().clear();


            }
        });


        for (Commande commande :
                commandes) {



            GridPane commandeStatus = new GridPane();
            int i = 0;

            Label clientId = new Label("client #" +Integer.toString(commande.getIdCommande()));


            Client client = ClientManager.CurrentRowToObject(connection.createStatement().
                    executeQuery("select * from client WHERE idClient = " + commande.getIdClient()));

            if (commande.getIdClient() != 1) {
                clientId = new Label("client #" +Integer.toString(commande.getIdCommande()) + " ( " + client.getNom() + " )");

            }

            clientId.setStyle("-fx-font-size: 14");
            commandeStatus.add(clientId, 0, 0, 2, 1);
            for (CommandeUnitaire commandeUnitaire:
                    commande.commandeUnitaires) {

                i++;
                Label quantite = new Label(commandeUnitaire.getQuantite()+ " * ");
                Label platName = new Label(commandeUnitaire.getPlat().getNom());
                Label cookName = new Label(CommandeUnitaireManager.getCuisiner(commandeUnitaire.getIdCuisiner()).getNom());
//                Label cookingTime = new Label("5:30");


                commandeStatus.add(quantite, 0, i);
                commandeStatus.add(platName, 1, i);
                commandeStatus.add(cookName, 2, i);

            }
            StackPane stackPane = new StackPane(commandeStatus);

            if (commande.estPrete()) {
                stackPane.getStyleClass().add("encoure");


            };
            stackPane.getStyleClass().add("oneCommande");

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    tilePane.getChildren().add( stackPane );

                }
            });

        }
    }


}
