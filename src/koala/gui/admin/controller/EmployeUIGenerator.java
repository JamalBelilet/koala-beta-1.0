package koala.gui.admin.controller;

import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import koala.gestionEmployes.Employe;
import koala.gestionEmployes.EmployeManager;
import strings.French;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bjama on 4/21/2017.
 */
public class EmployeUIGenerator extends CategorieUIGenerator {

    public EmployeUIGenerator() throws SQLException {
    }

    public static void initEmplyes(StackPane centerStack, FlowPane platsContainer, Pane actionBarForAll, TreeView treeView) throws SQLException {

        platsContainer.getChildren().clear();

        ArrayList<StackPane> tobeDeleted = new ArrayList<>();
        ArrayList<Object> tobeDeletedCategories = new ArrayList<>();
        ArrayList<CheckBox> selected = new ArrayList<>();


        final boolean[] deleteAllSelectionIsActive = {false};
        CheckBox selectAll = new CheckBox("Tout sélectionner");
        selectAll.getStyleClass().addAll("select-all", "select");

        platsContainer.getStyleClass().add("plats");
        List<Employe> employes = EmployeManager.loadTableToList();
        for (Employe employe :
                employes) {
            StackPane stackPane = getStackPane(centerStack, platsContainer, tobeDeleted, tobeDeletedCategories, selected, deleteAllSelectionIsActive, selectAll, employe, treeView);


            platsContainer.getChildren().add( stackPane );
        }


        Button statusAll = new Button();
        statusAll.getStyleClass().add("statusAll");

        Button addAll = new Button();
        addAll.getStyleClass().add("addAll");

        addAll.setOnAction(event -> {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    Platform.runLater(
                            new Runnable() {
                                @Override
                                public void run() {
                                    FormesUIGenerator.getEditForCategorieForm(centerStack, null, false , platsContainer, tobeDeleted, tobeDeletedCategories, selected, deleteAllSelectionIsActive, selectAll, null, treeView);

                                }
                            }
                    );
                }
            }).start();

        });
//            Button disableAll = new Button();
//            disableAll.getStyleClass().add("disableAll");

        Button deleteAll = new Button();
        deleteAll.getStyleClass().add("deleteAll");


        actionBarForAll.getChildren().clear();
        actionBarForAll.getChildren().addAll(addAll, deleteAll);

        actionBarForAll.getStyleClass().add("categorie-vction-bar");
        actionBarForAll.getStyleClass().add("categorie-top-bar");

        initdeleteOptions(centerStack, platsContainer, actionBarForAll, tobeDeleted, tobeDeletedCategories, selected, deleteAllSelectionIsActive, selectAll, deleteAll, treeView);



    }

    static StackPane getStackPane(final StackPane centerStack, FlowPane platsContainer, ArrayList<StackPane> tobeDeleted, ArrayList<Object> tobeDeletedCategories, ArrayList<CheckBox> selected, boolean[] deleteAllSelectionIsActive, CheckBox selectAll, final Employe employe, TreeView treeView) throws SQLException {
        Image image = null;
        if (employe.getImage() != null)
            image = new Image(employe.getImage().getBinaryStream()) ;

        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(200);
//            imageView.maxWidth(185);
//            imageView.setViewport(new Rectangle2D(250, 250, 550, 550));
        imageView.setPreserveRatio(true);


        ScrollPane imageHack = new ScrollPane(imageView);
        imageHack.setPrefHeight(185);
        imageHack.setPrefWidth(185);
        imageHack.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        imageHack.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);


        VBox vBox = getDesc(employe);

        HBox hBox = new HBox(imageHack, vBox);
        hBox.getStyleClass().add("one-category");
        StackPane stackPane = new StackPane(hBox);

        Rectangle rectangle = new Rectangle(450, 185, Color.valueOf("rgba(0,0,0,.7)"));


        Button status = new Button(French.ADMIN_STATS);
        status.getStyleClass().add("status");

        Button edit = new Button(French.ADMIN_MODIFIER);
        edit.getStyleClass().add("edit");
        edit.setOnAction(event -> {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Platform.runLater(
                            new Runnable() {
                                @Override
                                public void run() {
                                    FormesUIGenerator.getEditForCategorieForm(centerStack, employe, true, platsContainer, tobeDeleted, tobeDeletedCategories, selected, deleteAllSelectionIsActive, selectAll, stackPane, treeView);
                                }
                            }
                    );
                }
            }).start();
        });


//            Button disable = new Button("Désactiver");
//            disable.getStyleClass().add("disable");

        Button delete = new Button(French.ADMIN_SUPPRIMER);
        delete.getStyleClass().add("delete");

        CheckBox select = new CheckBox("Sélectionner");
        select.getStyleClass().add("select");

        selected.add(select);


        HBox actionBar = new HBox(edit,  delete);
        actionBar.getStyleClass().add("categorie-vction-bar");


        Text employeName = new Text(employe.getNom() + " " + employe.getPrenom());
        employeName.setStyle("-fx-font-weight: bold;");
        Label warning = new Label(null, new TextFlow(new Text(French.ADMIN_SUPPRIMER_QUESTION), employeName, new Text(" ?")));
        warning.getStyleClass().add("warning");

        Button yes = new Button("Oui");
        yes.getStyleClass().add("yes");

        Button no = new Button("Non");
        no.getStyleClass().add("no");


        HBox deleteChoice = new HBox(yes, no);
        VBox confirmDelete = new VBox(warning, deleteChoice);
        confirmDelete.getStyleClass().add("categorie-deleting-bar");


        Label errorLabel = new Label("Impossible de supprimer cet emloye !");
        errorLabel.setStyle("-fx-font-size: 18; -fx-text-fill: white");

        initSelecting(tobeDeleted, tobeDeletedCategories, selected, deleteAllSelectionIsActive, selectAll, employe, stackPane, rectangle, null, edit, delete, null, select, actionBar, ((TextFlow) warning.getGraphic()), warning, confirmDelete , errorLabel);


        delete.setOnAction(event -> {
            stackPane.getChildren().remove(actionBar);
            stackPane.getChildren().add(confirmDelete);

        });


        yes.setOnAction(event -> {

            try {
                if (EmployeManager.delete(employe))
                    platsContainer.getChildren().remove( stackPane );
                else {
                    stackPane.getChildren().remove(confirmDelete);
                    stackPane.getChildren().add(errorLabel);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
//                stackPane.getChildren().add(actionBar);
        });

        no.setOnAction(event -> {

            stackPane.getChildren().remove(confirmDelete);
            stackPane.getChildren().add(actionBar);
        });


//            stackPane.setOnMouseEntered( event ->  {
//                stackPane.getChildren().add(rectangle);
//                stackPane.getChildren().add(actionBar);
//
//
//
//            });
//            stackPane.setOnMouseExited( event ->  {
//
//                stackPane.getChildren().remove(errorLabel);
//
//                stackPane.getChildren().remove(rectangle);
//                stackPane.getChildren().remove(actionBar);
//                stackPane.getChildren().remove(confirmDelete);
//
//
//
//            });
        return stackPane;
    }


    protected static VBox getDesc(Employe employe) throws SQLException {

        Label name = new Label(employe.getNom() + " " + employe.getPrenom());
        name.getStyleClass().add("name");


        Label adresse = new Label(employe.getAdresse());
        adresse.getStyleClass().add("description");
        Label telephone = new Label(employe.getTelephone());
        adresse.getStyleClass().add("description");
        Label email = new Label(employe.getEmail());
        adresse.getStyleClass().add("description");

        Label qtags = new Label("tags : ");
        qtags.setStyle("-fx-font-size: 14");

        VBox vBox = new VBox(name, adresse, telephone, email);
        vBox.getStyleClass().add("disc-category");
        return vBox;
    }


}
