package koala.gui.admin.controller;

import javafx.event.ActionEvent;
import javafx.geometry.Pos;
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
import koala.gestionPlats.categorie.Categorie;
import koala.gestionPlats.categorie.CategorieManager;
import koala.gestionPlats.plat.Plat;
import koala.gestionPlats.plat.PlatManager;
import koala.gestionPlats.platProposer.PlatProposer;
import koala.gestionPlats.platProposer.PlatProposerManager;
import koala.statistiques.Statistique;
import strings.French;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by bjama on 4/16/2017.
 */
public class CategorieUIGenerator {
    static ArrayList<Categorie> initArrayOfCatigories;
    static {
        try {
            initArrayOfCatigories = new ArrayList<>(CategorieManager.loadTableToList());
            System.out.println("called #34 categorieUigenerator");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void initCategories(StackPane centerStack, FlowPane categories, Pane actionBarForAll, TreeView treeView) throws SQLException {
        categories.getChildren().clear();
        ArrayList<StackPane> tobeDeleted = new ArrayList<>();
        ArrayList<Object> tobeDeletedCategories = new ArrayList<>();
        ArrayList<CheckBox> selected = new ArrayList<>();



        final boolean[] deleteAllSelectionIsActive = {false};

        CheckBox selectAll = new CheckBox("Tout sélectionner");
        selectAll.getStyleClass().addAll("select-all", "select");

        for (Categorie categorie :
                initArrayOfCatigories) {
            StackPane stackPane = getStackPane(centerStack, categories, tobeDeleted, tobeDeletedCategories, selected, deleteAllSelectionIsActive, selectAll, categorie, treeView);





            categories.getChildren().add( stackPane );

        }

        Button statusAll = new Button();
        statusAll.getStyleClass().add("statusAll");
        statusAll.setTooltip(new Tooltip(French.ADMIN_STATS));

        Button addAll = new Button();
        addAll.getStyleClass().add("addAll");
        addAll.setTooltip(new Tooltip("add new\ncategorie"));


        addAll.setOnAction(event -> {
            FormesUIGenerator.getEditForCategorieForm(centerStack, new Categorie(), false, categories, tobeDeleted, tobeDeletedCategories, selected, deleteAllSelectionIsActive, selectAll, null, treeView);
        });
        statusAll.setOnAction(event -> {
            try {
                StatusUIGenerator.getStatusForCategorie(centerStack);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

//            Button disableAll = new Button();
//            disableAll.getStyleClass().add("disableAll");

        Button deleteAll = new Button();
        deleteAll.getStyleClass().add("deleteAll");
        deleteAll.setTooltip(new Tooltip(French.ADMIN_SUPPRIMER));


        actionBarForAll.getChildren().clear();
        actionBarForAll.getChildren().addAll(statusAll, addAll, deleteAll);

        actionBarForAll.getStyleClass().add("categorie-vction-bar");
        actionBarForAll.getStyleClass().add("categorie-top-bar");


        initdeleteOptions(centerStack, categories, actionBarForAll, tobeDeleted, tobeDeletedCategories, selected, deleteAllSelectionIsActive, selectAll, deleteAll, treeView);


        

    }

    static StackPane getStackPane(StackPane centerStack, FlowPane categories, ArrayList<StackPane> tobeDeleted, ArrayList<Object> tobeDeletedCategories, ArrayList<CheckBox> selected, boolean[] deleteAllSelectionIsActive, CheckBox selectAll, Categorie categorie, TreeView treeView) throws SQLException {
        Image image = null;
        if (categorie.getImage() != null)
            image = new Image(categorie.getImage().getBinaryStream()) ;

        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(200);
        imageView.setPreserveRatio(true);


        ScrollPane imageHack = new ScrollPane(imageView);
        imageHack.setPrefHeight(185);
        imageHack.setPrefWidth(185);
        imageHack.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        imageHack.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);


        Label name = new Label(categorie.getNom());
        name.getStyleClass().add("name");
        Label description = new Label(categorie.getDescription());
        description.getStyleClass().add("description");

        Text categorieSizeInt = new Text(Integer.toString(PlatManager.platsByCategorie(categorie.getIdCategorie()).size()));
        categorieSizeInt.setStyle("-fx-font-weight: bold; -fx-text-fill: #4D4D4D");

        Label categorieSize = new Label( null, new TextFlow(new Text("Les plats disponibles : "), categorieSizeInt));
        categorieSize.getStyleClass().add("categorieSize");

        Label qTopPlats = new Label("Le plat star : ");
        qTopPlats.setStyle("-fx-font-size: 14");

        HBox topPlats = new HBox();
        try {
            for (Plat plat :
                    Statistique.platTopSubInCategory(categorie.getIdCategorie(), 0, 1)) {
                Text counter = new Text(Integer.toString(Statistique.platCounterAll(plat.getIdPlat())));
                counter.setStyle("-fx-font-weight: bold;");

                Label topPlat = new Label(null, new TextFlow(new Text(plat.getNom() + " "), counter));
                topPlats.getChildren().add(topPlat);
            }
        } catch (NullPointerException ex) {
            System.err.println("ghiles nullPointerException");
            qTopPlats.setText(null);
        }
//            Label topPlat_1 = new Label("plat 1");
//            Label topPlat_2 = new Label("plat 2");
//            Label topPlat_3 = new Label("plat 3");
//            HBox topPlats = new HBox(topPlat_1, topPlat_2, topPlat_3);
        VBox topPlatsContainer = new VBox(qTopPlats, topPlats);

        topPlats.getStyleClass().add("topPlats");


        VBox vBox = new VBox(name, description, categorieSize, topPlatsContainer);
        vBox.getStyleClass().add("disc-category");

        HBox hBox = new HBox(imageHack, vBox);


        hBox.getStyleClass().add("one-category");
        StackPane stackPane = new StackPane(hBox);

        Rectangle rectangle = new Rectangle(450, 185, Color.valueOf("rgba(0,0,0,.7)"));


        Button status = new Button(French.ADMIN_STATS);
        status.getStyleClass().add("status");

        Button edit = new Button(French.ADMIN_MODIFIER);
        edit.setOnAction(event -> {
            FormesUIGenerator.getEditForCategorieForm(centerStack, categorie, true, categories, tobeDeleted, tobeDeletedCategories, selected, deleteAllSelectionIsActive, selectAll, stackPane, treeView);
        });
        edit.getStyleClass().add("edit");

//            Button disable = new Button("Désactiver");
//            disable.getStyleClass().add("disable");

        Button delete = new Button(French.ADMIN_SUPPRIMER);
        delete.getStyleClass().add("delete");

        CheckBox select = new CheckBox("Sélectionner");
        select.getStyleClass().add("select");

        selected.add(select);

        HBox actionBar;
        if (categorie.getIdCategorie() != 0 )
            actionBar = new HBox(status, edit, delete);
        else
            actionBar = new HBox(status);


        actionBar.getStyleClass().add("categorie-vction-bar");


        Text categorieNameText = new Text(categorie.getNom());
        categorieNameText.setStyle("-fx-font-weight: bold;");
        TextFlow confirmerTextFlow = new TextFlow(new Text(French.ADMIN_SUPPRIMER_QUESTION), categorieNameText, new Text(" ?"));
        Label warning = new Label(null, confirmerTextFlow);
        warning.getStyleClass().add("warning");

        Button yes = new Button("yes");
        yes.getStyleClass().add("yes");

        Button no = new Button("no");
        no.getStyleClass().add("no");


        HBox deleteChoice = new HBox(yes, no);
        VBox confirmDelete = new VBox(warning, deleteChoice);
        confirmDelete.getStyleClass().add("categorie-deleting-bar");

        initSelecting(tobeDeleted, tobeDeletedCategories, selected, deleteAllSelectionIsActive, selectAll, categorie, stackPane, rectangle, status, edit, delete, null, select, actionBar, confirmerTextFlow, warning, confirmDelete, null);


        PlatUIGenerateur.initStatus(categorie, imageHack, hBox, stackPane, rectangle, status, select, actionBar, confirmDelete, null, null);


        delete.setOnAction(event -> {
            stackPane.getChildren().remove(actionBar);
            stackPane.getChildren().add(confirmDelete);

        });

        no.setOnAction(event -> {

            stackPane.getChildren().remove(confirmDelete);
            stackPane.getChildren().add(actionBar);
            warning.setGraphic(confirmerTextFlow);
            warning.setText(null);


        });

        yes.setOnAction(event -> {
            try {

                if (CategorieManager.hasCommandeUnitaire(categorie.getIdCategorie())) {
                    System.out.println("warning");

                    warning.setGraphic(null);
                    warning.setText("Impossible de supprimer la categorie avec ses plats ! Les plats seront deplacés vers la catégorie 'Autre'");
                    yes.setOnAction(event1 -> {
                        try {
                            CategorieManager.deleteWell(categorie.getIdCategorie(), 0);
                            categories.getChildren().remove( stackPane );
                            initArrayOfCatigories.remove(categorie);


                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    });


                } else {
                    CategorieManager.deleteWithPlats(categorie.getIdCategorie());
                    categories.getChildren().remove( stackPane );
                    initArrayOfCatigories.remove(categorie);

                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        });
        return stackPane;
    }

    protected static void initSelecting(ArrayList<StackPane> tobeDeleted, ArrayList<Object> tobeDeletedCategories, ArrayList<CheckBox> selected, boolean[] deleteAllSelectionIsActive, CheckBox selectAll, Object categorie, StackPane stackPane, Rectangle rectangle, Button status, Button edit, Button delete, Button disable, CheckBox select, HBox actionBar, TextFlow confirmerTextFlow, Label warning, VBox confirmDelete, Label errorLabel) {
        selectAll.addEventHandler(ActionEvent.ACTION, event -> {
            if (!selectAll.isSelected()) {
                for (StackPane stpn :
                        tobeDeleted) {

                    stpn.getChildren().remove(stpn.getChildren().size() - 1);
                    stpn.getChildren().remove(stpn.getChildren().size() - 1);
                }

                for (CheckBox slct :
                        selected) {
                    slct.setSelected(false);
                }
                tobeDeleted.clear();
                tobeDeletedCategories.clear();
            }

        });

        selectAll.selectedProperty().addListener(observable -> {
            try {

                if (selectAll.isSelected()) {


                    if (deleteAllSelectionIsActive[0]) {

                        actionBar.getChildren().clear();
                        actionBar.getChildren().add(select);
                        select.setSelected(true);

                    } else {
                        actionBar.getChildren().clear();
                        if (status != null)
                            actionBar.getChildren().add(status);
                        if (edit != null)
                            actionBar.getChildren().add(edit);
                        if (delete != null)
                            actionBar.getChildren().add(delete);
                        if (disable != null)
                            actionBar.getChildren().add(disable);


                    }
                    warning.setGraphic(confirmerTextFlow);
                    warning.setText(null);

                    stackPane.getChildren().add(rectangle);
                    stackPane.getChildren().add(actionBar);


                    tobeDeleted.add(stackPane);

                    tobeDeletedCategories.add( categorie);

                }


            } catch (IllegalArgumentException ill) {

                System.err.println("alredy seleted");
            }

        });


        select.setOnAction(event1 -> {
            if (selectAll.isSelected())
                selectAll.setSelected(false);

            if (select.isSelected()) {
                tobeDeleted.add(stackPane);

                tobeDeletedCategories.add((categorie));

//                if (categorie instanceof Categorie)
//                    tobeDeletedCategories.add(((Categorie) categorie));
//                else if (categorie instanceof Plat)
//                    tobeDeletedCategories.add(((Plat) categorie));

            } else {
                tobeDeleted.remove(stackPane);
                tobeDeletedCategories.remove(categorie);
            }
        });

        stackPane.setOnMouseEntered( event ->  {

            if (select.isSelected() && deleteAllSelectionIsActive[0]) {

            } else {
                if (deleteAllSelectionIsActive[0]) {

                    actionBar.getChildren().clear();
                    actionBar.getChildren().add(select);

                } else {
                    actionBar.getChildren().clear();

                    actionBar.getChildren().clear();
                    if (status != null)
                        actionBar.getChildren().add(status);
                    if (edit != null)
                        actionBar.getChildren().add(edit);
                    if (disable != null)
                        actionBar.getChildren().add(disable);
                    if (delete != null)
                        actionBar.getChildren().add(delete);


                }
                warning.setGraphic(confirmerTextFlow);
                warning.setText(null);

                stackPane.getChildren().add(rectangle);
                stackPane.getChildren().add(actionBar);
            }


        });
        stackPane.setOnMouseExited( event ->  {
            System.out.println("anything +++++++++++++++++++++++++++++");
            if (select.isSelected() && deleteAllSelectionIsActive[0]) {


            } else {
                stackPane.getChildren().remove(rectangle);
                stackPane.getChildren().remove(actionBar);
                stackPane.getChildren().remove(confirmDelete);

                stackPane.getChildren().remove(errorLabel);


            }


        });
    }

    protected static void initdeleteOptions(StackPane centerStack, FlowPane categories, Pane actionBarForAll, ArrayList<StackPane> tobeDeleted, ArrayList<Object> tobeDeletedCategories, ArrayList<CheckBox> selected, boolean[] deleteAllSelectionIsActive, CheckBox selectAll, Button deleteAll, TreeView treeView) {
        Button annulerAll = new Button("Annuler");
        annulerAll.getStyleClass().add("annuler-all");

        Button confirmAll = new Button("Confirmer");
        confirmAll.getStyleClass().add("confirmer-all");

        HBox deleteOptions = new HBox(selectAll, annulerAll, confirmAll);
        deleteOptions.setStyle("-fx-background-color: rgba(0, 0, 0, .7); -fx-background-radius: 15; -fx-spacing: 10; -fx-padding: 2 7;");


        deleteAll.setOnAction( event -> {

            deleteAllSelectionIsActive[0] = true;

            actionBarForAll.getChildren().remove(deleteAll);

            actionBarForAll.getChildren().add(deleteOptions);
//            for (Node categorie :
//                    categories.getChildren()) {
//
//
//
//            }


        });

        annulerAll.setOnAction(event -> {
            for (StackPane stackPane :
                    tobeDeleted) {
                stackPane.getChildren().remove(stackPane.getChildren().size() - 1);
                stackPane.getChildren().remove(stackPane.getChildren().size() - 1);
            }

            for (CheckBox slct :
                    selected) {
                slct.setSelected(false);
            }

            tobeDeleted.clear();
            tobeDeletedCategories.clear();
            deleteAllSelectionIsActive[0] = false;
            actionBarForAll.getChildren().add(deleteAll);
            actionBarForAll.getChildren().remove(deleteOptions);
            if (selectAll.isSelected())
                selectAll.setSelected(false);



        });
        StringBuilder log = new StringBuilder();
        Label logLabel = new Label();
        logLabel.getStyleClass().add("log-label");
        final int[] count = {0};
        Rectangle forLocRect = new Rectangle(centerStack.getScene().getWidth()- 245, centerStack.getScene().getHeight() -135, Color.rgb(0,0,0,.8));

        forLocRect.setOnMouseClicked(event -> {
            centerStack.getChildren().removeAll(forLocRect, logLabel);

        });

        confirmAll.setOnAction(event -> {
//            for (StackPane stackPane :
//                    tobeDeleted) {
//                categories.getChildren().remove( stackPane );
//            }
            log.delete(0, log.length());

            if (tobeDeletedCategories.size() > 0 ) {


                if (tobeDeletedCategories.get(0) instanceof Plat)
                    try {
                        PlatUIGenerateur.appendPlats(centerStack, categories, actionBarForAll, treeView);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                else if (tobeDeletedCategories.get(0) instanceof Categorie)
                    try {
                        CategorieUIGenerator.initCategories(centerStack, categories, actionBarForAll, treeView);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                else if (tobeDeletedCategories.get(0) instanceof PlatProposer)
                    try {
                        PlatProposerUIGenerator.appendPlats(centerStack, categories, actionBarForAll);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                else if (tobeDeletedCategories.get(0) instanceof Employe)
                    try {
                        EmployeUIGenerator.initEmplyes(centerStack, categories, actionBarForAll, treeView);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }


                for (Object ctgr :
                        tobeDeletedCategories) {
                    try {
                        if (ctgr instanceof Categorie) {

                            if (!CategorieManager.deleteWithPlats(((Categorie) ctgr).getIdCategorie())) {
                                log.append("\nimposible de supprimer la categorie "+ ctgr);
                            } else
                                count[0]++;

                        } else if (ctgr instanceof Plat) {
                            if (!PlatManager.deleteWell(((Plat) ctgr).getIdPlat())) {
                                log.append("\nimposible de supprimer le plat "+ ctgr);
                            } else
                                count[0]++;
                        } else if (ctgr instanceof PlatProposer) {
                            if (!PlatProposerManager.delete(((PlatProposer) ctgr).getIdPlatProposer())) {
                                log.append("\nimposible de supprimer le plat proposer "+ ctgr);
                            } else
                                count[0]++;
                        } else if (ctgr instanceof Employe) {
                            if (!EmployeManager.delete(((Employe) ctgr))) {
                                log.append("\nimposible de supprimer l'employe "+ ctgr);
                            } else
                                count[0]++;
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }



            }

            System.out.println(log);
            log.insert(0,  count[0]+" suppression(s) et " + (tobeDeletedCategories.size() - count[0]) + " erreur(s) !\n\n")
                    .append("\n\n");
            logLabel.setText(log.toString());
            centerStack.getChildren().addAll(forLocRect, logLabel);
            StackPane.setAlignment(logLabel, Pos.CENTER);




        });
    }


}
