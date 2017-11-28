package koala.gui.caissier.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import koala.administration.Compte;
import koala.db.ConnectionManager;
import koala.gestionClient.Client;
import koala.gestionClient.ClientManager;
import koala.gestionCommandes.ModePaiement;
import koala.gestionCommandes.commande.Commande;
import koala.gestionCommandes.commande.CommandeManager;
import koala.gestionCommandes.commandeUnitaire.CommandeUnitaire;
import koala.gestionEmployes.Caissier;
import koala.gestionEmployes.EmployeManager;
import koala.gestionPlats.plat.Plat;
import koala.gestionPlats.plat.PlatManager;
import koala.gui.cuisinier.controller.FormUIGenerator;
import koala.gui.login.controller.CompteUIGenerator;
import strings.French;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

public class Controller {
    static List<Pane> pagingList = new ArrayList<>();
    Button btn_close = new Button();

    public static Connection connection = ConnectionManager.getInstance().getConnection();
    static LocalDateTime lastUpdate;

    @FXML
    private VBox notificationCollection;
    @FXML
    private Button proposer;
    @FXML
    private Button signaler;
    @FXML
    private StackPane rootContainer;

    @FXML
    private HBox compteInfo;

    EventHandler rechercheAvanceeHandler;
    @FXML
    private VBox ceo;
    @FXML
    private VBox order;
    @FXML
    private VBox centerContainer;
    @FXML
    private Button recherchAvancee;
    @FXML
    private Button client;
    @FXML
    private Label commanderLog;
    @FXML
    private Button commander;
    @FXML
    private ComboBox<String> comboBox;
    @FXML
    private TilePane categories;
    @FXML
    private ImageView searchIco;
    @FXML
    private ImageView lines;
    ArrayList<Node> categoryBuckUp = new ArrayList<>();
    ArrayList<StackPane> platsFromSearch  = new ArrayList<>();
    @FXML
    private ImageView rollback;
    @FXML
    private HBox searchBar;
    @FXML
    private HBox searchKey;
    ArrayList<Node> toBeRemovedGroup = new ArrayList<>();
    GridPane advancedSearchGridPane;
    boolean inCategorie;
    Timer timer;

    EventHandler clientHandler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            client.setOnAction(null);

            searchKey.setTranslateY(13);
            if (searchKey.isDisabled()) {
                searchKey.setDisable(false);
                returnWithCloseASearchBtn();


            }


            ceo.setVisible(false);
            ceo.setManaged(false);
            searchIco.setVisible(false);
            searchIco.setManaged(false);



            TilePane tilePane = new TilePane();

            timer = new Timer();
            try {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ClientUIGenerator.showClientsStatus(client, clientHandler, centerContainer, tilePane, order, searchKey, pagingList, timer, ceo, searchIco);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }).start();
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        try {
//                                ClientUIGenerator.syncClientStat(tilePane);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    };

    @FXML
    public void initialize() throws SQLException {

        lastUpdate = LocalDateTime.MIN;

//_________________________________init ui with categories_________________________________

        CategorieUIGenerateur.initCategories(centerContainer, categories, order, searchKey, pagingList);

//_________________________________ init rollback _________________________________

        rollback.setOnMouseClicked(event -> {
            searchKey.setTranslateY(0);


            if (timer != null) {
                timer.cancel();
                timer.purge();
            }


            ceo.setVisible(true);
            ceo.setManaged(true);
            searchIco.setVisible(true);
            searchIco.setManaged(true);

            recherchAvancee.getStyleClass().remove("selected");
            searchIco.setVisible(true);

            if (order.getChildren().size() > 0) {
                order.getParent().setVisible(true);
                order.getParent().setManaged(true);
            }

            pagingList.get(0).setVisible(true);
            pagingList.get(0).setManaged(true);
            for (int i = 1; i < pagingList.size(); i++) {

                centerContainer.getChildren().remove(pagingList.get(i));

            }
            pagingList.clear();
            pagingList.add(categories);

            // style advanced search
            recherchAvancee.setUnderline(false);
            recherchAvancee.setOnMouseEntered(e -> {
                recherchAvancee.setUnderline(true);
            });
            recherchAvancee.setOnMouseExited(e -> {
                recherchAvancee.setUnderline(false);
            });
            ((HBox) recherchAvancee.getParent()).getChildren().remove(btn_close);

            recherchAvancee.setOnMouseClicked(rechercheAvanceeHandler);

            searchKey.getChildren().clear();


            client.setOnAction(clientHandler);


        });

        Image row_ico = new Image("/img/row.png");
        Image row_ico_hover = new Image("/img/row-hover.png");
        rollback.setOnMouseEntered(event -> {
            rollback.setImage(row_ico_hover);
        });
        rollback.setOnMouseExited(event -> {
            rollback.setImage(row_ico);

        });

        Image lines_ico = new Image("/img/lines.png");
        Image lines_ico_hover = new Image("/img/lines-hover.png");
        comboBox.focusedProperty().addListener(observable -> {
            lines.setImage(lines_ico_hover);
        });
//_________________________________search_________________________________

        searchIco.setOnMouseClicked(event -> {


            searchFromMultiEvent();

        });

        comboBox.setOnKeyPressed(event -> {
            System.err.println("editor fired");
            if (event.getCode().equals(KeyCode.ENTER)) {
                searchFromMultiEvent();

            }
        });

        Image ico = new Image("/img/recherche.png");
        Image ico_hover = new Image("/img/recherche-hover.png");
        searchIco.setOnMouseEntered(event -> {
            searchIco.setImage(ico_hover);
        });
        searchIco.setOnMouseExited(event -> {
            searchIco.setImage(ico);

        });

        //set auto-compliment for simple search
        for (Plat plat :
                PlatManager.loadTableToList()) {
            comboBox.getItems().add(plat.getIdPlat() + " " +plat.getNom());
        }
        FxUtilTest.autoCompleteComboBoxPlus(comboBox, (typedText, itemToCompare) -> itemToCompare.toLowerCase().contains(typedText.toLowerCase()) || itemToCompare.equals(typedText));
        FxUtilTest.getComboBoxValue(comboBox);

//_________________________________recherche avancee_________________________________

        // style advanced search
        recherchAvancee.setText(French.CAISSIER_RECHERCHE_AVANCEE);
        recherchAvancee.setOnMouseEntered(event -> {
            recherchAvancee.setUnderline(true);
        });
        recherchAvancee.setOnMouseExited(event -> {
            recherchAvancee.setUnderline(false);
        });


        rechercheAvanceeHandler = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                searchKey.setDisable(true);
                searchIco.setVisible(false);
//                comboBox.getItems().clear();

                recherchAvancee.setUnderline(true);
                recherchAvancee.getStyleClass().add("selected");



                String categorie_nom = null;
                if (searchKey.getChildren().size() > 0) {
                    categorie_nom = ((Label) (((HBox) searchKey.getChildren().get(0))).getChildren().get(0)).getText();
                }

                recherchAvancee.setOnMouseEntered(null);
                recherchAvancee.setOnMouseExited(null);

                btn_close.getStyleClass().add("close_enCours_");
                btn_close.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                //TODO return from advanced search
                                returnWithCloseASearchBtn();
                            }

                        });
                ((HBox) recherchAvancee.getParent()).getChildren().add(btn_close);

                pagingList.get(pagingList.size() - 1).setVisible(false);
                pagingList.get(pagingList.size() - 1).setManaged(false);
                try {
                    advancedSearchGridPane = (GridPane) ((Group) FXMLLoader
                            .load(getClass().getResource("/fxml/rechercheAvancee.fxml"))).getChildren().get(0);

                    ((CheckBox)  advancedSearchGridPane.getChildren().get(22)).setOnAction(event1 -> {
                        if (
                                ((CheckBox)  advancedSearchGridPane.getChildren().get(22)).isSelected()
                                )
                            ((CheckBox)  advancedSearchGridPane.getChildren().get(22)).setText("oui");
                        else
                            ((CheckBox)  advancedSearchGridPane.getChildren().get(22)).setText("non");
                    });


                    centerContainer.getChildren().add(advancedSearchGridPane);
                    pagingList.add(advancedSearchGridPane);
                } catch (IOException e) {
                    e.printStackTrace();
                }


                String finalCategorie_nom = categorie_nom;
                ((HBox) ((GridPane) centerContainer.getChildren().get(centerContainer.getChildren().size() - 1)).getChildren()
                        .get(((GridPane) centerContainer.getChildren().get(centerContainer.getChildren().size() - 1)).getChildren().size() -1)).getChildren().get(0)
                        .setOnMouseClicked(new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent event) {
                                centerContainer.getChildren().remove(pagingList.get(pagingList.size() - 1));

                                try {

                                    TilePane searchedPlatsTilePane = new TilePane();

                                    pagingList.get(pagingList.size() - 1).setVisible(false);
                                    pagingList.get(pagingList.size() - 1).setManaged(false);
                                    pagingList.add(searchedPlatsTilePane);
                                    PlatUIGenerateur.appendPlatsFromAdvanced(centerContainer, searchedPlatsTilePane, order, advancedSearchGridPane, comboBox, pagingList, finalCategorie_nom);
                                    centerContainer.getChildren().add(searchedPlatsTilePane);

                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }


                            }
                        });

                recherchAvancee.setOnMouseClicked(null);

            }

//            }

        };
        recherchAvancee.setOnMouseClicked(rechercheAvanceeHandler);


//_________________________________client_________________________________

//        client.setText(French.CAISSIER_CLIENT);

        client.setOnAction(clientHandler);
//_________________________________commander_________________________________

        commander.setText(French.CAISSIER_COMMANDER);
        Timer t = new Timer();
        commander.setOnAction( event ->  {

            if (true) {
                commanderLog.setText(null);
                commanderLog.setGraphic(null);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Commande.setMaxId();
                            Commande commande = CommandeManager.Commander(OrderUIGenerateur.commandeUnitaires, 1, true, ModePaiement.BON);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        OrderUIGenerateur.commandeUnitaires.clear();
                    }
                }).start();

                order.getChildren().clear();
                order.getParent().setVisible(false);
                order.getParent().setManaged(false);
            } else {
                commander.setStyle(" -fx-background-color: orangered; -fx-text-fill: white; -fx-padding: 15 25; -fx-font-size: 18;");
                commanderLog.setText("stock insuffisant !");
                commanderLog.setGraphic(new ImageView(new Image("/img/error.png")));
                t.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        javafx.application.Platform.runLater(new Runnable() {
                            @Override
                            public void run() {

//                                    commanderLog.setText(null);
//                                    commanderLog.setGraphic(null);
                                commander.setStyle("-fx-padding: 15 25; -fx-background-color:  rgba(255,145,0,.8); -fx-border-width: 1; -fx-border-color: transparent; -fx-text-fill: white; -fx-font-size: 18;");


                            }
                        });
                    }
                }, 1000 );

            };

        });


        FormUIGenerator.setProposerNdSignalerForms(signaler, proposer, null, null, rootContainer);


        try {
            Caissier caissier = (Caissier) EmployeManager.getEmloye(Compte.getIdCompte(), "caissier");
            CompteUIGenerator.soutSignIn(compteInfo, caissier, "caissier");

        } catch (SQLException e) {
            e.printStackTrace();
        }




        Timer mobileAppClientsNotification_timer = new Timer();

        mobileAppClientsNotification_timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                System.out.println(Timestamp.valueOf(lastUpdate));
                                try {
                                    VBox clientNotQueue = new VBox();
                                    HashMap<Integer, ArrayList<CommandeUnitaire>> hashMap = Caissier.commandeClients(lastUpdate);
                                    for (int idClient : hashMap.keySet()) {

                                        notificationCollection.setMaxSize(VBox.USE_PREF_SIZE, VBox.USE_PREF_SIZE);

                                        Button _close = new Button();
                                        _close.getStyleClass().add("close_enCours_");
                                        HBox closeWrapper = new HBox(_close);
                                        closeWrapper.setAlignment(Pos.BASELINE_RIGHT);
                                        Label clientCred = new Label(
                                                ClientManager.CurrentRowToObject(connection.createStatement().executeQuery("select * from client WHERE idClient = " + idClient)).getNom() +
                                                        " #" +
                                                        idClient
                                        );
                                        clientCred.setStyle("-fx-font-size: 16; -fx-text-fill: white; -fx-padding: 0 0 0 8; -fx-underline: true");



                                        VBox not = new VBox(
                                                closeWrapper,
                                                clientCred
                                        );

                                        int max = 3;
                                        for (CommandeUnitaire commandeUnitaire :Caissier.commandeClients(lastUpdate).get(idClient)) {
                                            Label content = new Label(
                                                    commandeUnitaire.getPlat().getNom()+ " * " + commandeUnitaire.getQuantite()
                                            );
                                            content.setStyle("-fx-font-size: 16; -fx-text-fill: white; -fx-padding: 0 0 0 8");
                                            not.getChildren().addAll(content);
                                            if (--max <1)
                                                break;
                                        }
                                        if (Caissier.commandeClients(lastUpdate).get(idClient).size() > 3) {
                                            Label content = new Label(
                                                    "plus ("+
                                                    Integer.toString(Caissier.commandeClients(lastUpdate).get(idClient).size() - 3)+
                                                    ")"
                                            );
                                            content.setStyle("-fx-font-size: 14; -fx-text-fill: #F2F2F2;  -fx-padding: 0 0 0 8");

                                            content.getStyleClass().add("commandeUnitaire-fin");
                                            not.getChildren().addAll(content);

                                        }





                                        not.setMaxSize(VBox.USE_PREF_SIZE, VBox.USE_PREF_SIZE);
                                        not.getStyleClass().add("notification");
                                        not.getStylesheets().add("/css/magasinier.css");

                                        clientNotQueue.getChildren().add( not );

                                        _close.setOnAction(event -> {
                                            notificationCollection.getChildren().remove(not);
                                            if (clientNotQueue.getChildren().size() > 0) {
                                                notificationCollection.getChildren().add( clientNotQueue.getChildren().get(0) );
                                            }

                                        });

                                        not.setOnMouseClicked(event -> {


                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {

                                                    Rectangle rectangle = new Rectangle(rootContainer.getWidth(), rootContainer.getHeight(), Color.valueOf("rgba(0, 0, 0, .5)"));
                                                    Button btnClosePop = new Button();
                                                    btnClosePop.getStyleClass().add("close_enCours_");
//                                                    Label popNotContent = new Label(
//                                                            clientCred.getText() + "\n"+
//                                                                   "tag"
//                                                    );
//                                                    popNotContent.getStyleClass().add("cred");
                                                    VBox clientContainer = null;
                                                    try {
                                                        Client client = ClientManager.CurrentRowToObject(connection.createStatement().
                                                                executeQuery("select * from client WHERE idClient = " + idClient));

                                                        Label clientNom = new Label(
                                                                client.getNom()
                                                        );
                                                        clientNom.setStyle("-fx-font-size: 24; -fx-text-fill: #4D4D4D");
                                                        Label clientId = new Label(
                                                                "id #" +
                                                                Integer.toString(client.getIdClient())
                                                        );
                                                        clientId.setStyle("-fx-font-size: 16; -fx-text-fill: #4D4D4D");

                                                        Label clientEmail = new Label(
                                                                client.getEmail()
                                                        );
                                                        clientId.setStyle("-fx-font-size: 18; -fx-text-fill: #4D4D4D");

                                                        clientContainer = new VBox(clientNom, clientId,clientEmail);
                                                        clientContainer.setStyle("-fx-padding: 15");


                                                    } catch (SQLException e) {
                                                        e.printStackTrace();
                                                    }

                                                    VBox popCore = new VBox(clientContainer);
                                                    popCore.getStyleClass().add("popCore");

                                                    double somme = 0;
                                                    for (CommandeUnitaire commandeUnitaire :
                                                            hashMap.get(idClient)) {

                                                        somme += commandeUnitaire.getPlat().getPrix()*commandeUnitaire.getQuantite();

                                                        Label commandeUnitaireNameLabel = new Label(
                                                                commandeUnitaire.getPlat().getNom()
                                                        );
                                                        commandeUnitaireNameLabel.setStyle("-fx-font-size: 21; -fx-text-fill: #4D4D4D");

                                                        Label commandeUnitaireQuantityLabel = new Label(

                                                                Integer.toString(commandeUnitaire.getQuantite())+
                                                                " * "
                                                        );
                                                        commandeUnitaireQuantityLabel.setStyle("-fx-font-size: 24; -fx-text-fill: #4D4D4D");

                                                        Label commandeUnitairePrixLabel = new Label(
                                                                Double.toString(commandeUnitaire.getPlat().getPrix())
                                                                + " DA"
                                                        );
                                                        commandeUnitairePrixLabel.setStyle("-fx-font-size: 21; -fx-text-fill: #4D4D4D");

                                                        HBox commandeUnitaireContainer = new HBox(
                                                                15,
                                                                commandeUnitaireQuantityLabel,
                                                                commandeUnitaireNameLabel,
                                                                commandeUnitairePrixLabel
                                                        );
                                                        commandeUnitaireContainer.setStyle("-fx-alignment: center-left; -fx-text-alignment: left; -fx-border-width: 0 0 0 3; -fx-border-color: #4D4D4D; -fx-padding: 5 15");


                                                        popCore.getChildren().add(commandeUnitaireContainer);
                                                        popCore.setMinHeight(300);

                                                    }


                                                    Label sommeLabel = new Label(Double.toString(somme) + " DA");
                                                    sommeLabel.setStyle("-fx-alignment: center-left; -fx-text-alignment: left;-fx-padding: 5 175 5 5; -fx-text-fill: #4D4D4D; -fx-font-size: 26;");



                                                    Button btnValider = new Button("valider");


                                                    btnValider.setOnAction(event1 -> {
                                                        try {
                                                            Commande.setMaxId();
                                                            CommandeManager.Commander(hashMap.get(idClient), idClient, true, ModePaiement.BON);

                                                            notificationCollection.getChildren().remove(not);
                                                            if (clientNotQueue.getChildren().size() > 0) {
                                                                notificationCollection.getChildren().add( clientNotQueue.getChildren().get(0) );
                                                            }

//                                                            Caissier.NotificationManager.remove();




                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }

                                                        rootContainer.getChildren().remove(rootContainer.getChildren().size() -1);
                                                        rootContainer.getChildren().remove(rootContainer.getChildren().size() -1);
                                                    });


                                                    Button btnNRead = new Button("retarder");

                                                    btnNRead.setOnAction(event1 -> {
                                                        rootContainer.getChildren().remove(rootContainer.getChildren().size() -1);
                                                        rootContainer.getChildren().remove(rootContainer.getChildren().size() -1);
                                                    });

                                                    HBox actionBar = new HBox(sommeLabel, btnNRead, btnValider);
                                                    actionBar.getStyleClass().add("actionBar");
                                                    actionBar.getStylesheets().add("/css/magasinier.css");

//
//

                                                    VBox popNotification = new VBox(btnClosePop, popCore, actionBar);
                                                    popNotification.getStyleClass().add("popNotification");
                                                    rectangle.setOnMouseClicked(new EventHandler<MouseEvent>() {
                                                        @Override
                                                        public void handle(MouseEvent event) {
                                                            rootContainer.getChildren().remove(rootContainer.getChildren().size() -1);
                                                            rootContainer.getChildren().remove(rootContainer.getChildren().size() -1);
                                                        }
                                                    });
                                                    Platform.runLater(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            rootContainer.getChildren().add(rectangle);
                                                            rootContainer.getChildren().addAll(popNotification);
                                                        }
                                                    });

                                                    btnClosePop.setOnAction(new EventHandler<ActionEvent>() {
                                                        @Override
                                                        public void handle(ActionEvent event) {
                                                            rootContainer.getChildren().remove(rootContainer.getChildren().size() -1);
                                                            rootContainer.getChildren().remove(rootContainer.getChildren().size() -1);
                                                        }
                                                    });






                                                }
                                            }).start();



                                        });





                                    }
                                    if (clientNotQueue.getChildren().size() > 3) {
                                        notificationCollection.getChildren().addAll(clientNotQueue.getChildren().subList(0, 4 -notificationCollection.getChildren().size()));

                                    } else {
                                        System.out.println(clientNotQueue.getChildren().size() +"-----------------------------");
                                        for (int i = 0; i < 3; i++) {
                                            if (notificationCollection.getChildren().size()< 4 && clientNotQueue.getChildren().size() > 0)
                                                notificationCollection.getChildren().addAll(clientNotQueue.getChildren().get(0));
                                        }

                                    }



                                    lastUpdate = LocalDateTime.now();


                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            }
                        });


                    }
                }).start();
            }
        }, 0, 5000);




    }

    private void returnWithCloseASearchBtn() {
        centerContainer.getChildren().remove(advancedSearchGridPane);
        if (pagingList.get(pagingList.size() -1  )  instanceof GridPane) {

        } else {


            centerContainer.getChildren().remove(pagingList.size() -1);
            pagingList.remove(pagingList.size() -1);

        }
        centerContainer.getChildren().remove(advancedSearchGridPane);

        pagingList.remove(advancedSearchGridPane);

        pagingList.get(pagingList.size() -1).setManaged(true);
        pagingList.get(pagingList.size() -1).setVisible(true);
        searchKey.setDisable(false);
        searchIco.setVisible(true);
//                                closeSearche(btn_close);

        // style advanced search
        recherchAvancee.setUnderline(false);
        recherchAvancee.setOnMouseEntered(e -> {
            recherchAvancee.setUnderline(true);
        });
        recherchAvancee.setOnMouseExited(e -> {
            recherchAvancee.setUnderline(false);
        });
        recherchAvancee.getStyleClass().remove("selected");

        ((HBox) recherchAvancee.getParent()).getChildren().remove(btn_close);

        recherchAvancee.setOnMouseClicked(rechercheAvanceeHandler);
    }

    private void searchFromMultiEvent() {
        JTilePane jTilePane = null;
        searchKey.setDisable(true);

        if ((pagingList.get(pagingList.size()-1) instanceof JTilePane)) {
            jTilePane = (JTilePane) pagingList.get(pagingList.size()-1);
            jTilePane.getChildren().clear();
        } else {
            if (pagingList.size()> 0) {
                pagingList.get(pagingList.size()-1).setVisible(false);
                pagingList.get(pagingList.size()-1).setManaged(false);
                jTilePane = new JTilePane();
                centerContainer.getChildren().add(jTilePane);
                pagingList.add(jTilePane);

            }
            else return;
        }



        try {
            PlatUIGenerateur platUIGenerateur = new PlatUIGenerateur();
            platUIGenerateur.appendPlatsFromSearchByString(jTilePane, order, PlatManager.stringWithoutDigitsAndLeadingOrTrailingWhitespace(comboBox.getEditor().getText()) );


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
