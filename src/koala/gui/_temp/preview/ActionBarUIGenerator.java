package koala.gui._temp.preview;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Created by bjama on 4/3/2017.
 */
public class ActionBarUIGenerator {
    private static HBox actionbar = new HBox(
            new Button("cai") ,
            new Button("cui"),
            new Button("mag"),
            new Button("adm")
    );

    public HBox createActionbar(Stage primaryStage) {

        actionbar.getStyleClass().add("preview-action-bar");
        actionbar.getStylesheets().add("/css/actionBarPreview.css");
        ((Button) actionbar.getChildren().get(0)).setOnAction(event -> {

            try {
                primaryStage.getScene().setRoot(FXMLLoader.load(getClass().getResource("/fxml/caissier.fxml")));
            } catch (IOException e) {
                e.printStackTrace();
            }

        });

        ((Button) actionbar.getChildren().get(1)).setOnAction(event -> {

            try {
                primaryStage.getScene().setRoot(FXMLLoader.load(getClass().getResource("/fxml/cuisinier.fxml")));
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
        ((Button) actionbar.getChildren().get(2)).setOnAction(event -> {

            try {
                primaryStage.getScene().setRoot(FXMLLoader.load(getClass().getResource("/fxml/magasinier.fxml")));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        ((Button) actionbar.getChildren().get(3)).setOnAction(event -> {


            try {
                primaryStage.getScene().setRoot(FXMLLoader.load(getClass().getResource("/fxml/admin.fxml")));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return actionbar;

    }
}
