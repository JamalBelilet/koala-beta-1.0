package koala.gui.admin.controller;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import koala.db.Backup;
import koala.db.Restore;
import strings.French;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by bjama on 4/22/2017.
 */
public class BackupUIGenerator {

    public static void initBackupUI(StackPane rootContainer, FlowPane centerPane) {

        centerPane.getChildren().clear();


        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open database script");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("SQL", "*.sql")
        );
//        File file = fileChooser.showOpenDialog(centerPane.getScene().getWindow());


        final File[] file = {null};
        Label openDatabase = new Label(French.ADMIN_DONNEES_IMPORTER_TEXTE);
        openDatabase.getStyleClass().add("load");
        Button openDatabaseAsFile = new Button(French.ADMIN_DONNEES_IMPORTER);
        openDatabaseAsFile.getStyleClass().add("load");
        TextField openDatabaseAsFileTextField = new TextField();
        openDatabaseAsFileTextField.getStyleClass().addAll("load");
        openDatabaseAsFileTextField.setPrefColumnCount(45);
        openDatabaseAsFileTextField.setPromptText(French.ADMIN_DONNEES_IMPORTER_PROMPT);

        HBox openDatabaseAsFileHBox = new HBox(15, openDatabaseAsFileTextField, openDatabaseAsFile);
        VBox openDatabaseAsFileVBox = new VBox(5, openDatabase, openDatabaseAsFileHBox);



        openDatabaseAsFile.setOnAction(event -> {
            if (openDatabaseAsFileTextField.getText().equals("")){
                try {
                    file[0] = fileChooser.showOpenDialog(centerPane.getScene().getWindow());
                    new Restore(file[0].getPath()).start(openDatabaseAsFileHBox, rootContainer);
                    openDatabaseAsFileTextField.setText(file[0].getPath());
                } catch (NullPointerException ex) {
                }
            } else {
                new Restore(openDatabaseAsFileTextField.getText()).start(openDatabaseAsFileHBox, rootContainer);
            }
        });
        openDatabaseAsFileTextField.setOnMouseClicked(event -> {
            try {
                file[0] = fileChooser.showOpenDialog(centerPane.getScene().getWindow());
                openDatabaseAsFileTextField.setText(file[0].getPath());
            } catch (NullPointerException ex) {
                System.err.println("error");
            }
        });


        Label saveDatabase = new Label(French.ADMIN_DONNEES_EXPORTER_TEXTE);
        saveDatabase.getStyleClass().add("save");

        Button saveDatabaseAsFile = new Button(French.ADMIN_DONNEES_EXPORTER);
        saveDatabaseAsFile.getStyleClass().add("save");
        TextField saveDatabaseAsFileTextField = new TextField();
        saveDatabaseAsFileTextField.getStyleClass().add("save");
        saveDatabaseAsFileTextField.setPrefColumnCount(45);
        saveDatabaseAsFileTextField.setPromptText(French.ADMIN_DONNEES_EXPORTER_PROMPT);

        HBox saveDatabaseAsFileHBox = new HBox(15, saveDatabaseAsFileTextField, saveDatabaseAsFile );
        VBox saveDatabaseAsFileVBox = new VBox(5, saveDatabase, saveDatabaseAsFileHBox);



        saveDatabaseAsFile.setOnAction(event -> {
            if (saveDatabaseAsFileTextField.getText().equals("")){
                try {
                    file[0] = fileChooser.showSaveDialog(centerPane.getScene().getWindow());
                    new Backup(file[0].getPath()).start(saveDatabaseAsFileHBox, rootContainer);
                    saveDatabaseAsFileTextField.setText(file[0].getPath());
                } catch (NullPointerException ex) {
                }
            } else {
                new Backup(saveDatabaseAsFileTextField.getText()).start(saveDatabaseAsFileHBox, rootContainer);
            }
        });
        saveDatabaseAsFileTextField.setOnMouseClicked(event -> {
            try {
                file[0] = fileChooser.showSaveDialog(centerPane.getScene().getWindow());
                saveDatabaseAsFileTextField.setText(file[0].getPath());
            } catch (NullPointerException ex) {
            }

        });

        Label titleLabel = new Label(French.ADMIN_DONNEES_TITRE);
        titleLabel.getStyleClass().addAll("title");

        Label titleText = new Label(French.ADMIN_DONNEES_BACKUP_DESCRIPTION);

        VBox headerVBox = new VBox(
                15,
                titleLabel,
                titleText

        );
        headerVBox.getStyleClass().add("backup");

        VBox vBox = new VBox(50,headerVBox, openDatabaseAsFileVBox, saveDatabaseAsFileVBox);

        centerPane.getChildren().add(vBox);
        FlowPane.setMargin(vBox, new Insets(50 , 0, 0, 0));



    }

    public static void initBackupNotification(StackPane rootContainer, String message) {
        Rectangle rect = new Rectangle(rootContainer.getScene().getWidth(), rootContainer.getScene().getHeight(), Color.rgb(0,0,0,.5));
        rootContainer.getChildren().add(rect);
        Label messageLabel = new Label(message);
        messageLabel.getStyleClass().add("backup-message");
        rootContainer.getChildren().add(messageLabel);
        StackPane.setAlignment(messageLabel, Pos.CENTER);

        rect.setOnMouseClicked(event -> {
            rootContainer.getChildren().removeAll(rect, messageLabel);
        });
        messageLabel.setOnMouseClicked(event -> {
            rootContainer.getChildren().removeAll(rect, messageLabel);
        });

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {

                        rootContainer.getChildren().removeAll(rect, messageLabel);


                    }
                });
            }
        }, 7000 );

    }

}
