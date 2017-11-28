package koala.gui.login.controller;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import koala.gestionEmployes.Employe;
import strings.French;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Created by bjama on 4/19/2017.
 */
public class CompteUIGenerator {



    public static void soutSignIn(Pane compteInfo, Employe employe, String type) {
        try {

            ImageView compteImage =new ImageView();
            compteImage.setStyle("-fx-effect: dropshadow(gaussian , rgba(0,0,0,0.7), 4, 0, 0, 0);");

            compteImage.setImage(new Image(employe.getImage().getBinaryStream()));
            compteImage.setFitHeight(115);
            compteImage.maxWidth(115);
            compteImage.setPreserveRatio(true);

            Button btn = new Button(French.HEADER_LOGOUT);
            btn.getStyleClass().add("sign-out");


            Alert dialogC = new Alert(Alert.AlertType.CONFIRMATION);
            dialogC.setTitle(French.HEADER_LOGOUT);
            dialogC.setHeaderText(null);
            dialogC.setContentText(" ");
            dialogC.setGraphic(null);

            System.out.println( dialogC.getDialogPane().getChildren().get(1)+ "-------------------------");
            Label dialogCTitle = new Label(French.HEADER_LOGOUT);
            dialogCTitle.setStyle("-fx-text-fill: white; -fx-font-size: 24");
            Text titleC = new Text(French.HEADER_LOGOUT + "\n\n");
            titleC.setFill(Color.valueOf("white"));
            titleC.setStyle("-fx-font-size: 24; -fx-padding: 25 0 0 10");

            Text contentC = new Text(French.HEADER_LOGOUT_QUESTION);
            contentC.setFill(Color.valueOf("white"));
            contentC.setStyle("-fx-font-size: 18");

            ((Label) dialogC.getDialogPane().getChildren().get(1)).setGraphic(new TextFlow(titleC, contentC));
            dialogC.getDialogPane().getChildren().add(0, dialogCTitle);
            dialogC.getDialogPane().getChildren().get(0).setStyle("-fx-background-color: transparent;");
            dialogC.getDialogPane().getChildren().get(1).setStyle("-fx-background-color: transparent;");
            dialogC.getDialogPane().getChildren().get(2).setStyle("-fx-background-color: transparent;");
            dialogC.getDialogPane().setStyle("-fx-background-color: #ffa000e0; -fx-border-width: 1 1 2 2; -fx-border-color: #FFFFFF; -fx-min-height: 200; -fx-min-width: 450;" +
                    "-fx-effect: dropshadow(gaussian, black, 10, .9, 0, 0)");

            dialogC.getDialogPane().getScene().setFill(Color.TRANSPARENT);
            ((Stage) dialogC.getDialogPane().getScene().getWindow()).initStyle(StageStyle.TRANSPARENT);
            ((Stage) dialogC.getDialogPane().getScene().getWindow())
                    .getIcons().add(new Image("/img/logo.png"));


            dialogC.getDialogPane().getStylesheets().add(
                    CompteUIGenerator.class.getResource("/css/admin.css").toExternalForm());
//            dialogC.getDialogPane().getStyleClass().add("dialog-pane");

            dialogC.getDialogPane().getScene().getWindow();

            dialogC.getDialogPane().getButtonTypes().clear();
            dialogC.getDialogPane().getButtonTypes().add(
                    new ButtonType(French.HEADER_LOGOUT_OUI, ButtonBar.ButtonData.OK_DONE));
            dialogC.getDialogPane().getButtonTypes().add(
                    new ButtonType(French.HEADER_LOGOUT_ANNULER, ButtonBar.ButtonData.CANCEL_CLOSE));

            btn.setOnAction(event -> {

                Optional<ButtonType> answer = dialogC.showAndWait();
                try {
                    System.out.println(answer.get());
                    if (answer.get().getButtonData()==ButtonBar.ButtonData.OK_DONE) {
                        System.out.println(answer.get());

                        compteInfo.getScene().setRoot(FXMLLoader.load(new CompteUIGenerator().getClass().getResource("/fxml/login.fxml")));

                        switch (type) {
                            case "magasinier": {
                                koala.gui.magasinier.controller.Controller.killAll();

                            }
                            case "cuisiner": {
                                koala.gui.cuisinier.controller.Controller.killAll();

                            }
                        }

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            Label nom = new Label(employe.getNom());
            Label prenom = new Label(employe.getPrenom());
            VBox textInfo = new VBox(
                    nom,
                    prenom,
                    btn
            );
            ;
            VBox.setMargin(btn, new Insets(15, 0, 0, 0));
            VBox.setMargin(nom, new Insets(7, 0, 0, 0));
            textInfo.getStyleClass().addAll("profile-labels");


            compteInfo.getChildren().addAll(compteImage, textInfo);


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
