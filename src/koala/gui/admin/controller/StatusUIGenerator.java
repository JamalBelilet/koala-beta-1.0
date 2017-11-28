package koala.gui.admin.controller;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import koala.db.ConnectionManager;
import koala.gestionPlats.categorie.Categorie;
import koala.gestionPlats.categorie.CategorieManager;
import koala.gestionPlats.plat.Plat;
import koala.gestionPlats.plat.PlatManager;
import koala.statistiques.Statistique;
import strings.French;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by bjama on 5/6/2017.
 */
public class StatusUIGenerator {

    static XYChart.Series<Number, String> vmonthVend = new XYChart.Series<>();
    static XYChart.Series<Number, String> vweekVend = new XYChart.Series<>();
    static XYChart.Series<Number, String> vallVend = new XYChart.Series<>();
    static XYChart.Series<String, Number> hmonthVend = new XYChart.Series<>();
    static XYChart.Series<String, Number> hweekVend = new XYChart.Series<>();
    static XYChart.Series<String, Number> hallVend = new XYChart.Series<>();

    static CategoryAxis hxAxis = new CategoryAxis();
    static NumberAxis hyAxis = new NumberAxis();
    static CategoryAxis vxAxis = new CategoryAxis();
    static NumberAxis vyAxis = new NumberAxis();

    static BarChart<Number, String> vBarChart = new BarChart<>(vyAxis, vxAxis);
    static BarChart<String, Number> hBarChart = new BarChart<>(hxAxis, hyAxis);


    static Label titleLabel = new Label();


    public static Connection connection = ConnectionManager.getInstance().getConnection();

    public static void getStatusForCategorie(StackPane centerStack) throws SQLException {

        init(centerStack, true);

        titleLabel.setText("Ventes par catégorie");
        for (Categorie categorie :
                CategorieManager.loadTableToList()) {
            hmonthVend.getData().add(new XYChart.Data<>(categorie.getNom(), Statistique.categorieCounterLastDays(categorie.getIdCategorie(), 30)));

        }

        for (Categorie categorie :
                CategorieManager.loadTableToList()) {
            hweekVend.getData().add(new XYChart.Data<>(categorie.getNom(), Statistique.categorieCounterLastDays(categorie.getIdCategorie(), 7)));

        }

        for (Categorie categorie :
                CategorieManager.loadTableToList()) {
            hallVend.getData().add(new XYChart.Data<>(categorie.getNom(), Statistique.categorieCounterAll(categorie.getIdCategorie())));

        }


    }

    public static void getStatusForPlat(StackPane centerStack) throws SQLException {

        List<Plat> plats = PlatManager.loadTableToList();

        init(centerStack, false);

        titleLabel.setText("Ventes des plats");

        int count = 0;
        for (Plat plat :
                plats ) {
            vmonthVend.getData().add(new XYChart.Data<>(Statistique.platCounterLastDays(plat.getIdPlat(), 30), plat.getNom()));

//            if (count++ > 6) break;
        }

        count = 0;
        for (Plat plat :
                plats) {

            vweekVend.getData().add(new XYChart.Data<>(Statistique.platCounterLastDays(plat.getIdPlat(), 7), plat.getNom()));
//            if (count++ > 6) break;

        }

        count = 0;

        for (Plat plat :
                plats) {
            vallVend.getData().add(new XYChart.Data<>(Statistique.platCounterAll(plat.getIdPlat()), plat.getNom()));
//            if (count++ > 6) break;


        }


    }

    private static void init(StackPane centerStack, boolean isHorizontal) {
        Rectangle rect = new Rectangle(centerStack.getScene().getWidth()- 245, centerStack.getScene().getHeight() -135, Color.rgb(0,0,0,.6));

        rect.getStyleClass().add("stats-rect");
        centerStack.getChildren().add(rect);


        Button close = new Button(null);
        close.getStyleClass().add("close_enCours_");
        close.setTooltip(new Tooltip("close"));


        HBox closeContainer = new HBox(0, close);
        closeContainer.setAlignment(Pos.TOP_RIGHT);

        titleLabel.setStyle("-fx-font-size: 24; -fx-min-height: 50");
        HBox titleLabelContainer = new HBox(titleLabel);
        titleLabelContainer.setPadding(new Insets(0 ,0, 0, 10));
        titleLabelContainer.setAlignment(Pos.TOP_LEFT);


        Button month = new Button(French.ADMIN_STATISTIQUES_AUJOURDHUI);
        Button week = new Button(French.ADMIN_STATISTIQUES_MOIS);
        Button all = new Button(French.ADMIN_STATISTIQUES_TOUJOURS);
        HBox chartActionBar = new HBox(15, month, week, all);

        chartActionBar.setPadding(new Insets(0, 0, 0, 20));


        vBarChart.setLegendVisible(false);
        hBarChart.setLegendVisible(false);



        month.setOnAction(event -> {
            if (isHorizontal)
                grup(week, all, month, hBarChart, hmonthVend);
            else
                grup(week, all, month, vBarChart, vmonthVend, false);

        });

        week.setOnAction(event -> {
            if (isHorizontal)
                grup(month, all, week, hBarChart, hweekVend);
            else
                grup(month, all, week, vBarChart, vweekVend, false);

        });

        all.setOnAction(event -> {
            if (isHorizontal)
                grup(month, week, all, hBarChart, hallVend);
            else
                grup(month, week, all, vBarChart, vallVend, false);
        });




        hBarChart.getData().clear();
        hBarChart.layout();
        vBarChart.getData().clear();
        vBarChart.layout();

        all.getStyleClass().add("active");

        VBox vBox;
        vmonthVend.getData().clear();
        vallVend.getData().clear();
        vweekVend.getData().clear();
        vBarChart.getData().add(vallVend);
        vBarChart.setTitle(null);
        hmonthVend.getData().clear();
        hallVend.getData().clear();
        hweekVend.getData().clear();
        hBarChart.getData().add(hallVend);
        hBarChart.setTitle(null);

        HBox hBox = new HBox(25, titleLabelContainer, chartActionBar);
        if (isHorizontal) {
            vBox = new VBox(closeContainer, hBox, hBarChart);
        } else {
            ScrollPane scrollPane = new ScrollPane(vBarChart);
            vBox = new VBox(closeContainer, hBox, scrollPane);
            vBarChart.setPrefHeight(1200);
            vyAxis.setSide(Side.TOP);
        }


        VBox vBoxContainer = new VBox(vBox);
        centerStack.getChildren().add(vBoxContainer);


        StackPane.setAlignment(vBoxContainer, Pos.CENTER);
        vBox.setSpacing(5);
        vBoxContainer.getStyleClass().addAll("edit-form", "no-max");

        vBoxContainer.setMaxHeight(centerStack.getScene().getHeight() -170);
        vBox.setMinHeight(centerStack.getScene().getHeight() -170);
        vBoxContainer.setMaxWidth(centerStack.getScene().getWidth() -287);

        rect.setHeight(centerStack.getScene().getHeight() -135 -7);
        rect.setWidth(centerStack.getScene().getWidth() -247);

        centerStack.getScene().heightProperty().addListener(observable -> {
            vBoxContainer.setMaxHeight(centerStack.getScene().getHeight() -180);
            vBox.setMinHeight(centerStack.getScene().getHeight() -180);
            rect.setHeight(centerStack.getScene().getHeight() -135 -7);
        });
        centerStack.getScene().widthProperty().addListener(observable -> {
            rect.setWidth(centerStack.getScene().getWidth() -247);
            vBoxContainer.setMaxWidth(centerStack.getScene().getWidth() -287);

        });

        rect.setOnMouseClicked(event -> {
            centerStack.getChildren().removeAll(rect, vBoxContainer);
        });

        close.setOnAction( event ->  {
            centerStack.getChildren().removeAll(rect, vBoxContainer);
        });

        vBoxContainer.getStyleClass().add("status-container");


    }

    private static void grup(Button disOne, Button disTwo, Button actOne, BarChart<String, Number> barChart, XYChart.Series<String, Number> xVend) {
        barChart.getData().setAll(xVend);
        disOne.getStyleClass().remove("active");
        disTwo.getStyleClass().remove("active");
        actOne.getStyleClass().remove("active");
        actOne.getStyleClass().add("active");
    }
    private static void grup(Button disOne, Button disTwo, Button actOne, BarChart<Number, String> barChart, XYChart.Series<Number, String> xVend, boolean ex) {
        barChart.getData().setAll(xVend);
        disOne.getStyleClass().remove("active");
        disTwo.getStyleClass().remove("active");
        actOne.getStyleClass().remove("active");
        actOne.getStyleClass().add("active");
    }



    public static void initPlatStatus(StackPane centerStack, HBox hBox, Plat plat) {
        VBox vBox = initUniStatus(centerStack);
        vBox.getChildren().add(hBox);


    }


    private static VBox initUniStatus(StackPane centerStack) {
        Rectangle rect = new Rectangle(centerStack.getScene().getWidth()- 245, centerStack.getScene().getHeight() -135, Color.rgb(0,0,0,.6));



        rect.getStyleClass().add("stats-rect");

        centerStack.getChildren().add(rect);



        Button close = new Button(null);
        close.getStyleClass().add("close_enCours_");
        close.setTooltip(new Tooltip("close"));
        HBox closeContainer = new HBox(15, close);
        closeContainer.setAlignment(Pos.TOP_RIGHT);


        VBox vBox = new VBox(closeContainer);

        centerStack.getChildren().add(vBox);


        StackPane.setAlignment(vBox, Pos.CENTER);
        vBox.setSpacing(5);
        vBox.getStyleClass().addAll("edit-form", "no-max");

        vBox.setMaxHeight(centerStack.getScene().getHeight() -170);
        vBox.setMaxWidth(centerStack.getScene().getWidth() -287);

        rect.setHeight(centerStack.getScene().getHeight() -135 -7);
        rect.setWidth(centerStack.getScene().getWidth() -247);

        centerStack.getScene().heightProperty().addListener(observable -> {
            vBox.setMaxHeight(centerStack.getScene().getHeight() -180);
            rect.setHeight(centerStack.getScene().getHeight() -135 -7);
        });
        centerStack.getScene().widthProperty().addListener(observable -> {
            rect.setWidth(centerStack.getScene().getWidth() -247);
            vBox.setMaxWidth(centerStack.getScene().getWidth() -287);

        });

        rect.setOnMouseClicked(event -> {
            centerStack.getChildren().removeAll(rect, vBox);
        });

        close.setOnAction( event ->  {
            centerStack.getChildren().removeAll(rect, vBox);
        });
        vBox.getStyleClass().add("status-container");

        return vBox;
    }

}
