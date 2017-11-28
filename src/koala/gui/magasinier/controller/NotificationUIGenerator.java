package koala.gui.magasinier.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import koala.db.ConnectionManager;
import koala.gestionCommandes.commandeUnitaire.CommandeUnitaireManager;
import koala.gestionEmployes.Magasinier;
import koala.gestionStock.Ingredient;
import koala.gestionStock.IngredientPropritiete;
import koala.gestionStock.StockManager;
import koala.gestionStock.mini.IngredientAndQuantite;
import koala.gestionStock.mini.MiniStockManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

/**
 * Created by bjama on 3/21/2017.
 */
public class NotificationUIGenerator {
    static List<Magasinier.Notification> notifications;
    static List<Magasinier.Notification> allNotifications;
    static List<Magasinier.Notification> ReadNotifications;
    static TilePane tilePane = new TilePane();
    static boolean recAll = false;
    static Timestamp lastUpdate;
    static Timestamp lastUpdateFromDatabase = null;


    public static Connection connection = ConnectionManager.getInstance().getConnection();


    public static void syncNotifications(StackPane rootContainer, BorderPane container, VBox notificationCollection, Button btnViewAll, Button btnNotification) {
        try(
                ResultSet resultSet = connection.createStatement().executeQuery("SELECT * from updateLog WHERE tag = 'magNot'");

        ) {
            resultSet.next();
            lastUpdateFromDatabase = resultSet.getTimestamp("lastUpdate");
        } catch (SQLException e) {
            e.printStackTrace();
        }


        if (lastUpdate != null && lastUpdateFromDatabase != null) {
            if (lastUpdate.before(lastUpdateFromDatabase)) {

//                Platform.runLater(new Runnable() {
//                    @Override
//                    public void run() {
//                        notificationCollection.getChildren().clear();
//
//
//                    }
//                });
                lastUpdate = lastUpdateFromDatabase;

                innerSync(rootContainer, container, notificationCollection, btnViewAll, btnNotification, true);

                System.err.println("---- before the problem");

            }
            System.err.println("this is the problem");
        } else {

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    notificationCollection.getChildren().clear();
                    lastUpdate = lastUpdateFromDatabase;


                }
            });

            innerSync(rootContainer, container, notificationCollection, btnViewAll, btnNotification, false);

        }

    }

    public static void innerSync(StackPane rootContainer, BorderPane container, VBox notificationCollection, Button btnViewAll, Button btnNotification, boolean update) {
        try {
            if (update) {
                notifications = Magasinier.NotificationManager.loadTableToList("SELECT * FROM magasinierNotification ORDER BY idmagasinierNotification DESC LIMIT 1;  ");
            } else
                notifications = Magasinier.NotificationManager.loadNReadNIgnoreTableToList();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        for (Magasinier.Notification notification :
                notifications) {

            if (notification.isRead())
                continue;
            if (notification.isIgnore())
                continue;

            if (!recAll ){
                container.getLeft().setVisible(true);
                container.getLeft().setManaged(true);
            }

            Button btnClose = new Button();
            btnClose.getStyleClass().add("close_enCours_");

            btnClose.setVisible(false);

            HBox closeWrapper = new HBox(btnClose);
            closeWrapper.setAlignment(Pos.BASELINE_RIGHT);



            Label lblContent = getNotificationCoreLabel(notification);
            lblContent.getStyleClass().add("enAtt");
            HBox contentWrapper = new HBox(lblContent);
            contentWrapper.setSpacing(16);


            VBox miniNot = new VBox(closeWrapper, contentWrapper);

            miniNot.setOnMouseEntered(event -> {
                btnClose.setVisible(true);

            });
            miniNot.setOnMouseExited(event -> {
                btnClose.setVisible(false);

            });

            miniNot.getStyleClass().add("notification");
            miniNot.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                notificationCollection.getChildren().remove(miniNot);
                if (notificationCollection.getChildren().size() ==0 ){
                    container.getLeft().setVisible(false);
                    container.getLeft().setManaged(false);
                }
            });

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    if (notificationCollection.getChildren().size() < 5) {

                        notificationCollection.getChildren().add(0, miniNot);
                    }

                }
            });


            appendActionToNot(rootContainer, container, notificationCollection, btnViewAll, btnNotification, miniNot,  null, notification);


            btnClose.setOnAction(event1 -> {


                notification.setIgnore(true);
                notificationCollection.getChildren().remove(miniNot);
                if (notificationCollection.getChildren().size() ==0 ){
                    container.getLeft().setVisible(false);
                    container.getLeft().setManaged(false);
                }
                innerSync(rootContainer, container, notificationCollection, btnViewAll, btnNotification, true);

            });

            if (notificationCollection.getChildren().size()> 4)
                break;
        }
//        if( notificationCollection.getChildren().size()== 0) {
//            System.err.println("container.getLeft().setVisible(false);");
//            container.getLeft().setVisible(false);
//            container.getLeft().setManaged(false);
//        }

        btnViewAll.setOnAction(event -> {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    recAll = true;

                    container.getLeft().setVisible(false);
                    container.getLeft().setManaged(false);

                    ((StackPane) container.getCenter()).getChildren().get(0).setVisible(false);
                    ((StackPane) container.getCenter()).getChildren().get(0).setManaged(false);

                    tilePane.getStyleClass().add("viewAllNotTile");

                    ScrollPane scrollPane = new ScrollPane(tilePane);
                    scrollPane.getStyleClass().add("viewAllNotScroll");
                    scrollPane.getStyleClass().add("edge-to-edge");


                    Button btnRBack = new Button();
                    btnRBack.getStyleClass().add("r-back");
                    HBox hBox = new HBox(btnRBack, scrollPane);
                    hBox.getStyleClass().add("viewAllNotHBox");

                    btnRBack.setOnAction(event1 -> {
                        recAll = false;

                        ((StackPane) container.getCenter()).getChildren().remove(hBox);

                        notificationCollection.getChildren().clear();
                        innerSync(rootContainer, container, notificationCollection, btnViewAll, btnNotification, false);



                        ((StackPane) container.getCenter()).getChildren().get(0).setVisible(true);
                        ((StackPane) container.getCenter()).getChildren().get(0).setManaged(true);
//                        if( notificationCollection.getChildren().size()== 0) {
//                            System.err.println("container.getLeft().setVisible(false);");
//                            container.getLeft().setVisible(false);
//                            container.getLeft().setManaged(false);
//                        }
                        if (notificationCollection.getChildren().size() > 0) {
                            container.getLeft().setVisible(true);
                            container.getLeft().setManaged(true);
                        }
                        if (notificationCollection.getChildren().size() > 5) {
                            notificationCollection.getChildren().remove(4, notificationCollection.getChildren().size()-1);
                        }
                    });


                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            ((StackPane) container.getCenter()).getChildren().add(hBox);
                            tilePane.getChildren().clear();


                        }
                    });


                    recAllNot(rootContainer, container, notificationCollection, btnViewAll, btnNotification);
                }
            }).start();

        });
        btnNotification.setOnAction(btnViewAll.getOnAction());

        if (recAll && update) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    tilePane.getChildren().clear();

                }
            });
            recAllNot(rootContainer, container, notificationCollection, btnViewAll, btnNotification);
        }
    }

    public static void recAllNot(StackPane rootContainer, BorderPane container, VBox notificationCollection, Button btnViewAll, Button btnNotification) {

        try {
            allNotifications = Magasinier.NotificationManager.loadNReadIgnoreTableToList();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for (Magasinier.Notification notification :
                allNotifications) {
            if (notification.isRead())
                continue;

            Button btnClose = new Button();
            btnClose.getStyleClass().add("close_Not_As_read_");
            btnClose.setVisible(false);

            HBox closeWrapper = new HBox(btnClose);
            closeWrapper.setAlignment(Pos.BASELINE_RIGHT);


            Label lblContent = getNotificationCoreLabel(notification);

            lblContent.getStyleClass().add("enAtt");
            HBox contentWrapper = new HBox(lblContent);
            contentWrapper.setSpacing(16);


            VBox miniNot = new VBox(closeWrapper, contentWrapper);
            miniNot.getStyleClass().add("notification");


            miniNot.setOnMouseEntered(event -> {
                btnClose.setVisible(true);

            });
            miniNot.setOnMouseExited(event -> {
                btnClose.setVisible(false);

            });


            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    tilePane.getChildren().add(0, miniNot);

                }
            });
            btnClose.setOnAction(event1 -> {
                notification.setIgnore(true);
                notification.setRead(true);

                miniNot.getStyleClass().remove("notification");
                miniNot.getStyleClass().add("read-notification");
                miniNot.getChildren().remove(closeWrapper);

            });


            appendActionToNot(rootContainer, container, notificationCollection, btnViewAll, btnNotification, miniNot, closeWrapper, notification);
        }

        try {
            ReadNotifications = Magasinier.NotificationManager.loadReadTableToList();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        for (Magasinier.Notification notification :
                ReadNotifications) {

            Label lblContent = getNotificationCoreLabel(notification);
            lblContent.getStyleClass().add("enAtt");
            HBox contentWrapper = new HBox(lblContent);
            contentWrapper.setSpacing(16);

            VBox miniNot = new VBox(contentWrapper);
            miniNot.getStyleClass().add("read-notification");


            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    tilePane.getChildren().add( miniNot);

                }
            });
            appendActionToNot(rootContainer, container, notificationCollection, btnViewAll, btnNotification, miniNot, null, notification);
        }
    }

    public static Label getNotificationCoreLabel(Magasinier.Notification notification) {
        Text cuisinierName = new Text(CommandeUnitaireManager.getCuisiner(notification.getIdCuisinier()).getNom());
        cuisinierName.getStyleClass().add("nb-bold");
        Text ingredientName = new Text(Ingredient.getIngredient(notification.getIdIngredient()).nom);
        ingredientName.getStyleClass().add("nb-bold");



//            + (iqAtMini.getQuantite() - iq.getQuantite()* commandeUnitaire.getQuantite())

        Text cuisinierLabel = new Text("Le cuisinier ");
        cuisinierLabel.getStyleClass().add("nb");

        Text manqu = new Text(" a besoin de ");
        manqu.getStyleClass().add("nb");


        return new Label(
                null,
                new TextFlow(
                        cuisinierLabel,
                        cuisinierName,
                        manqu,
                        ingredientName
//                            new Text(" \n quantite actuelle : ")
                )
        );
    }

    public static void appendActionToNot(StackPane rootContainer, BorderPane container, VBox notificationCollection, Button btnViewAll, Button btnNotification, VBox miniNot, HBox closeWrapper, Magasinier.Notification notification) {
            miniNot.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {



                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            miniNot.getStyleClass().remove("notification");
                            miniNot.getStyleClass().add("read-notification");

                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    miniNot.getChildren().remove(closeWrapper);

                                }
                            });




                            Rectangle rectangle = new Rectangle(rootContainer.getWidth(), rootContainer.getHeight(), Color.valueOf("rgba(0, 0, 0, .5)"));
                            Button btnClosePop = new Button();
                            btnClosePop.getStyleClass().add("close_enCours_");
                            Label popNotContent = new Label(
                                    "cuinier : " +
                                            CommandeUnitaireManager.getCuisiner(notification.getIdCuisinier()).getNom() +
                                            "\n" +
                                            "ingredient : " +
                                            Ingredient.getIngredient(notification.getIdIngredient()).nom
                            );



                            int qActuelleAtStockInt = 0;
                            int qActuelleAtMiniStockInt = 0;



                            popNotContent.getStyleClass().add("cred");


                            Label qActuelleAtMiniStock = new Label();
                            for (IngredientAndQuantite ing :
                                    MiniStockManager.getMiniStock(notification.getIdCuisinier()).ingredientAndQuantites) {
                                if (ing.getIdIngredient() == notification.getIdIngredient()) {
                                    qActuelleAtMiniStock.setText("quantite actuelle dans le mini stock : " + ing.getQuantite());
                                    qActuelleAtMiniStockInt = ing.getQuantite();

                                }
                            }

                            Label qActuelleAtStock = new Label();
                            for (IngredientPropritiete ing :
                                    StockManager.getStock(1).ingredientPropreties) {
                                if (ing.getIdIngredient() == notification.getIdIngredient()) {
                                    qActuelleAtStock.setText("quantite actuelle dans le stock : " +  ing.getQuantitie());
                                    qActuelleAtStockInt = ing.getQuantitie();
                                }
                            }

                            Label qAFournire = new Label("quantite a fournire : ");
                            qAFournire.getStyleClass().add("action");


                            TextField qAFournireInpute = new TextField();
                            qAFournireInpute.setPromptText("0");
                            HBox qAFournireContainer = new HBox(qAFournire, qAFournireInpute);

                            Label log = new Label();
                            log.getStyleClass().add("log");





                            VBox popCore = new VBox(popNotContent, qActuelleAtMiniStock, qActuelleAtStock, qAFournireContainer, log);
                            popCore.getStyleClass().add("popCore");


//                    Button btnAuto = new Button("auto");
//                    Button btnManual = new Button("manual");
//                    HBox actionBar = new HBox(btnAuto, btnManual);

                            Button btnValider = new Button("valider");
                            Button btnNRead = new Button("marquer non lu");
                            HBox actionBar = new HBox(btnNRead, btnValider);
                            actionBar.getStyleClass().add("actionBar");

                            int finalQActuelleAtStockInt = qActuelleAtStockInt;
                            int finalQActuelleAtMiniStockInt = qActuelleAtMiniStockInt;
                            btnValider.setOnAction(event1 -> {

                                try {

                                    if (finalQActuelleAtStockInt - Integer.parseInt(qAFournireInpute.getText()) < 0) {
                                        throw new Exception("la quantitee demanandee n\' est pas disponible");
                                    }

                                    StockManager.initialisation(notification.getIdIngredient(), 1, (finalQActuelleAtStockInt - Integer.parseInt(qAFournireInpute.getText())));
                                    MiniStockManager.updateIngredient(notification.getIdIngredient(), notification.getIdCuisinier(), (finalQActuelleAtMiniStockInt + Integer.parseInt(qAFournireInpute.getText())));


                                    rootContainer.getChildren().remove(rootContainer.getChildren().size() -1);
                                    rootContainer.getChildren().remove(rootContainer.getChildren().size() -1);

                                    Magasinier.NotificationManager.remove(notification.getIdNotification());

                                } catch (Exception ex) {
                                    if (ex.getMessage().equals("la quantitee demanandee n\' est pas disponible")) {
                                        log.setText(ex.getMessage());
                                        log.setGraphic(new ImageView(new Image("img/error.png")));
                                        log.setGraphicTextGap(5);

                                        qAFournireInpute.getStyleClass().add("mekkiError");
                                    }


                                    System.err.println("invalid inpute !");
                                }

                            });
                            btnNRead.setOnAction(event1 -> {
                                notification.setRead(false);
                                notification.setIgnore(false);
                                if (!recAll) {
                                    container.getLeft().setVisible(true);
                                    container.getLeft().setManaged(true);



                                    notificationCollection.getChildren().clear();
                                    innerSync(rootContainer, container, notificationCollection, btnViewAll, btnNotification, false);



                                }

                            });

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

                            notification.setRead(true);





                        }
                    }).start();


                }
            });
    }


}
