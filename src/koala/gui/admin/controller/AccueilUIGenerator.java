package koala.gui.admin.controller;

import javafx.animation.Animation;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import koala.gestionPlats.plat.Plat;
import koala.gestionPlats.plat.PlatManager;
import koala.statistiques.Statistique;
import koala.statistiques.Suggestions;
import strings.French;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

/**
 * Created by bjama on 4/19/2017.
 */
public class AccueilUIGenerator {
    private static Timeline tl = new Timeline();
    private static Timer timer = new Timer();
    static ArrayList<String> deletedSuggestions = new ArrayList<>();


    public static void initAccuil(StackPane centerStack, ScrollPane centerScrollPane, FlowPane centerPane, HBox actionBarForAll) throws SQLException {
        List<Plat> liste = PlatManager.loadTableToList();


        centerPane.getChildren().clear();
        actionBarForAll.getChildren().clear();


        LineChart<String, Number> lineChart = getLineChart(liste);
        HBox lineChartContainer = new HBox(lineChart);


        StackPane suggestions =  getSuggestions();
        Label suggestionsLabel = new Label("Suggestions");
        suggestionsLabel.getStyleClass().add("suggestions-label");
        VBox suggestinsContainer = new VBox(suggestionsLabel, suggestions);
        System.out.println("the bool ------------------------------");


        resizeAccueil(centerPane, lineChart, suggestions);

        centerPane.sceneProperty().addListener(observable -> {

            resizeAccueil(centerPane, lineChart, suggestions);

        });


        VBox scrollStackContainer = new VBox(lineChartContainer, suggestinsContainer);
        scrollStackContainer.setSpacing(25);

        centerPane.getChildren().add( scrollStackContainer);


    }

    private static void resizeAccueil(FlowPane centerPane, LineChart<String, Number> lineChart, StackPane suggestions) {
        if (centerPane.getScene() != null) {
            suggestions.setPrefWidth(centerPane.getScene().getWidth() - 310);

            lineChart.setPrefWidth(centerPane.getScene().getWidth() - 310);

            centerPane.getScene().widthProperty().addListener(observable1 -> {
                suggestions.setPrefWidth(centerPane.getScene().getWidth() - 310);
                lineChart.setPrefWidth(centerPane.getScene().getWidth() - 310);
            });
            System.out.println("the bool sheet is here or there");
        }
    }

    private static StackPane getSuggestions() {
        HBox SuggestionsContainer = new HBox(15);
        ScrollPane SuggestionsContainerScrollPane = new ScrollPane(SuggestionsContainer);

        StackPane scrollStack = new StackPane(SuggestionsContainerScrollPane);


        Button p = new Button(null, new ImageView(new Image("/img/privious.png")));
//        p.setStyle("-fx-background-color: transparent");
        p.getStyleClass().add("privious");

        p.setOnAction(event -> {
            SuggestionsContainerScrollPane.setHvalue(SuggestionsContainerScrollPane.getHvalue() - 0.05);
        });
        Button s = new Button(null, new ImageView(new Image("/img/suivant.png")));
//        s.setStyle("-fx-background-color: transparent");
        s.getStyleClass().add("suiviant");


        s.setOnAction(event -> {
            SuggestionsContainerScrollPane.setHvalue(SuggestionsContainerScrollPane.getHvalue() + 0.05);


        });

        scrollStack.getChildren().addAll(p, s);
        StackPane.setAlignment(s, Pos.CENTER_RIGHT);
        StackPane.setAlignment(p, Pos.CENTER_LEFT);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    SuggestionsContainer.getScene().setOnScroll( event1 -> {

                        SuggestionsContainerScrollPane.setHvalue(SuggestionsContainerScrollPane.getHvalue() -event1.getDeltaY()/ SuggestionsContainerScrollPane.getContent().getBoundsInLocal().getWidth());

                    });
                }catch (NullPointerException e) {

                    ChangeListener changeListener= new ChangeListener() {
                        @Override
                        public void changed(ObservableValue observable2, Object oldValue, Object newValue) {
                            SuggestionsContainer.getScene().setOnScroll( event1 -> {

                                SuggestionsContainerScrollPane.setHvalue(SuggestionsContainerScrollPane.getHvalue() -event1.getDeltaY()/ SuggestionsContainerScrollPane.getContent().getBoundsInLocal().getWidth());

                            });
                            SuggestionsContainer.sceneProperty().removeListener(this);
                        }
                    };
                    SuggestionsContainer.sceneProperty().addListener(changeListener);


                    System.out.println("not ready");
                }
            }
        });
        try {
            SuggestionsContainer.getChildren().clear();
//            int max = 8;
            for (String sugText :
                    Suggestions.getAll()) {
                if (deletedSuggestions.contains(sugText))
                    continue;


                Button btnClose = new Button();
                btnClose.getStyleClass().add("close_enCours_");
                btnClose.setVisible(false);





                Label lblContent = new Label(sugText);
                lblContent.getStyleClass().add("sugText");


                HBox sug = new HBox(lblContent, btnClose);

                sug.setOnMouseEntered(event -> {
                    btnClose.setVisible(true);

                });
                sug.setOnMouseExited(event -> {
                    btnClose.setVisible(false);

                });

                sug.getStyleClass().add("suggestion");

                btnClose.setOnAction( event -> {
                    deletedSuggestions.add(sugText);

                    if (SuggestionsContainer.getChildren().size() == 1) {
                        scrollStack.getChildren().removeAll(p, s);
                        Label noSuggestionsLabel = new Label("Il n'y a pas de nouvelles suggestions");
                        noSuggestionsLabel.setStyle(" -fx-text-fill: white; -fx-alignment: center-left; -fx-text-alignment: left");
                        scrollStack.getChildren().add(noSuggestionsLabel);
                    }
                    SuggestionsContainer.getChildren().remove(sug);



                });
                SuggestionsContainer.getChildren().add(0, sug);
//                if (max--== 0)
//                    break;
            }
            if (SuggestionsContainer.getChildren().size() == 0) {
                scrollStack.getChildren().removeAll(p, s);
                Label noSuggestionsLabel = new Label("il n ya pas des nouveaux suggestions");
                noSuggestionsLabel.setStyle(" -fx-text-fill: white; -fx-alignment: center-left; -fx-text-alignment: left");
                scrollStack.getChildren().add(noSuggestionsLabel);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return scrollStack;
    }

    private static LineChart<String, Number> getLineChart(List<Plat> liste) throws SQLException {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();

        LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);

        lineChart.setLegendVisible(false);

        XYChart.Series<String, Number> nVend = new XYChart.Series<>();

        nVend.getData().add(new XYChart.Data<>(LocalDate.now().plusDays(-6).getDayOfWeek().name(), Statistique.recetteAday(LocalDateTime.now().plusDays(-6), liste)));
        nVend.getData().add(new XYChart.Data<>(LocalDate.now().plusDays(-5).getDayOfWeek().name(), Statistique.recetteAday(LocalDateTime.now().plusDays(-5), liste)));
        nVend.getData().add(new XYChart.Data<>(LocalDate.now().plusDays(-4).getDayOfWeek().name(), Statistique.recetteAday(LocalDateTime.now().plusDays(-4), liste)));
        nVend.getData().add(new XYChart.Data<>(LocalDate.now().plusDays(-3).getDayOfWeek().name(), Statistique.recetteAday(LocalDateTime.now().plusDays(-3), liste)));
        nVend.getData().add(new XYChart.Data<>(LocalDate.now().plusDays(-2).getDayOfWeek().name(), Statistique.recetteAday(LocalDateTime.now().plusDays(-2), liste)));
        nVend.getData().add(new XYChart.Data<>(LocalDate.now().plusDays(-1).getDayOfWeek().name(), Statistique.recetteAday(LocalDateTime.now().plusDays(-1), liste)));

//        final double[] recette = {Statistique.recetteToday()};
        XYChart.Data<String, Number> todayReccete = new XYChart.Data<>(LocalDate.now().getDayOfWeek().name(), Statistique.recetteToday(liste));
//        XYChart.Data<String, Number> todayReccete = new XYChart.Data<>(LocalDate.now().getDayOfWeek().name(), 15);
        nVend.getData().add(todayReccete);


        lineChart.getData().add(nVend);
        lineChart.setTitle(French.ADMIN_RECETTE);


//        tl.getKeyFrames().add(new KeyFrame(Duration.millis(5000),
//                (ActionEvent actionEvent) -> {
//                    try {
//                        liste.clear() ;
//                        liste.addAll( PlatManager.loadTableToList());
//
//
//
//                        if (todayReccete.getYValue().doubleValue() != Statistique.recetteToday(liste)) {
//
//                            nVend.getData().get(nVend.getData().size() -1).setYValue(Statistique.recetteToday(liste));
//
////                            recette[0] = (double) todayReccete.getYValue();
//                        }
//                    } catch (SQLException e) {
//                        e.printStackTrace();
//                    }
//
//
//                }
//        ));
        tl.setCycleCount(Animation.INDEFINITE);
        tl.setAutoReverse(false);
        tl.play();





        return lineChart;


    }
}
