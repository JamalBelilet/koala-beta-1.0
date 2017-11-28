package koala.gui.admin.controller;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import koala.gestionPlats.categorie.Categorie;
import koala.gestionPlats.plat.Plat;
import koala.gestionPlats.plat.PlatManager;
import koala.gestionPlats.tag.Tag;
import koala.statistiques.Statistique;
import strings.French;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PlatUIGenerateur extends CategorieUIGenerator {

    public PlatUIGenerateur() throws SQLException {
    }

    public static void appendPlats(StackPane centerStack, FlowPane platsContainer, Pane actionBarForAll, TreeView treeView) throws SQLException {

        platsContainer.getChildren().clear();
        ArrayList<StackPane> tobeDeleted = new ArrayList<>();
        ArrayList<Object> tobeDeletedCategories = new ArrayList<>();
        ArrayList<CheckBox> selected = new ArrayList<>();

        final boolean[] deleteAllSelectionIsActive = {false};
        CheckBox selectAll = new CheckBox("Tout sélectionner");
        selectAll.getStyleClass().addAll("select-all", "select");


        platsContainer.getStyleClass().add("plats");
        List<Plat> plats = getPlats();
        for (Plat plat :
                plats) {
            StackPane stackPane = getStackPane(centerStack, platsContainer, tobeDeleted, tobeDeletedCategories, selected, deleteAllSelectionIsActive, selectAll, plat, treeView);


            platsContainer.getChildren().add( stackPane );
        }

        Button statusAll = new Button();
        statusAll.getStyleClass().add("statusAll");

        Button addAll = new Button();
        addAll.getStyleClass().add("addAll");

        addAll.setOnAction(event -> {
            FormesUIGenerator.getEditForCategorieForm(centerStack, new Plat(), false, platsContainer, tobeDeleted, tobeDeletedCategories, selected, deleteAllSelectionIsActive, selectAll, null, treeView);
        });
        statusAll.setOnAction(event -> {
            try {
                StatusUIGenerator.getStatusForPlat(centerStack);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        Button deleteAll = new Button();
        deleteAll.getStyleClass().add("deleteAll");


        actionBarForAll.getChildren().clear();
        actionBarForAll.getChildren().addAll(statusAll, addAll, deleteAll);

        actionBarForAll.getStyleClass().add("categorie-vction-bar");
        actionBarForAll.getStyleClass().add("categorie-top-bar");


        initdeleteOptions(centerStack, platsContainer, actionBarForAll, tobeDeleted, tobeDeletedCategories, selected, deleteAllSelectionIsActive, selectAll, deleteAll, treeView);



    }

    static StackPane getStackPane(StackPane centerStack, FlowPane platsContainer, ArrayList<StackPane> tobeDeleted, ArrayList<Object> tobeDeletedCategories, ArrayList<CheckBox> selected, boolean[] deleteAllSelectionIsActive, CheckBox selectAll, Plat plat, TreeView treeView) throws SQLException {
        Image image = null;
        if (plat.getImage() != null)
            image = new Image(plat.getImage().getBinaryStream()) ;

        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(200);
        imageView.setPreserveRatio(true);

        ScrollPane imageHack = new ScrollPane(imageView);
        imageHack.setPrefHeight(185);
        imageHack.setPrefWidth(185);
        imageHack.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        imageHack.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);


        VBox vBox = getDesc(plat);

        HBox hBox = new HBox(imageHack, vBox);
        hBox.getStyleClass().add("one-category");
        StackPane stackPane = new StackPane(hBox);

        Rectangle rectangle = new Rectangle(450, 185, Color.valueOf("rgba(0,0,0,.7)"));


        Button status = new Button(French.ADMIN_STATS);
        status.getStyleClass().add("status");

        Button edit = new Button(French.ADMIN_MODIFIER);
        edit.getStyleClass().add("edit");

        edit.setOnAction(event -> {
            FormesUIGenerator.getEditForCategorieForm(centerStack, plat, true, platsContainer, tobeDeleted, tobeDeletedCategories, selected, deleteAllSelectionIsActive, selectAll, stackPane, treeView);
        });

        Button disable = new Button("Désactiver");
        disable.getStyleClass().add("disable");

        Button delete = new Button(French.ADMIN_SUPPRIMER);
        delete.getStyleClass().add("delete");

        CheckBox select = new CheckBox("Sélectionner");
        select.getStyleClass().add("select");

        selected.add(select);


        HBox actionBar = new HBox();
        actionBar.getStyleClass().add("categorie-vction-bar");


        Text platNameText = new Text(plat.getNom());
        platNameText.setStyle("-fx-font-weight: bold;");
        Label warning = new Label(null, new TextFlow(new Text(French.ADMIN_SUPPRIMER_QUESTION), platNameText, new Text(" ?")));
        warning.getStyleClass().add("warning");

        Button yes = new Button("Oui");
        disable.getStyleClass().add("yes");

        Button no = new Button("Non");
        delete.getStyleClass().add("no");


        HBox deleteChoice = new HBox(yes, no);
        VBox confirmDelete = new VBox(warning, deleteChoice);
        confirmDelete.getStyleClass().add("categorie-deleting-bar");


        ImageView _disable = new ImageView(new Image("img/disabled.png"));
        _disable.setFitHeight(185);
        _disable.setFitWidth(450 );


        Label errorLabel = new Label("Impossible de supprimer ce plat !");

        errorLabel.setStyle("-fx-font-size: 18; -fx-text-fill: white");

        initSelecting(tobeDeleted, tobeDeletedCategories, selected, deleteAllSelectionIsActive, selectAll, plat, stackPane, rectangle, status, edit, delete, disable, select, actionBar, ((TextFlow) warning.getGraphic()), warning, confirmDelete, errorLabel);
        initStatus(plat, imageHack, hBox, stackPane, rectangle, status, select, actionBar, confirmDelete, errorLabel, _disable);


        yes.setOnAction(event -> {
            try {
                if (PlatManager.hasCommandeUnitaire(plat.getIdPlat())) {

                    stackPane.getChildren().remove(confirmDelete);
                    stackPane.getChildren().add(errorLabel);
//                        stackPane.getChildren().add(actionBar);


                } else {
                    PlatManager.deleteWell(plat.getIdPlat());
                    platsContainer.getChildren().remove( stackPane );


                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        delete.setOnAction(event -> {
            stackPane.getChildren().remove(actionBar);
            stackPane.getChildren().add(confirmDelete);

        });

        no.setOnAction(event -> {

            stackPane.getChildren().remove(confirmDelete);
            stackPane.getChildren().add(actionBar);
        });



        if (!plat.isDisponible()) {

            disable.setText("Activer");
            disable.getStyleClass().add("enable");

            stackPane.getChildren().add(_disable);
        }


        disable.setOnAction(event -> {

            if (plat.isDisponible()) {


                disable.setText("Activer");
                disable.getStyleClass().add("enable");
                try {
                    plat.setDisponible(false);

                    PlatManager.update(plat);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                stackPane.getChildren().add(stackPane.getChildren().size() - 2, _disable);
            } else {

                try {
                    plat.setDisponible(true);
                    PlatManager.update(plat);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                stackPane.getChildren().remove(_disable);
                disable.setText("Désactiver");
                disable.getStyleClass().remove("enable");

                stackPane.getChildren().remove(_disable);
            }


        });
        return stackPane;
    }

    public static void initStatus(Object objPlatCategorie, ScrollPane imageHack, HBox hBox, StackPane stackPane, Rectangle rectangle, Button status, CheckBox select, HBox actionBar, VBox confirmDelete, Label errorLabel, ImageView _disable) {
        status.setOnAction(event -> {

            if (status.getText().equals(French.ADMIN_STATS)) {


                stackPane.getChildren().remove(rectangle);
                stackPane.getChildren().remove(actionBar);
                stackPane.getChildren().remove(confirmDelete);

                stackPane.getChildren().remove(errorLabel);



                stackPane.setPrefHeight(395);
                stackPane.setPrefWidth(925);
//                stackPane.setOnMouseEntered(null);
                imageHack.setPrefWidth(250);
                imageHack.setMaxHeight(200);

                if (_disable != null) {
                    _disable.setFitWidth(925);
                    StackPane.setAlignment(_disable, Pos.TOP_LEFT);
                }


                rectangle.setWidth(925);
                StackPane.setAlignment(rectangle, Pos.TOP_LEFT);
                StackPane.setAlignment(actionBar, Pos.TOP_CENTER);
                StackPane.setMargin(actionBar, new Insets(75, 0, 0, 0));


                try {
                    StackPane.setAlignment(errorLabel, Pos.TOP_CENTER);
                    StackPane.setMargin(errorLabel, new Insets(75, 0, 0, 0));
                } catch (NullPointerException e) {

                }

                StackPane.setAlignment(confirmDelete, Pos.CENTER);
                StackPane.setMargin(confirmDelete, new Insets(-160, 0, 0, 0));
                stackPane.setStyle("-fx-background-color: rgba(0,0,0,.1)");
                select.getStyleClass().add("inStatus");

                try {
                    if (objPlatCategorie instanceof Plat) {
                        stackPane.getChildren().add(new VBox(hBox, generateLineChart((Plat) objPlatCategorie)));
                        if (_disable != null && !((Plat) objPlatCategorie).isDisponible()) {
                            stackPane.getChildren().remove(_disable);
                            stackPane.getChildren().add(_disable);

                        }


                    } else
                        stackPane.getChildren().add(new VBox(hBox, generateLineChart( (Categorie) objPlatCategorie )));
                } catch (SQLException e) {
                    e.printStackTrace();
                }


                status.setText("Réduire");
                status.getStyleClass().add("collapse");

            } else if (status.getText().equals("Réduire")) {

                stackPane.setPrefHeight(185);
                stackPane.setPrefWidth(450);

                imageHack.setPrefWidth(185);
                imageHack.setMaxHeight(185);



                if (_disable != null) {
                    _disable.setFitWidth(450);
                    StackPane.setAlignment(_disable, Pos.CENTER);

                }

                rectangle.setWidth(450);
                StackPane.setAlignment(rectangle, Pos.CENTER);
                StackPane.setAlignment(actionBar, Pos.CENTER);
                StackPane.setMargin(actionBar, null);

                try {
                    StackPane.setAlignment(errorLabel, Pos.CENTER);
                    StackPane.setMargin(errorLabel, null);

                } catch (NullPointerException e) {

                }


                StackPane.setAlignment(confirmDelete, Pos.CENTER);
                StackPane.setMargin(confirmDelete, null);
                stackPane.setStyle("-fx-background-color: transparent");
                select.getStyleClass().remove("inStatus");

                stackPane.getChildren().clear();

                stackPane.getChildren().add(hBox);
                if (_disable != null  && !((Plat) objPlatCategorie).isDisponible()) {
                    stackPane.getChildren().remove(_disable);

                    stackPane.getChildren().add(_disable);


                }
                status.setText(French.ADMIN_STATS);

                status.getStyleClass().remove("collapse");

            }
        });
    }

    private static LineChart<String, Number>  generateLineChart(Plat plat) throws SQLException {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();

        LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);

        lineChart.setLegendVisible(false);

        XYChart.Series<String, Number> nVend = new XYChart.Series<>();

        nVend.getData().add(new XYChart.Data<>(LocalDate.now().plusDays(-6).getDayOfWeek().name(), Statistique.platCounterAday(plat.getIdPlat(), LocalDateTime.now().plusDays(-6))));
        nVend.getData().add(new XYChart.Data<>(LocalDate.now().plusDays(-5).getDayOfWeek().name(), Statistique.platCounterAday(plat.getIdPlat(), LocalDateTime.now().plusDays(-5))));
        nVend.getData().add(new XYChart.Data<>(LocalDate.now().plusDays(-4).getDayOfWeek().name(), Statistique.platCounterAday(plat.getIdPlat(), LocalDateTime.now().plusDays(-4))));
        nVend.getData().add(new XYChart.Data<>(LocalDate.now().plusDays(-3).getDayOfWeek().name(), Statistique.platCounterAday(plat.getIdPlat(), LocalDateTime.now().plusDays(-3))));
        nVend.getData().add(new XYChart.Data<>(LocalDate.now().plusDays(-2).getDayOfWeek().name(), Statistique.platCounterAday(plat.getIdPlat(), LocalDateTime.now().plusDays(-2))));
        nVend.getData().add(new XYChart.Data<>(LocalDate.now().plusDays(-1).getDayOfWeek().name(), Statistique.platCounterAday(plat.getIdPlat(), LocalDateTime.now().plusDays(-1))));

        XYChart.Data<String, Number> todayReccete = new XYChart.Data<>(LocalDate.now().getDayOfWeek().name(), Statistique.platCounterToday(plat.getIdPlat()));
        nVend.getData().add(todayReccete);


        lineChart.getData().add(nVend);
        lineChart.setTitle("Vente du plat '" + plat.getNom() + "' cette semaine");

        return lineChart;
    }

    private static LineChart<String, Number> generateLineChart(Categorie categorie) throws SQLException {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();

        LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);

        lineChart.setLegendVisible(false);

        XYChart.Series<String, Number> nVend = new XYChart.Series<>();

        nVend.getData().add(new XYChart.Data<>(LocalDate.now().plusDays(-6).getDayOfWeek().name(), Statistique.categorieCounterLastDays(categorie.getIdCategorie(), -6)));
        nVend.getData().add(new XYChart.Data<>(LocalDate.now().plusDays(-5).getDayOfWeek().name(), Statistique.categorieCounterLastDays(categorie.getIdCategorie(), -5)));
        nVend.getData().add(new XYChart.Data<>(LocalDate.now().plusDays(-4).getDayOfWeek().name(), Statistique.categorieCounterLastDays(categorie.getIdCategorie(), -4)));
        nVend.getData().add(new XYChart.Data<>(LocalDate.now().plusDays(-3).getDayOfWeek().name(), Statistique.categorieCounterLastDays(categorie.getIdCategorie(), -3)));
        nVend.getData().add(new XYChart.Data<>(LocalDate.now().plusDays(-2).getDayOfWeek().name(), Statistique.categorieCounterLastDays(categorie.getIdCategorie(), -2)));
        nVend.getData().add(new XYChart.Data<>(LocalDate.now().plusDays(-1).getDayOfWeek().name(), Statistique.categorieCounterLastDays(categorie.getIdCategorie(), -1)));

        XYChart.Data<String, Number> todayReccete = new XYChart.Data<>(LocalDate.now().getDayOfWeek().name(), Statistique.categorieCounterToday(categorie.getIdCategorie()));
        nVend.getData().add(todayReccete);


        lineChart.getData().add(nVend);
        lineChart.setTitle(categorie.getNom() + " plats count per day");

        return lineChart;
    }


    protected static List<Plat> getPlats() throws SQLException {
        return PlatManager.loadTableToList();
    }

    protected static VBox getDesc(Plat plat) throws SQLException {
        Label name = new Label(plat.getNom());


        name.getStyleClass().add("name");
        Label description = new Label(plat.getDescription());
        description.getStyleClass().add("description");

        Text counter = new Text(Integer.toString(Statistique.platCounterAll(plat.getIdPlat())));
        counter.setStyle("-fx-font-weight: bold; -fx-text-fill: #4D4D4D");

        Label platSize = new Label( null, new TextFlow(new Text("Vendu "), counter , new Text(" fois")));
        platSize.getStyleClass().add("categorieSize");

//


        Label qtags = new Label("tags : ");
        qtags.setStyle("-fx-font-size: 14");

        TilePane tags = new TilePane();
        for (Tag tagString:
                plat.getTags()) {

            Label tag = new Label(tagString.getNom());
            tag.setStyle("-fx-font-weight: bold;");

            tags.getChildren().add(tag);
        }
        if (plat.getTags().size() == 0)
            qtags.setText(null);

        VBox tagsContainer = new VBox(qtags, tags);

        tags.getStyleClass().add("tags");

        VBox vBox = new VBox(name, description, platSize, tagsContainer);
        vBox.getStyleClass().add("disc-category");
        return vBox;
    }

}
