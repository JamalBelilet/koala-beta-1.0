package koala.gui.caissier.controller;

import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import koala.gestionPlats.categorie.CategorieManager;
import koala.gestionPlats.plat.Plat;
import koala.gestionPlats.plat.PlatManager;
import koala.gui.caissier.controller.animation.Animator;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PlatUIGenerateur {

    public PlatUIGenerateur() throws SQLException {
    }

    public static void appendPlats(TilePane tilePane, VBox order, String categorieNom) throws SQLException {
        tilePane.getStyleClass().add("plats");
        List<Plat> plats = PlatManager.platsDisponiblesByCategorie(CategorieManager.getCategorie(categorieNom).getIdCategorie());
        for (Plat plat :
                plats) {


            uniPlatUI(tilePane, plat);

        }

        OrderUIGenerateur.appendOrder(tilePane , order);


        List<Plat> platsIndesponibles = PlatManager.platsIndisponiblesByCategorie(CategorieManager.getCategorie(categorieNom).getIdCategorie());
        for (Plat plat :
                platsIndesponibles) {


            Image image = null;
            if (plat.getImage() != null)
                image = new Image(plat.getImage().getBinaryStream()) ;

            ImageView imageView = new ImageView(image);
//            imageView.setPreserveRatio(true);
            imageView.setFitHeight(130);
            imageView.setFitWidth(200);
            imageView.setStyle("-fx-max-width: 50; -fx-max-height: 50");
            imageView.setClip(new Rectangle(200, 130));


            Animator animator = new Animator();
            animator.animate(imageView);


            StackPane stackPane = new StackPane();
            stackPane.getChildren().add(imageView);


            stackPane.getChildren().add(new Label(plat.getNom()));
            stackPane.getChildren().add(new Rectangle(200, 130, Color.valueOf("rgba(255, 255, 255, .6)")));


            stackPane.getStyleClass().add("onePlat");
            tilePane.getChildren().add( stackPane );

            stackPane.setOnMouseEntered(event -> {
                animator.parallelTransition.play();
            });
            stackPane.setOnMouseExited(mouseEvent -> {
                animator.parallelTransition_reverse.play();
            });

        }

    }

    private static void uniPlatUI(TilePane tilePane, Plat plat) throws SQLException {
        Image image = null;
        if (plat.getImage() != null)
            image = new Image(plat.getImage().getBinaryStream()) ;

        ImageView imageView = new ImageView(image);
//            imageView.setPreserveRatio(true);
        imageView.setFitHeight(130);
        imageView.setFitWidth(200);
        imageView.setStyle("-fx-max-width: 50; -fx-max-height: 50");
        imageView.setClip(new Rectangle(200, 130));


        Animator animator = new Animator();
        animator.animate(imageView);


        StackPane stackPane = new StackPane();
        stackPane.getChildren().add(imageView);


        stackPane.getChildren().add(new Label(plat.getNom()));


        stackPane.getStyleClass().add("onePlat");
        tilePane.getChildren().add( stackPane );

        stackPane.setOnMouseEntered(event -> {
            animator.parallelTransition.play();
        });
        stackPane.setOnMouseExited(mouseEvent -> {
            animator.parallelTransition_reverse.play();
        });
    }

    public List<StackPane> appendPlatsFromSearchByString(TilePane tilePane, VBox order, String platNom) throws SQLException {
        List<StackPane> platsUI = new ArrayList<>();
        tilePane.getStyleClass().add("plats");

        for (Plat p :
                PlatManager.platsByString(platNom)) {
//            StackPane stackPane = new StackPane(new Label(p.getNom()));
//            stackPane.getStyleClass().add("onePlat");
//            tilePane.getChildren().add( stackPane );
//            platsUI.add(stackPane);
            uniPlatUI(tilePane, p);

        }

        OrderUIGenerateur.appendOrder(tilePane , order);

        return platsUI;

    }
    public static void appendPlatsFromAdvanced(Pane centerContainer, TilePane tilePane, VBox order, GridPane advancedSearchGridPane, ComboBox comboBox, List<Pane> pagingList, String finalCategorie_nom) throws SQLException {


        tilePane.getStyleClass().add("plats");
        int id_Categorie = -1;
        if (finalCategorie_nom != null)
            id_Categorie = CategorieManager.getCategorie(finalCategorie_nom).getIdCategorie();
        List<Plat> plats = PlatManager.platsMulticriteresWithString(
                getNum(((TextField) advancedSearchGridPane.getChildren().get(1)).getText()),
                getNum(((TextField) advancedSearchGridPane.getChildren().get(3)).getText()),
                getNum(((TextField) advancedSearchGridPane.getChildren().get(7)).getText()),
                getNum(((TextField) advancedSearchGridPane.getChildren().get(9)).getText()),
                getNum(((TextField) advancedSearchGridPane.getChildren().get(11)).getText()),
                getNum(((TextField) advancedSearchGridPane.getChildren().get(13)).getText()),
                getNum(((TextField) advancedSearchGridPane.getChildren().get(19)).getText()),
                getNum(((TextField) advancedSearchGridPane.getChildren().get(21)).getText()),
                getNum(((TextField) advancedSearchGridPane.getChildren().get(15)).getText()),
                getNum(((TextField) advancedSearchGridPane.getChildren().get(17)).getText()),
                0,
                getNum(((TextField) advancedSearchGridPane.getChildren().get(5)).getText(), true),
                id_Categorie,
                ((CheckBox)  advancedSearchGridPane.getChildren().get(22)).isSelected(),
                PlatManager.stringWithoutDigitsAndLeadingOrTrailingWhitespace(comboBox.getEditor().getText())
        );


        for (Plat plat :
                plats) {
            uniPlatUI(tilePane, plat);

        }
        OrderUIGenerateur.appendOrder(tilePane , order);

        centerContainer.getChildren().remove(pagingList.get(pagingList.size() - 1));

    }


    private static double getNum( String texto){

        try {
            double d = Double.parseDouble(texto);
            return d;

        } catch (Exception e) {
            return Double.MAX_VALUE;
        }

    }
    private static int getNum( String texto, boolean itIsInt){

        try {
            int i = Integer.parseInt(texto);
            return i;

        } catch (Exception e) {
            return Integer.MAX_VALUE;
        }

    }
}
