package koala.gui.caissier.controller;

import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import koala.gestionPlats.categorie.Categorie;
import koala.gestionPlats.categorie.CategorieManager;
import koala.gui.caissier.controller.animation.Animator;

import java.sql.SQLException;
import java.util.List;

public class CategorieUIGenerateur {

    public CategorieUIGenerateur() throws SQLException {
    }

    public static void initCategories(Pane centerContainer, TilePane categories, VBox order,  Pane searchKey, List<Pane> pagingList) throws SQLException {
        pagingList.add(categories);
        for (Categorie categorie :
                CategorieManager.loadTableToList()) {
            Image image = null;
            if (categorie.getImage() != null)
                image = new Image(categorie.getImage().getBinaryStream()) ;

            ImageView imageView = new ImageView(image);
//            imageView.setPreserveRatio(true);
            imageView.setFitHeight(187);
            imageView.setFitWidth(187);
            imageView.setStyle("-fx-max-width: 50; -fx-max-height: 50");
            imageView.setClip(new Rectangle(187, 187));


            Animator animator = new Animator();
            animator.animate(imageView);


            StackPane stackPane = new StackPane();
            stackPane.getChildren().add(imageView);
            Label label = new Label(categorie.getNom());
            stackPane.getChildren().add(label);
            stackPane.getStyleClass().add("oneCategory");

            stackPane.setOnMouseClicked( event ->  {

                TilePane platsTilePane = new TilePane();

                pagingList.get(pagingList.size() - 1).setVisible(false);
                pagingList.get(pagingList.size() - 1).setManaged(false);
                pagingList.add(platsTilePane);


                try {
                    PlatUIGenerateur.appendPlats(platsTilePane, order, ((Label) ((StackPane) stackPane).getChildren().get(((StackPane) stackPane).getChildren().size() -1)).getText());
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                centerContainer.getChildren().add(platsTilePane);

//              adding searching keys
                HBox aKey= new HBox();;
                ImageView keyClose = new ImageView(new Image("/img/liteClose.png"));
                keyClose.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        searchKey.getChildren().remove(aKey);
                        centerContainer.getChildren().remove(platsTilePane);
                        pagingList.remove(pagingList.size() - 1);
                        pagingList.get(pagingList.size() -1).setManaged(true);
                        pagingList.get(pagingList.size() -1).setVisible(true);
                    }
                });
                Label keyName = new Label(
                        ((Label) ((StackPane) event.getSource()).getChildren().get(1)).getText()
                );
                aKey.getChildren().addAll(keyName, keyClose);
                searchKey.getChildren().add(aKey);

            });




            stackPane.setOnMouseEntered(event -> {
                animator.parallelTransition.play();
            });
            stackPane.setOnMouseExited(mouseEvent -> {
                animator.parallelTransition_reverse.play();
            });
            categories.getChildren().add( stackPane );

        }
    }
}
