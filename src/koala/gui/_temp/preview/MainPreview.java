package koala.gui._temp.preview;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

/**
 * Created by bjama on 4/3/2017.
 */
public class MainPreview {

    public void lunchPreview(Stage primaryStage){
        Stage actionBarStage = new Stage();



        Scene sc = new Scene(new ActionBarUIGenerator().createActionbar(primaryStage), 250, 65);
        sc.setFill(Paint.valueOf("transparent"));
        actionBarStage.setScene(sc);
        actionBarStage.initStyle(StageStyle.TRANSPARENT);
        actionBarStage.setX(primaryStage.getX()+primaryStage.getWidth() - 275);
        actionBarStage.setY(primaryStage.getY()+primaryStage.getHeight() + 3);
        actionBarStage.show();

        primaryStage.xProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {

                actionBarStage.setX(primaryStage.getX()+primaryStage.getWidth() - 275);
                actionBarStage.setY(primaryStage.getY()+primaryStage.getHeight() + 3);
            }
        });
        primaryStage.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, event -> {
            actionBarStage.close();
            System.out.println("all closed");
        });

    }

}
