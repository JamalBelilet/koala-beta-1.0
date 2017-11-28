package koala.gui.cuisinier.controller;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import koala.administration.Compte;
import koala.db.ConnectionManager;
import koala.gestionCommandes.Etat;
import koala.gestionCommandes.commande.Commande;
import koala.gestionCommandes.commande.CommandeManager;
import koala.gestionCommandes.commandeUnitaire.CommandeUnitaire;
import koala.gestionCommandes.commandeUnitaire.CommandeUnitaireManager;
import koala.gestionEmployes.Cuisiner;
import koala.gestionEmployes.EmployeManager;
import strings.French;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandeUIGenerator {
    static Map<CommandeUnitaire, Pane> encoursCU = new HashMap<>();
    public static Connection connection = ConnectionManager.getInstance().getConnection();
    static LocalDateTime lastUpdate = LocalDateTime.MIN;



    public static void syncCommandes(VBox queue, FlowPane enCours) throws SQLException {
        Map<CommandeUnitaire, Pane> queueCU = new HashMap<>();


        ResultSet resultSet = connection.createStatement().executeQuery("SELECT * from cuisiner WHERE idCuisiner = "+ Compte.getIdCompte());

        resultSet.next();

        List<CommandeUnitaire> commandeUnitaires = new ArrayList<>();

        try {

            Cuisiner cuisinier =
                    ((Cuisiner) EmployeManager.loadCurrentRowToObject(resultSet, "cuisiner"));
            System.out.println("smth has been updated" + cuisinier.commandeCourante.size() + " | " + cuisinier.commandeEnAttente.size());

            LocalDateTime lastUpdateForCalc = lastUpdate;
            Commande commande = null;

            commandeUnitaires.clear();


            for (CommandeUnitaire commandeUnitaire :
                    cuisinier.commandeCourante) {
                commande = CommandeManager.getCommande(commandeUnitaire.getIdCommande());
                if (commande.getDateCommande().isBefore(lastUpdate)
                        || commande.getDateCommande().isEqual(lastUpdate) )
                    continue;

                if (commande.getDateCommande().isAfter(lastUpdateForCalc))
                    lastUpdateForCalc = commande.getDateCommande();

                commandeUnitaires.add(commandeUnitaire);
            }

            for (CommandeUnitaire commandeUnitaire :
                    cuisinier.commandeEnAttente) {
                commande = CommandeManager.getCommande(commandeUnitaire.getIdCommande());
                if (
                        ( commande.getDateCommande().isBefore(lastUpdate)
                                || commande.getDateCommande().isEqual(lastUpdate) )
                        && commandeUnitaire.getEtat() != Etat.A)
                    continue;

                if (commande.getDateCommande().isAfter(lastUpdateForCalc))
                    lastUpdateForCalc = commande.getDateCommande();

                System.err.println(commandeUnitaire.getEtat());
                if (commandeUnitaire.miniStockSuffisantCommande() && commandeUnitaire.getEtat() == Etat.A ) {
                    commandeUnitaire.setEtat(Etat.T);
                    CommandeUnitaireManager.objectToRow(commandeUnitaire);
                }
                if (commandeUnitaire.getEtat() != Etat.A) {
                    commandeUnitaires.add(commandeUnitaire);
                }

//                commandeUnitaires.add(commandeUnitaire);
            }


            lastUpdate = lastUpdateForCalc;
            System.out.println(lastUpdate);




//            commandeUnitaires.addAll(cuisinier.commandeCourante);
        } catch (Exception ex) {

            ex.printStackTrace();
        }




        Platform.runLater(new Runnable() {
            @Override
            public void run() {
//                queue.getChildren().clear();

            }
        });
        System.out.println("queue has been cleared " + commandeUnitaires.size());
        int ident= 60, initWidth= 265, initHeight= 42;
        final int fIdent = ident;

        for (CommandeUnitaire cu :
                commandeUnitaires) {
            HBox inQueue = new HBox(new Label(Integer.toString(cu.getQuantite()) + " * "), new Label(cu.getPlat().getNom()));
            inQueue.getStyleClass().add("inQueue");
            if (cu.getEtat() == Etat.E) {
                inQueue.getStyleClass().add("e");
            }
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    queue.getChildren().add(inQueue);


                }
            });
            queueCU.put(cu, inQueue);

            appendPreview(queue, inQueue, enCours, cu, queueCU, encoursCU);

        }

        if (queue.getChildren().size() <= 0) {
            queue.setVisible(false);
            queue.setManaged(false);
        } else {
            queue.setVisible(true);
            queue.setManaged(true);
        }
        for (CommandeUnitaire eCU :
                encoursCU.keySet()) {
            boolean te= false;


            System.out.println(encoursCU.size());
            System.out.println(queueCU.size());

            for (CommandeUnitaire qCU :
                    queueCU.keySet()) {
                if (qCU.equals(eCU) ) {
                    queueCU.get(qCU).getStyleClass().add("selected");
                }
            }


        }

    }
    private static void appendPreview(VBox queue, Node cu, FlowPane enCours, CommandeUnitaire commandeUnitaires, Map<CommandeUnitaire, Pane> queueCU, Map<CommandeUnitaire, Pane> encoursCU) {

        cu.setOnMouseClicked(event -> {

            if (cu.getStyleClass().contains("selected"))
                return;

            if (enCours.getChildren().size()== 2) {


                for (CommandeUnitaire cl :
                        encoursCU.keySet()) {
                    if (encoursCU.get(cl).equals(enCours.getChildren().get(0))) {

                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                encoursCU.remove(cl);
                            }
                        });

                        for (CommandeUnitaire lj :
                                queueCU.keySet()) {
                            if (cl.equals(lj))
                                queueCU.get(lj).getStyleClass().remove("selected");

                        }
                    }
                }
                enCours.getChildren().remove(0);

            }
            VBox inEnCours = new VBox();
            cu.getStyleClass().add("selected");



            Button btnClose = new Button("");
            btnClose.getStyleClass().add("close_enCours_");
            btnClose.setOnAction(event1 -> {

                enCours.getChildren().remove(inEnCours);
                encoursCU.remove(commandeUnitaires);
                queueCU.get(commandeUnitaires).getStyleClass().remove("selected");


            });
            HBox close = new HBox(btnClose);
            close.setAlignment(Pos.CENTER_RIGHT);

            ImageView platImageView = null;
            try {
                platImageView = new ImageView(new Image(commandeUnitaires.getPlat().getImage().getBinaryStream()));
                platImageView.setPreserveRatio(true);
                platImageView.setFitWidth(250);
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();

            }
            Label platNameLabel = new Label(commandeUnitaires.getPlat().getNom());
            platNameLabel.setStyle("-fx-font-size: 20; -fx-text-fill: #222222; -fx-padding: 0 0 15 5");
//            String platIInfoText = commandeUnitaires.getplat.getDescription() != null ? "quantite : " + commandeUnitaires.quantite + "\n" + commandeUnitaires.plat.getDescription() : "quantite : " + commandeUnitaires.quantite + "\n";
            Label platQuantiteLabel = new Label( French.CUISINIER_QUANTITE + commandeUnitaires.getQuantite());
            platQuantiteLabel.setStyle("-fx-font-size: 16");
            Label platDescriptionLabel = new Label( French.CUISINIER_DESCRIPTION + "\n" + commandeUnitaires.getPlat().getDescription());
            platDescriptionLabel.setStyle("-fx-font-size: 16");

            VBox platInfo= new VBox(platNameLabel, platQuantiteLabel, platDescriptionLabel);
            if (platImageView != null) {
                platInfo.getChildren().add(1, platImageView);
            }
            platInfo.setAlignment(Pos.CENTER_LEFT);

            platInfo.setPrefHeight(350);

            Button btnValid = new Button(French.CUISINIER_VALIDER);
            btnValid.setDisable(true);

            if (commandeUnitaires.getEtat() == Etat.E)
                btnValid.setDisable(false);
            btnValid.getStyleClass().add("valider");
            btnValid.setOnAction(event1 -> {

                enCours.getChildren().remove(inEnCours);
                encoursCU.remove(commandeUnitaires);
                queueCU.get(commandeUnitaires).getStyleClass().remove("selected");

                queue.getChildren().remove(queueCU.get(commandeUnitaires));

                commandeUnitaires.valider();
                System.out.println("valider !");

            });
            Button btnPrepare = new Button(French.CUISINIER_PREPARER);
            btnPrepare.getStyleClass().add("valider");

            btnPrepare.setOnAction(event1 -> {
                cu.getStyleClass().add("e");
                btnPrepare.toBack();
                btnValid.setDisable(false);
                commandeUnitaires.prepare();

            });

            Button btnAnnuler = new Button(French.CUISINIER_ANNULER);
            btnAnnuler.getStyleClass().add("valider");
            btnAnnuler.setOnAction(event1 -> {
                cu.getStyleClass().remove("e");

                btnAnnuler.toBack();
                try {
                    commandeUnitaires.reaffecterCommande();
                } catch (Exception e) {
                    e.printStackTrace();
                }


                btnValid.setDisable(true);



                enCours.getChildren().remove(inEnCours);
                encoursCU.remove(commandeUnitaires);
                queueCU.get(commandeUnitaires).getStyleClass().remove("selected");



            });

            StackPane stackPane = new StackPane(btnAnnuler, btnPrepare);
            if (commandeUnitaires.getEtat() == Etat.E)
                btnPrepare.toBack();

            HBox actionBar = new HBox(btnValid, stackPane);
            actionBar.getStyleClass().add("actionBar");

            inEnCours.getStyleClass().add("inEnCours");

            inEnCours.getChildren().addAll(close, platInfo, actionBar);
            enCours.getChildren().add(inEnCours);
            encoursCU.put(commandeUnitaires, inEnCours);


        });

    }
}
