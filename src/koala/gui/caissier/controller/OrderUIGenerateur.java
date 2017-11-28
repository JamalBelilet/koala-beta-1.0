package koala.gui.caissier.controller;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import koala.gestionCommandes.commandeUnitaire.CommandeUnitaire;
import koala.gestionCommandes.commandeUnitaire.CommandeUnitaireManager;
import koala.gestionPlats.plat.PlatManager;

import java.sql.SQLException;
import java.util.ArrayList;

public class OrderUIGenerateur {
    public static ArrayList<CommandeUnitaire> commandeUnitaires = new ArrayList<>();

    public static void appendOrder(final TilePane tilePane, final VBox order) throws SQLException {

        for (Node _oneCategory: tilePane.getChildren()) {
            StackPane oneCategory = (StackPane) _oneCategory;


            oneCategory.setOnMouseClicked(event -> {

//              for the selected plats we inc count
                for (final Node h_Box : order.getChildren()) {
                    if (((Label) oneCategory.getChildren().get(oneCategory.getChildren().size() -1))
                            .getText().equals(((Label) ((HBox) h_Box).getChildren().get(2)).getText())) {

                        ((TextField) ((HBox) h_Box).getChildren().get(0))
                                .setText(Integer.toString(Integer.parseInt(  ((TextField) ((HBox) h_Box).getChildren().get(0)  ).getText()) + 1));


                        for (CommandeUnitaire com :
                                commandeUnitaires) {
                            if (com.getPlat().getNom().equals( ((Label) ((HBox) h_Box).getChildren().get(2)).getText()   )) {
                                com.setQuantite(Integer.parseInt(  ((TextField) ((HBox) h_Box).getChildren().get(0)  ).getText()));
                            }
                        }
                        //TODO handling integer parsing error if the textfield is empty
                        return;

                    }
                }

//              for the non selected plats we add new line in ordering list
                order.getParent().setVisible(true);
                order.getParent().setManaged(true);
                TextField quantityField = new TextField(Integer.toString(1)) {
                    @Override public void replaceText(int start, int end, String text) {
                        // If the replaced text would end up being invalid, then simply
                        // ignore this call!
                        System.out.println(text.compareTo(KeyCode.BACK_SPACE.toString()));
                        if (text.matches("[1-9]")) {
                            super.replaceText(start, end, text);
                        }
                    }

                    @Override public void replaceSelection(String text) {
                        if (text.matches("[1-9]")) {
                            super.replaceSelection(text);
                        }
                    }
                };
                quantityField.setPrefColumnCount(2);
                quantityField.getStyleClass().add("quantity");




                Label platNameLabel = new Label( ((Label) oneCategory.getChildren().get(oneCategory.getChildren().size()-1)).getText() );





                platNameLabel.getStyleClass().add("plat-name");
                Label platPriceLabel = null;
                try {
                    platPriceLabel = new Label(Double.toString(PlatManager.getPlat(platNameLabel.getText()).getPrix()) + " DA");
                } catch (SQLException e) {
                    e.printStackTrace();
                }


//                HBox hBox = new HBox(quantityField, new Label("*"), platNameLabel, platPriceLabel);
                HBox hBox = new HBox(quantityField, new Label(null), platNameLabel, platPriceLabel);


                quantityField.textProperty().addListener(observable -> {
                    System.out.println(quantityField.getText());
                    for (CommandeUnitaire com :
                            commandeUnitaires) {
                        if ( com.getPlat().getNom().equals( platNameLabel.getText() ) ) {
                            com.setQuantite(Integer.parseInt(  quantityField.getText()));
                        }
                    }
                });


                try {
                    commandeUnitaires.add(CommandeUnitaireManager.commander(platNameLabel.getText(), 1));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                hBox.getStyleClass().add("subOrder");
                order.getChildren().add(hBox);


//              showing close button when a sub order is hover
                hBox.setOnMouseEntered(event1 -> {
                    ImageView _close = new ImageView(new Image("/img/gr_close.png"));
                    _close.getStyleClass().add("close-order");
                    ((HBox) hBox).getChildren().add(_close);

                    _close.setOnMouseClicked(event2 ->  {
                        if (order.getChildren().size() == 1) {
                            order.getParent().setVisible(false);
                            order.getParent().setManaged(false);
                        }
                        order.getChildren().remove(_close.getParent());

                    });
                    _close.setOnMouseEntered(event2 ->  {
                        _close.setImage(new Image("/img/gr_close-hover.png"));

                    });
                    _close.setOnMouseExited(event2 ->  {
                        _close.setImage(new Image("/img/gr_close.png"));

                    });
                    //TODO del closed commandeunitaire
                });
                hBox.setOnMouseExited(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        ((HBox) hBox).getChildren().remove(((HBox) hBox).getChildren().size() - 1);


                    }
                });



            });
        }


    }
}
