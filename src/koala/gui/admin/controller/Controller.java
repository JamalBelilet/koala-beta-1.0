package koala.gui.admin.controller;

import javafx.application.Platform;
import javafx.css.PseudoClass;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import koala.administration.Compte;
import koala.gestionEmployes.Admin;
import koala.gestionEmployes.EmployeManager;
import koala.gui.login.controller.CompteUIGenerator;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Timer;

import static java.lang.Thread.sleep;


public class Controller {

    @FXML
    private TreeView<String> treeView;
    @FXML
    private StackPane centerStack;
    @FXML
    private ScrollPane centerScrollPane;
    @FXML
    private FlowPane centerPane;
    @FXML
    private HBox compteInfo;

    public static HBox actionBarForAll = new HBox();

    @FXML
    private StackPane rootContainer;
    @FXML
    private BorderPane container;
    @FXML
    private VBox notificationCollection;
    @FXML
    private Button btnViewAll;
    @FXML
    private Button btnNotification;

    Timer timer = new Timer();

    @FXML
    public void initialize() {
        for (TreeItem<String> treeItem: treeView.getRoot().getChildren()) {

            treeItem.setExpanded(true);
        }

        PseudoClass subElementPseudoClass = PseudoClass.getPseudoClass("sub-tree-item");

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    sleep(2000);
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                AccueilUIGenerator.initAccuil(centerStack, centerScrollPane, centerPane, actionBarForAll);
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        boolean isChow = false;
        ArrayList<EventHandler<KeyEvent>> eventHandlers = new ArrayList<>();

        final int[] count = {2};

        treeView.getSelectionModel().selectFirst();
        treeView.setCellFactory(tv -> {
            TreeCell<String> cell = new TreeCell<String>() {
                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    setDisclosureNode(null);

                    if (empty) {
                        setText("");
                        setGraphic(null);
                    } else {
                        setText(item); // appropriate text for item
                        Tooltip tooltip = new Tooltip(item.substring(0, 1));

                        if (--count[0] == 0) {
                            setTooltip(tooltip);
                            count[0]++;
                        }

                        int index = this.getIndex();
                        EventHandler eventHandler = new EventHandler<KeyEvent>() {
                            @Override
                            public void handle(KeyEvent event) {

                                if (!((KeyEvent) event).getCode().equals(KeyCode.ALT))
                                    tooltip.hide();

                                if (((KeyEvent) event).getCode().getName().equalsIgnoreCase(item.substring(0, 1))) {
                                    System.err.println(item.substring(0, 1));

                                    adminActions(getText());
                                    treeView.getSelectionModel().select(index);
                                    for (EventHandler e : eventHandlers) {


                                        getScene().removeEventHandler(KeyEvent.ANY, e);
                                        getScene().removeEventHandler(KeyEvent.ANY, this);

                                        System.out.println("one");
                                    }

                                }
                                event.consume();

                            }

                        };
                        eventHandlers.add(eventHandler);


                        this.getScene().addEventHandler(KeyEvent.KEY_PRESSED, event -> {

                            if (event.getCode().equals(KeyCode.ALT)) {
                                Bounds boundsInScreen = this.localToScreen(this.getBoundsInLocal());
                                double x = boundsInScreen.getMinX() -7;
                                double y = (boundsInScreen.getMaxY() + boundsInScreen.getMinY())/2d -7;

                                System.out.println(x + "  "+ y);
                                if (getTooltip()!= null && ! getTooltip().isShowing())
                                    tooltip.show( this.getScene().getWindow() , x, y );

                                this.getScene().addEventHandler(KeyEvent.KEY_PRESSED, eventHandler);
                                event.consume();

                            }
                        });

                        setOnMouseClicked(event -> {
                            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
                                TreeItem<String> ti = treeItemProperty().get();
                                ti.expandedProperty().set(true);


                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Platform.runLater(
                                                new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        adminActions(getText());

                                                    }
                                                }
                                        );
                                    }
                                }).start();

                            }
                            event.consume();
                        });

                    }
                }

                private void adminActions(String text) {
                    switch (text) {
                        case "Accueil" :
                            try {
                                AccueilUIGenerator.initAccuil(centerStack, centerScrollPane, centerPane, actionBarForAll);
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                            break;
                        case "Employés" :

                            centerStack.getChildren().remove(actionBarForAll);
                            centerStack.getChildren().add(actionBarForAll);

                            try {
                                EmployeUIGenerator.initEmplyes(centerStack, centerPane, actionBarForAll, treeView);
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                            break;
                        case "Comptes" :

                            centerStack.getChildren().remove(actionBarForAll);
                            centerStack.getChildren().add(actionBarForAll);
                            CompteUIManager.appendComptes(centerStack, centerScrollPane, centerPane, actionBarForAll, treeView);
                            break;
                        case "Stock" :

                            centerStack.getChildren().remove(actionBarForAll);
                            centerStack.getChildren().add(actionBarForAll);

                            StockUIGenerator.appendStock(centerStack, centerScrollPane, centerPane, actionBarForAll, treeView);
                            break;
                        case "Catégories" :
                            try {

                                centerStack.getChildren().remove(actionBarForAll);

                                centerStack.getChildren().add(actionBarForAll);
                                CategorieUIGenerator.initCategories(centerStack, centerPane, actionBarForAll, treeView);
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                            break;
                        case "Plats" :
                            try {
                                centerStack.getChildren().remove(actionBarForAll);

                                centerStack.getChildren().add(actionBarForAll);

                                PlatUIGenerateur.appendPlats(centerStack, centerPane, actionBarForAll, treeView);
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                            break;
                        case "Plats Proposés" :
                            try {
                                centerStack.getChildren().remove(actionBarForAll);

                                centerStack.getChildren().add(actionBarForAll);

                                PlatProposerUIGenerator.appendPlats(centerStack, centerPane, actionBarForAll);
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                            break;
                        case "Données" :
                            try {
                                centerStack.getChildren().remove(actionBarForAll);

//                                            centerStack.getChildren().add(actionBarForAll);

                                BackupUIGenerator.initBackupUI(rootContainer, centerPane);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        default:
                            centerStack.getChildren().remove(actionBarForAll);
                            centerPane.getChildren().clear();

                    }
                }

            };
            cell.treeItemProperty().addListener((obs, oldTreeItem, newTreeItem) -> {
                cell.pseudoClassStateChanged(subElementPseudoClass,
                        newTreeItem != null && newTreeItem.getParent() != cell.getTreeView().getRoot());
            });
            return cell ;
        });

        try {
            Admin admin = (Admin) EmployeManager.getEmloye(Compte.getIdCompte(), "admin");
            CompteUIGenerator.soutSignIn(compteInfo, admin, "administrateur");


        } catch (SQLException e) {
            e.printStackTrace();
        }

    }




}
