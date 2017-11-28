package koala.gui.login.controller;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import koala.administration.Compte;
import koala.administration.Login;
import strings.French;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;

public class Controller {

    @FXML private Button signIn;

    @FXML private Button passwdOption;
    @FXML private Button pinOption;

    @FXML private ImageView grayCircle;
    @FXML private ImageView blackCircle;

    @FXML private PasswordField passwd;
    @FXML private TextField login;

    @FXML private VBox welcomeContainer;


    @FXML private Label loginLabel;
    @FXML private Label passwdLabel;



    @FXML private HBox signInContainer;
    @FXML private HBox loginOptions;

    @FXML private VBox passwdContainer;
    @FXML private VBox loginContainer;
    @FXML private VBox loginSuperContainer;

    @FXML
    public void initialize() {


        welcomeContainer.sceneProperty().addListener(observable -> {
            if (welcomeContainer != null && welcomeContainer.getScene() != null)
                welcomeContainer.getScene().getWindow().sizeToScene();

        });


        passwd.setPromptText(French.LOGIN_PASSWORD);
        login.setPromptText(French.LOGIN_USER);
        signIn.setText(French.LOGIN_LOGIN);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {


                passwdOption.getStyleClass().add("active");

                signIn.getScene().getWindow().setWidth(576 );
                signIn.getScene().getWindow().centerOnScreen();

            }
        });


        pinOption.setOnAction(event -> {

            pinOption.getStyleClass().remove("active");
            pinOption.getStyleClass().add("active");

            passwdOption.getStyleClass().remove("active");

//            passwdLabel.setText("pin:");

            passwd.setPromptText(French.LOGIN_PIN);
            System.out.println(loginOptions.lookup(".active").lookup(".pin"));
        });

        passwdOption.setOnAction(event -> {
            passwdOption.getStyleClass().remove("active");
            passwdOption.getStyleClass().add("active");

            pinOption.getStyleClass().remove("active");
//            passwdLabel.setText("passwd:");
            passwd.setPromptText(French.LOGIN_PASSWORD);


        });


        KeyValue keyValue_0 = new KeyValue(blackCircle.rotateProperty(), 720);
        KeyValue keyValue_1 = new KeyValue(grayCircle.rotateProperty(), -360);
        KeyFrame keyFrame_0 = new KeyFrame(Duration.millis(2500), keyValue_0);
        KeyFrame keyFrame_1 = new KeyFrame(Duration.millis(5000), keyValue_1);

        Timeline timeline_0 = new Timeline(keyFrame_0, keyFrame_1);




        Timer t = new Timer();

//        signIn.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
//            if (event.getCode() == KeyCode.ALT)
//                System.err.println("hello from event handler !");
//        });

//        signIn.setOnKeyPressed( event -> {
//            if (event.getCode() == KeyCode.ALT)
//                System.err.println("hello from event handler !");
//            signIn.setOnKeyPressed(null);
//        });



        signIn.setOnAction(event -> {

            if (signInNow(timeline_0, t, loginOptions))
                signInNow(timeline_0, t, loginOptions);
        });
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                signIn.getScene().setOnKeyPressed(event -> {
                    if (event.getCode().equals(KeyCode.ENTER)) {
                        if (signInNow(timeline_0, t, loginOptions))
                            signIn.getScene().setOnKeyPressed(null);

                    }


                });
            }
        });

    }

    private boolean signInNow(final Timeline timeline_0, Timer t, HBox loginOptions) {
        Parent root = null;

        try {
            String[] loginNPasswd = Login.getConnection(login.getText(), passwd.getText());






            if ( Integer.parseInt(loginNPasswd[0]) != -1) {
                loginSuperContainer.getStyleClass().add("change-back");

                Compte.setIdCompte(Integer.parseInt(loginNPasswd[0]));

                switch (loginNPasswd[1]) {

                    case "magasinier":
                        root = FXMLLoader.load(getClass().getResource("/fxml/magasinier.fxml"));

                        break;
                    case "admin":
                        root = FXMLLoader.load(getClass().getResource("/fxml/admin.fxml"));
                        break;
                    case "cuisiner":
                        root = FXMLLoader.load(getClass().getResource("/fxml/cuisinier.fxml"));
                        break;
                    case "caissier":
                        root = FXMLLoader.load(getClass().getResource("/fxml/caissier.fxml"));
                        break;

                }

                loginOptions.setVisible(false);

                TranslateTransition tt_0 = new TranslateTransition(Duration.millis(300), signInContainer);
                tt_0.setFromX(0);
                tt_0.setToX(1280);
                tt_0.setCycleCount(1);


                TranslateTransition tt_1 = new TranslateTransition(Duration.millis(300), passwdContainer);
                tt_1.setFromX(0);
                tt_1.setToX(-1280);
                tt_1.setCycleCount(1);

                TranslateTransition tt_2 = new TranslateTransition(Duration.millis(300), loginContainer);
                tt_2.setFromX(0);
                tt_2.setToX(-1280);
                tt_2.setCycleCount(1);

                ParallelTransition parallelTransition_0 = new ParallelTransition(tt_0, tt_1, tt_2);
                parallelTransition_0.setCycleCount(1);
                parallelTransition_0.setDelay(Duration.millis(300));

                parallelTransition_0.play();


                Timer ccTimer = new Timer();
                ccTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                welcomeContainer.setVisible(true);
                                welcomeContainer.setManaged(true);
                                timeline_0.play();

                                ccTimer.purge();
                                ccTimer.cancel();
                            }
                        });

                    }
                }, 350);

                Timer cTimer = new Timer();
                Parent finalRoot = root;
                cTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                welcomeContainer.setVisible(false);
                                welcomeContainer.setManaged(false);

                                loginSuperContainer.getStyleClass().add("change-back-sec");
                                signIn.getScene().getWindow().setWidth(1280);
                                signIn.getScene().getWindow().centerOnScreen();
                                signIn.getScene().setRoot(finalRoot);

                                cTimer.purge();
                                ccTimer.cancel();

                            }
                        });

                    }
                }, 3000);
                return true;

            } else {
//                    loginLabel.getStyleClass().add("error");
                passwdLabel.getStyleClass().add("error");
                passwd.getStyleClass().add("error");
                login.getStyleClass().add("error");
//                    login.setStyle(" -fx-background-color: brown; -fx-padding: 15 25; -fx-font-size: 18; -fx-text-fill: #F2F2F2");
                t.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {


                                passwd.getStyleClass().remove("error");
                                login.getStyleClass().remove("error");

                            }
                        });
                    }
                }, 2000 );
                return false;


            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;

        } catch (IOException e) {
            e.printStackTrace();
            return false;

        }

    }
}
