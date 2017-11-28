package koala.gui.admin.controller;

import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import koala.gestionPlats.platProposer.PlatProposer;
import koala.gestionPlats.platProposer.PlatProposerManager;
import strings.French;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PlatProposerUIGenerator extends PlatUIGenerateur {

    public PlatProposerUIGenerator() throws SQLException {
    }

    public static void appendPlats(StackPane centerStack, FlowPane platsContainer, Pane actionBarForAll) throws SQLException {

        platsContainer.getChildren().clear();


        ArrayList<StackPane> tobeDeleted = new ArrayList<>();
        ArrayList<Object> tobeDeletedCategories = new ArrayList<>();
        ArrayList<CheckBox> selected = new ArrayList<>();

        final boolean[] deleteAllSelectionIsActive = {false};
        CheckBox selectAll = new CheckBox("Tout sélectionner");
        selectAll.getStyleClass().addAll("select-all", "select");


        platsContainer.getStyleClass().add("plats");
        List<PlatProposer> platsProposer = PlatProposerManager.loadTableToList();
        System.out.println(platsProposer.size());
        for (PlatProposer platProposer :
                platsProposer) {

            Image image = null;
            if (platProposer.getImage() != null)
                image = new Image(platProposer.getImage().getBinaryStream()) ;

            ImageView imageView = new ImageView(image);
            imageView.setFitHeight(200);
            imageView.setPreserveRatio(true);

            ScrollPane imageHack = new ScrollPane(imageView);
            imageHack.setPrefHeight(185);
            imageHack.setPrefWidth(185);
            imageHack.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            imageHack.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);



            VBox vBox = getDesc(platProposer);
            vBox.getStyleClass().add("disc-category");

            HBox hBox = new HBox(imageHack, vBox);
            hBox.getStyleClass().add("one-category");
            StackPane stackPane = new StackPane(hBox);

            Rectangle rectangle = new Rectangle(450, 185, Color.valueOf("rgba(0,0,0,.7)"));

            Button delete = new Button(French.ADMIN_SUPPRIMER);
            delete.getStyleClass().add("delete");

            CheckBox select = new CheckBox("Sélectionner");
            select.getStyleClass().add("select");

            selected.add(select);


            HBox actionBar = new HBox(delete);
            actionBar.getStyleClass().add("categorie-vction-bar");

            Text platNameText = new Text(platProposer.getNom());
            platNameText.setStyle("-fx-font-weight: bold;");
            Label warning = new Label(null, new TextFlow(new Text(French.ADMIN_SUPPRIMER_QUESTION), platNameText, new Text(" ?")));
            warning.getStyleClass().add("warning");

            Button yes = new Button("Oui");
            yes.getStyleClass().add("yes");

            Button no = new Button("Non");
            no.getStyleClass().add("no");

            HBox deleteChoice = new HBox(yes, no);
            VBox confirmDelete = new VBox(warning, deleteChoice);
            confirmDelete.getStyleClass().add("categorie-deleting-bar");

            initSelecting(tobeDeleted, tobeDeletedCategories, selected, deleteAllSelectionIsActive, selectAll, platProposer, stackPane, rectangle, null, null, delete, null, select, actionBar, ((TextFlow) warning.getGraphic()), warning, confirmDelete, null);

            delete.setOnAction(event -> {
                stackPane.getChildren().remove(actionBar);
                stackPane.getChildren().add(confirmDelete);

            });

            yes.setOnAction(event -> {
                try {
                    if (PlatProposerManager.delete(platProposer.getIdPlatProposer())) {
                        platsContainer.getChildren().remove( stackPane );
                        if (platsProposer.size() == 0 ) {
                            actionBarForAll.getChildren().clear();
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            no.setOnAction(event -> {

                stackPane.getChildren().remove(confirmDelete);
                stackPane.getChildren().add(actionBar);
            });


            platsContainer.getChildren().add( stackPane );
        }

        Button deleteAll = new Button();
        deleteAll.getStyleClass().add("deleteAll");


        actionBarForAll.getChildren().clear();

        if (platsProposer.size() > 0 ) {
            actionBarForAll.getChildren().addAll(deleteAll);

            actionBarForAll.getStyleClass().add("categorie-vction-bar");
            actionBarForAll.getStyleClass().add("categorie-top-bar");
        }

        initdeleteOptions(centerStack, platsContainer, actionBarForAll, tobeDeleted, tobeDeletedCategories, selected, deleteAllSelectionIsActive, selectAll, deleteAll, null);

    }

    protected static VBox getDesc(PlatProposer platProposer) throws SQLException {
        Label name = new Label(platProposer.getNom());

        name.getStyleClass().add("name");

        Label description = new Label(platProposer.getDescription());
        description.getStyleClass().add("description");


        VBox desc = new VBox(name, description);
        return desc;
    }

}