package koala.gui.admin.controller;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import koala.db.ConnectionManager;
import koala.gestionEmployes.Magasinier;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import static koala.gui.magasinier.controller.NotificationUIGenerator.*;

/**
 * Created by bjama on 3/21/2017.
 */
public class NotificationUIGenerator extends Magasinier.Notification{
    static List<Magasinier.Notification> notifications;
    static List<Magasinier.Notification> allNotifications;
    static List<Magasinier.Notification> ReadNotifications;
    static TilePane tilePane = new TilePane();
    static boolean recAll = false;
    static Timestamp lastUpdate;

    public static Connection connection = ConnectionManager.getInstance().getConnection();


    public static void syncNotifications(StackPane rootContainer, BorderPane container, VBox notificationCollection, Button btnViewAll, Button btnNotification) {
        Timestamp lastUpdateFromDatabase = null;
        try {
            ResultSet resultSet = connection.createStatement().executeQuery("SELECT * from updateLog WHERE tag = 'magNot'");
            resultSet.next();
            lastUpdateFromDatabase = resultSet.getTimestamp("lastUpdate");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (lastUpdate != null && lastUpdateFromDatabase != null) {
            if (lastUpdate.before(lastUpdateFromDatabase)) {

                notificationCollection.getChildren().clear();
                lastUpdate = lastUpdateFromDatabase;
                innerSync(rootContainer, container, notificationCollection, btnViewAll, btnNotification, true);

                System.err.println("---- before the problem");

            }
            System.err.println("this is the problem");
        } else {
            notificationCollection.getChildren().clear();


            lastUpdate = lastUpdateFromDatabase;
            innerSync(rootContainer, container, notificationCollection, btnViewAll, btnNotification, false);

        }

    }

    private static void innerSync(StackPane rootContainer, BorderPane container, VBox notificationCollection, Button btnViewAll, Button btnNotification, boolean update) {
        try {
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
                container.getRight().setVisible(true);
                container.getRight().setManaged(true);
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

            notificationCollection.getChildren().add(0, miniNot);
            appendActionToNot(rootContainer, container, notificationCollection, btnViewAll, btnNotification, miniNot, null, notification);


            btnClose.setOnAction(event1 -> {


                notification.setIgnore(true);
                notificationCollection.getChildren().remove(miniNot);
                if (notificationCollection.getChildren().size() ==0 ){
                    container.getRight().setVisible(false);
                    container.getRight().setManaged(false);
                }
                innerSync(rootContainer, container, notificationCollection, btnViewAll, btnNotification, true);

            });

            if (notificationCollection.getChildren().size()> 4)
                break;
        }
        if( notificationCollection.getChildren().size()== 0) {
            System.err.println("container.getRight().setVisible(false);");
            container.getRight().setVisible(false);
            container.getRight().setManaged(false);
        }

        btnViewAll.setOnAction(event -> {
            recAll = true;

            container.getRight().setVisible(false);
            container.getRight().setManaged(false);

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
                if (notificationCollection.getChildren().size() > 0) {
                    container.getRight().setVisible(true);
                    container.getRight().setManaged(true);
                }


                notificationCollection.getChildren().clear();
                innerSync(rootContainer, container, notificationCollection, btnViewAll, btnNotification, true);



                ((StackPane) container.getCenter()).getChildren().get(0).setVisible(true);
                ((StackPane) container.getCenter()).getChildren().get(0).setManaged(true);
            });

            ((StackPane) container.getCenter()).getChildren().add(hBox);

            tilePane.getChildren().clear();


            recAllNot(rootContainer, container, notificationCollection , btnViewAll, btnNotification);

        });
        btnNotification.setOnAction(btnViewAll.getOnAction());

        if (recAll && update) {
            tilePane.getChildren().clear();
            recAllNot(rootContainer, container, notificationCollection, btnViewAll, btnNotification);
        }
    }

//    private static void recAllNot(StackPane rootContainer) {
//
//        try {
//            allNotifications = Magasinier.NotificationManager.loadNReadIgnoreTableToList();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        for (Magasinier.Notification notification :
//                allNotifications) {
//            if (notification.isRead())
//                continue;
//
//            Button btnClose = new Button();
//            btnClose.getStyleClass().add("close_Not_As_read_");
//            btnClose.setVisible(false);
//
//            HBox closeWrapper = new HBox(btnClose);
//            closeWrapper.setAlignment(Pos.BASELINE_RIGHT);
//
//
//            Label lblContent = getNotificationCoreLabel(notification);
//
//            lblContent.getStyleClass().add("enAtt");
//            HBox contentWrapper = new HBox(lblContent);
//            contentWrapper.setSpacing(16);
//
//
//            VBox miniNot = new VBox(closeWrapper, contentWrapper);
//            miniNot.getStyleClass().add("notification");
//
//
//            miniNot.setOnMouseEntered(event -> {
//                btnClose.setVisible(true);
//
//            });
//            miniNot.setOnMouseExited(event -> {
//                btnClose.setVisible(false);
//
//            });
//
//            tilePane.getChildren().add(0, miniNot);
//
//            btnClose.setOnAction(event1 -> {
//                notification.setIgnore(true);
//                notification.setRead(true);
//
//                miniNot.getStyleClass().remove("notification");
//                miniNot.getStyleClass().add("read-notification");
//                miniNot.getChildren().remove(closeWrapper);
//
//            });
//
//
//            appendActionToNot(rootContainer, miniNot, closeWrapper, notification);
//        }
//
//        try {
//            ReadNotifications = Magasinier.NotificationManager.loadReadTableToList();
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        for (Magasinier.Notification notification :
//                ReadNotifications) {
//
//            Label lblContent = getNotificationCoreLabel(notification);
//            lblContent.getStyleClass().add("enAtt");
//            HBox contentWrapper = new HBox(lblContent);
//            contentWrapper.setSpacing(16);
//
//            VBox miniNot = new VBox(contentWrapper);
//            miniNot.getStyleClass().add("read-notification");
//
//            tilePane.getChildren().add( miniNot);
//
//            appendActionToNot(rootContainer, miniNot, null, notification);
//        }
//    }

//    private static Label getNotificationCoreLabel(Magasinier.Notification notification) {
//        Text cuisinierName = new Text(CommandeUnitaireManager.getCuisiner(notification.getIdCuisinier()).getNom());
//        cuisinierName.setStyle("-fx-font-weight: bold;  -fx-text-fill: #EEEEEE;");
//        Text ingredientName = new Text(Ingredient.getIngredient(notification.getIdIngredient()).nom);
//        ingredientName.setStyle("-fx-font-weight: bold;  -fx-text-fill: #666666;");
//
//
//
////            + (iqAtMini.getQuantite() - iq.getQuantite()* commandeUnitaire.getQuantite())
//
//
//        return new Label(
//                null,
//                new TextFlow(
//                        new Text("cuisinier : "),
//                        cuisinierName,
//                        new Text(" manque d ingredient : "),
//                        ingredientName
////                            new Text(" \n quantite actuelle : ")
//                )
//        );
//    }
//
//    public static void appendActionToNot(StackPane rootContainer, VBox miniNot, HBox closeWrapper, Magasinier.Notification notification) {
//            miniNot.setOnMouseClicked(new EventHandler<MouseEvent>() {
//                @Override
//                public void handle(MouseEvent event) {
//
//
//
//                    miniNot.getStyleClass().remove("notification");
//                    miniNot.getStyleClass().add("read-notification");
//                    miniNot.getChildren().remove(closeWrapper);
//
//
//
//
//
//                    Rectangle rectangle = new Rectangle(rootContainer.getWidth(), rootContainer.getHeight(), Color.valueOf("rgba(0, 0, 0, .5)"));
//                    Button btnClosePop = new Button();
//                    btnClosePop.getStyleClass().add("close_enCours_");
//                    Label popNotContent = new Label(
//                            "cuinier : " +
//                            CommandeUnitaireManager.getCuisiner(notification.getIdCuisinier()).getNom() +
//                            "\n" +
//                            "ingredient : " +
//                            Ingredient.getIngredient(notification.getIdIngredient()).nom
//                    );
//
//
//
//                    int qActuelleAtStockInt = 0;
//                    int qActuelleAtMiniStockInt = 0;
//
//
//
//                    popNotContent.getStyleClass().add("cred");
//
//
//                    Label qActuelleAtMiniStock = new Label();
//                    for (IngredientAndQuantite ing :
//                            MiniStockManager.getMiniStock(notification.getIdCuisinier()).ingredientAndQuantites) {
//                        if (ing.getIdIngredient() == notification.getIdIngredient()) {
//                            qActuelleAtMiniStock.setText("quantite actuelle dans le mini stock : " + ing.getQuantite());
//                            qActuelleAtMiniStockInt = ing.getQuantite();
//
//                        }
//                    }
//
//                    Label qActuelleAtStock = new Label();
//                    for (IngredientPropritiete ing :
//                            StockManager.getStock(1).ingredientPropreties) {
//                        if (ing.getIdIngredient() == notification.getIdIngredient()) {
//                            qActuelleAtStock.setText("quantite actuelle dans le stock : " +  ing.getQuantitie());
//                            qActuelleAtStockInt = ing.getQuantitie();
//                        }
//                    }
//
//                    Label qAFournire = new Label("quantite a fournire : ");
//                    qAFournire.getStyleClass().add("action");
//
//
//                    TextField qAFournireInpute = new TextField();
//                    qAFournireInpute.setPromptText("0");
//                    HBox qAFournireContainer = new HBox(qAFournire, qAFournireInpute);
//
//                    Label log = new Label();
//                    log.getStyleClass().add("log");
//
//
//
//
//
//                    VBox popCore = new VBox(popNotContent, qActuelleAtMiniStock, qActuelleAtStock, qAFournireContainer, log);
//                    popCore.getStyleClass().add("popCore");
//
//
////                    Button btnAuto = new Button("auto");
////                    Button btnManual = new Button("manual");
////                    HBox actionBar = new HBox(btnAuto, btnManual);
//
//                    Button btnValider = new Button("valider");
//                    Button btnNRead = new Button("marquer non lu");
//                    HBox actionBar = new HBox(btnNRead, btnValider);
//                    actionBar.getStyleClass().add("actionBar");
//
//                    int finalQActuelleAtStockInt = qActuelleAtStockInt;
//                    int finalQActuelleAtMiniStockInt = qActuelleAtMiniStockInt;
//                    btnValider.setOnAction(event1 -> {
//
//                        try {
//
//                            if (finalQActuelleAtStockInt - Integer.parseInt(qAFournireInpute.getText()) < 0) {
//                                throw new Exception("la quantitee demanandee n\' est pas disponible");
//                            }
//
//                            StockManager.initialisation(notification.getIdIngredient(), 1, (finalQActuelleAtStockInt - Integer.parseInt(qAFournireInpute.getText())));
//                            MiniStockManager.updateIngredient(notification.getIdIngredient(), notification.getIdCuisinier(), (finalQActuelleAtMiniStockInt + Integer.parseInt(qAFournireInpute.getText())));
//
//                            rootContainer.getChildren().remove(rootContainer.getChildren().size() -1);
//                            rootContainer.getChildren().remove(rootContainer.getChildren().size() -1);
//
//                            Magasinier.NotificationManager.remove(notification.getIdNotification());
//
//                        } catch (Exception ex) {
//                            if (ex.getMessage().equals("la quantitee demanandee n\' est pas disponible")) {
//                                log.setText(ex.getMessage());
//                                log.setGraphic(new ImageView(new Image("img/error.png")));
//                                log.setGraphicTextGap(5);
//
//                                qAFournireInpute.getStyleClass().add("mekkiError");
//                            }
//
//
//                            System.err.println("invalid inpute !");
//                        }
//
//                    });
//                    btnNRead.setOnAction(event1 -> {
//                        notification.setRead(false);
//                        notification.setIgnore(false);
//
//                    });
//
//                    VBox popNotification = new VBox(btnClosePop, popCore, actionBar);
//                    popNotification.getStyleClass().add("popNotification");
//                    rectangle.setOnMouseClicked(new EventHandler<MouseEvent>() {
//                        @Override
//                        public void handle(MouseEvent event) {
//                            rootContainer.getChildren().remove(rootContainer.getChildren().size() -1);
//                            rootContainer.getChildren().remove(rootContainer.getChildren().size() -1);
//                        }
//                    });
//                    rootContainer.getChildren().add(rectangle);
//                    rootContainer.getChildren().addAll(popNotification);
//
//                    btnClosePop.setOnAction(new EventHandler<ActionEvent>() {
//                        @Override
//                        public void handle(ActionEvent event) {
//                            rootContainer.getChildren().remove(rootContainer.getChildren().size() -1);
//                            rootContainer.getChildren().remove(rootContainer.getChildren().size() -1);
//                        }
//                    });
//
//                    notification.setRead(true);
//
//
//                }
//            });
//    }
}
