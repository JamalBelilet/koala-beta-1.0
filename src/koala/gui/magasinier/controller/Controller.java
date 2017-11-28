package koala.gui.magasinier.controller;

import javafx.beans.InvalidationListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.util.Callback;
import koala.administration.Compte;
import koala.gestionEmployes.EmployeManager;
import koala.gestionEmployes.Magasinier;
import koala.gestionStock.IngredientManager;
import koala.gui.cuisinier.controller.FormUIGenerator;
import koala.gui.login.controller.CompteUIGenerator;
import strings.French;

import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;

import static koala.gui.magasinier.controller.IngredientUIManager.data;

public class Controller {

    private static InvalidationListener listener ;


    private Rectangle rect_left ;
    private Rectangle rect_center ;

    @FXML
    private HBox compteInfo;





    @FXML
    private BorderPane container;


    @FXML
    private StackPane rootContainer;
    @FXML
    private StackPane enCours;

    @FXML
    private Button proposer;
    @FXML
    private Button signaler;
    private ObservableList<Node> proposerForm;
    @FXML
    private TableView tableView;
    @FXML
    private TextField searchField;

    @FXML
    private Button btnViewAll;
    @FXML
    private Button btnNotification;
    @FXML
    private VBox notificationCollection;


    @FXML
    private ImageView lines;

    static Timer timer;


    @FXML
    public void initialize() {
        timer = new Timer();

        Image lines_ico = new Image("/img/lines_null.png");
        Image lines_ico_hover = new Image("/img/lines_null-hover.png");
        searchField.focusedProperty().addListener(observable -> {

            if (searchField.isFocused()) {
                lines.setImage(lines_ico_hover);

            } else
                lines.setImage(lines_ico);

        });

        btnViewAll.setText(French.MAGASINIER_VIEW_ALL);

        FormUIGenerator.setProposerNdSignalerForms(signaler, proposer, null, null, rootContainer);




        container.getLeft().setVisible(false);
        container.getLeft().setManaged(false);



        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        NotificationUIGenerator.syncNotifications(rootContainer, container, notificationCollection, btnViewAll, btnNotification);
                    }
                }).start();
            }
        }, 0, 5000);

        tableView.sceneProperty().addListener(observable -> {
            if (tableView.getScene() != null) {
                tableView.setMinHeight(tableView.getScene().getHeight() - 253);
                tableView.getScene().heightProperty().addListener(observable1 -> {
                    try {
                        tableView.setMinHeight(tableView.getScene().getHeight() - 253);
                    } catch (Exception e) {
                        listener = observable2 -> rootContainer.getScene().heightProperty().removeListener(listener);

                    }


                });
            }
        });


        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        tableView.setRowFactory(new Callback<TableView, TableRow>() {
            @Override
            public TableRow call(TableView param) {
                final TableRow row = new TableRow<>();

                MenuItem mi1 = new MenuItem("Menu item 1");
                mi1.setOnAction((ActionEvent event) -> {
                    System.out.println("Menu item 1");
                    Object item = tableView.getSelectionModel().getSelectedItem();
                    System.out.println(row.getIndex());

                    IngredientManager.Delete(Integer.parseInt(((IngredientProperty) tableView.getSelectionModel().getSelectedItem()).getId()));
                    data.remove(tableView.getSelectionModel().getSelectedItem());


//                    System.out.println("Selected item: " + item.getName());
                });

                ContextMenu menu = new ContextMenu();
                menu.getItems().add(mi1);
                tableView.setContextMenu(menu);


//                final ContextMenu rowMenu = new ContextMenu();
//                MenuItem editItem = new MenuItem("Edit");
////                editItem.setOnAction(...);
//                MenuItem removeItem = new MenuItem("Delete");
//                removeItem.setOnAction(new EventHandler<ActionEvent>() {
//
//                    @Override
//                    public void handle(ActionEvent event) {
//                        tableView.getItems().remove(row.getItem());
//                    }
//                });
//                rowMenu.getItems().addAll(editItem, removeItem);
//
//// only display context menu for non-null items:
//                row.contextMenuProperty().bind(
//                        Bindings.when(Bindings.isNotNull(row.itemProperty()))
//                                .then(rowMenu)
//                                .otherwise((ContextMenu)null));
                return row;
            }
        });


        IngredientUIManager.initTable(tableView);

        tableView.setEditable(true);
        try {
            IngredientUIManager.appendData(tableView, searchField);
        } catch (SQLException e) {
            e.printStackTrace();
        }



        try {
            System.out.println(Compte.getIdCompte());
            Magasinier magasinier = (Magasinier) EmployeManager.getEmloye(Compte.getIdCompte(), "magasinier");
            CompteUIGenerator.soutSignIn(compteInfo, magasinier, "magasinier");


        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static void killAll() {


        NotificationUIGenerator.lastUpdate = null;
        if (timer != null ) {
            timer.cancel();
            timer.purge();
        }


    }

}
