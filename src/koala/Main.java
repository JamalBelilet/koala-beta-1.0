package koala;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import koala.db.ConnectionManager;

import java.io.IOException;

import static java.lang.Thread.sleep;


public class Main extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception{

        primaryStage.setOnCloseRequest(e -> ConnectionManager.getInstance().close());
        VBox pane =new VBox();
        pane.setStyle(
                "-fx-pref-width: 576 ;" +
                "-fx-max-width: 576 ;" +
                "-fx-alignment: center;" +
                "-fx-padding: 250 100 50 100;" +
                "-fx-background-color: #666666;" +
                "-fx-background-image: url('/img/1-Login/5.jpg');" +
                "-fx-spacing: 50;" +
                "-fx-font-size: 24;" +
                "-fx-font-weight: bold;"
        );
        Scene pre_sc = new Scene(pane  , 576 , 719);
//        Scene sc = new Scene(root, 1280, 720);
        primaryStage.setScene(pre_sc);

//        pre_sc.setFill(Color.TRANSPARENT);
//        primaryStage.initStyle(StageStyle.TRANSPARENT);

        primaryStage.setTitle("koala");
        primaryStage.getIcons().add(new Image("/img/logo.png"));

        primaryStage.show();
        primaryStage.setOnCloseRequest(event -> System.exit(0));

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    sleep(400);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        Parent root = null;
                        try {
                            root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        pre_sc.setRoot(root);
                    }
                });
            }
        }).start();
//        new MainPreview().lunchPreview(primaryStage);
    }


    public static void main(String[] args) {
        launch(args);

    }
}
