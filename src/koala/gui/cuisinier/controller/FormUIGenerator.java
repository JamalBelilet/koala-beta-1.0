package koala.gui.cuisinier.controller;

import javafx.beans.InvalidationListener;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import koala.administration.Compte;
import koala.db.ConnectionManager;
import koala.gestionPlats.platProposer.PlatProposer;
import koala.gestionPlats.platProposer.PlatProposerManager;
import koala.getionProbleme.Probleme;
import koala.getionProbleme.ProblemeManager;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Created by bjama on 5/12/2017.
 */
public class FormUIGenerator {

    public static Connection connection = ConnectionManager.getInstance().getConnection();


    @FXML private TextArea contentTextArea;
    @FXML private TextArea contentTextArea_proposer;

    @FXML private TextField plat_nom_textField;

    @FXML private Button envoye_signaler_Btn;
    @FXML private Button envoye_proposer_Btn;

    @FXML private ImageView image;



    private static InvalidationListener listener ;


//    @FXML
//    public void initialize() {
//        envoye_signaler_Btn.setOnAction(event -> {
//            try {
//                ProblemeManager.insert(new Probleme(Compte.getIdCompte(), contentTextArea.getText(), 1, Date.valueOf(LocalDate.now())));
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        });

//    }

    public static void setProposerNdSignalerForms(Button signaler, Button proposer, StackPane enCoursStack, StackPane queueStack, StackPane rootContainier) {

        System.out.println("from proposer signaler pres ---------------------------------");

        proposer.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    setForm("/fxml/proposerForm.fxml", true, this, proposer, enCoursStack, queueStack, rootContainier);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                proposer.setOnAction(null);
            }
        });
        signaler.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    setForm("/fxml/signalerForm.fxml", false, this, signaler, enCoursStack, queueStack, rootContainier);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                signaler.setOnAction(null);
            }
        });
    }

    private static void setForm(String path, boolean isProposerForm, final EventHandler eventHandler, final Button button, StackPane enCoursStack, StackPane queueStack, StackPane rootContainer) throws IOException {

        System.err.println("from proposer signaler pres ---------------------------------");

        ObservableList<javafx.scene.Node> proposerForm;
        Rectangle rect_left = null ;
        Rectangle rect_center = null ;
        Rectangle root_rect = null;

        if (button.getId().equals("proposer") ) {
        } else if (button.getId().equals("signaler")) {
        }



//        proposerForm = ((Group) FXMLLoader.load(Cuisiner.class.getResource(path))).getChildren();
        if (isProposerForm) {

            proposerForm = createProposerForm().getChildren();
        } else {
            proposerForm = createSignalerForm().getChildren();

        }



        if (rootContainer == null) {
            rect_center = new Rectangle(enCoursStack.getWidth(), enCoursStack.getHeight() , Color.valueOf("rgba(0, 0, 0, .7)"));
            rect_left = new Rectangle(queueStack.getWidth(), queueStack.getHeight() , Color.valueOf("rgba(0, 0, 0, .7)"));
            rect_center.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if (rootContainer == null) {
                        enCoursStack.getChildren().remove(enCoursStack.getChildren().size()-1);
                        enCoursStack.getChildren().remove(enCoursStack.getChildren().size()-1);
                        queueStack.getChildren().remove(queueStack.getChildren().size()-1);


                    } else {
                        rootContainer.getChildren().remove(rootContainer.getChildren().size() - 1);
                        rootContainer.getChildren().remove(rootContainer.getChildren().size() - 1);
                    }

                    button.setOnAction(eventHandler);



                }
            });
            rect_left.setOnMouseClicked(rect_center.getOnMouseClicked());
            ((VBox) proposerForm.get(0)).getChildren().get(0).setOnMouseClicked(rect_center.getOnMouseClicked());


        } else {
            root_rect = new Rectangle(rootContainer.getWidth(), rootContainer.getHeight() - 135);
            root_rect.setFill(Color.valueOf("rgba(0, 0, 0, .7)"));
            root_rect.setOnMouseClicked(event -> {
                rootContainer.getChildren().remove(rootContainer.getChildren().size() - 1);
                rootContainer.getChildren().remove(rootContainer.getChildren().size() - 1);

                button.setOnAction(eventHandler);
            });

            ((VBox) proposerForm.get(0)).getChildren().get(0).setOnMouseClicked(root_rect.getOnMouseClicked());


        }







        if (isProposerForm) {
            final javafx.scene.Node imageView = ((HBox) ((VBox) ((VBox) proposerForm.get(0)).getChildren().get(1)).getChildren().get(2)).getChildren().get(1);
            imageView.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setTitle("Open Resource File");

                    fileChooser.getExtensionFilters().addAll(
                            new FileChooser.ExtensionFilter("Images", "*.gif", "*.png", "*.jpg"),
                            new FileChooser.ExtensionFilter("GIF", "*.gif"),
                            new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                            new FileChooser.ExtensionFilter("PNG", "*.png")
                    );
                    File file = fileChooser.showOpenDialog(rootContainer.getScene().getWindow());
                    try {
                        BufferedImage bufferedImage = ImageIO.read(file);
                        Image image = SwingFXUtils.toFXImage(bufferedImage, null);
                        imageView.getStyleClass().clear();

                        ((ImageView)imageView).setImage(image);
                    } catch (IOException ex) {
                    } catch (IllegalArgumentException ex) {

                    }

                }
            });
        }



        if (rootContainer == null) {

            enCoursStack.getChildren().add(rect_center);
            queueStack.getChildren().add(rect_left);
            enCoursStack.getChildren().addAll(proposerForm);


        } else {
            VBox containerV = ((VBox) proposerForm.get(0));
            rootContainer.getChildren().add(root_rect);

            ((VBox) proposerForm.get(0)).setMaxHeight(rootContainer.getScene().getHeight() - 135);
            Rectangle finalRoot_rect = root_rect;
            rootContainer.getScene().heightProperty().addListener(observable -> {
                try {
                    containerV.setMaxHeight(rootContainer.getScene().getHeight() - 135);
                    finalRoot_rect.setHeight(rootContainer.getScene().getHeight() - 135);
                } catch (Exception e) {
                    listener = observable1 -> rootContainer.getScene().heightProperty().removeListener(listener);

                }


            });
            rootContainer.getScene().widthProperty().addListener(observable -> {
                try {
                    finalRoot_rect.setWidth(rootContainer.getScene().getWidth());

                } catch (Exception e) {
                    listener = observable1 -> rootContainer.getScene().heightProperty().removeListener(listener);

                }
            });

            StackPane.setAlignment(((VBox) proposerForm.get(0)), Pos.BOTTOM_RIGHT);
            StackPane.setAlignment(root_rect, Pos.BOTTOM_RIGHT);

            rootContainer.getChildren().addAll(proposerForm);
        }




        // TODO EVENT HANDLER FOR RESIZING THE SCREEN
    }

    private static Group createProposerForm() {
        Button close=new Button();
        close.getStyleClass().addAll("close_enCours_");

        Label proposerPlat=new Label("Proposer un plat");
        proposerPlat.setStyle("-fx-font-size: 22; -fx-text-alignment: center; -fx-padding: 0 0 0 8");
        Separator separator=new Separator();
        separator.setStyle("-fx-pref-width: 250; -fx-orientation: horizontal; -fx-padding: 5 0 25 0");

        Label nom=new Label("nom");
        nom.setStyle("-fx-font-size: 18");

        TextField nomPlat=new TextField();
        nomPlat.setPromptText("ex pizza margeritta");
        nomPlat.getStyleClass().add("text-field-propo");

        VBox nomNomPlat=new VBox();
        nomNomPlat.setSpacing(0);
        nomNomPlat.getChildren().addAll(nom,nomPlat);

        ImageView image=new ImageView();
        image.getStyleClass().add("import-plat-image");
//        fitWidth="90" fitHeight="90"  preserveRatio="true"
        image.setFitHeight(90);
        image.setFitWidth(90);
        image.setPreserveRatio(true);


        HBox nomPlatImage=new HBox();

        nomPlatImage.getChildren().addAll(nomNomPlat,image);
        nomPlatImage.setStyle(" -fx-alignment: center; -fx-spacing: 50");

        Label description=new Label("Description");
        TextArea descriptionProposer=new TextArea();
        descriptionProposer.getStyleClass().add("text-area-propo");

        VBox descriptionArea=new VBox();
        descriptionArea.getChildren().addAll(description,descriptionProposer);
        descriptionArea.setStyle("-fx-padding: 30 8 0 8");


        VBox subWrapper=new VBox();
        subWrapper.getChildren().addAll(proposerPlat,separator,nomPlatImage,descriptionArea);
        subWrapper.setStyle("-fx-padding: 15; -fx-pref-height: 450 ");


        Button envoyer=new Button("Envoyer");
        envoyer.getStyleClass().add("envoyer");



        VBox wrapper=new VBox();
        wrapper.getChildren().addAll(close,subWrapper,envoyer);
        wrapper.getStyleClass().add("inEnCoursProposeForm");
        wrapper.getStylesheets().add("/css/form.css");




        envoyer.setOnAction(event -> {

            ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(image.getImage(), null), "jpg", byteOutput);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Blob logoBlob = null;
            try {
                logoBlob = connection.createBlob();
                logoBlob.setBytes(1, byteOutput.toByteArray());

            } catch (SQLException e) {
                e.printStackTrace();
            }

            try {
                PlatProposerManager.insert(new PlatProposer(Compte.getIdCompte(), nomPlat.getText(), descriptionProposer.getText(), logoBlob, Date.valueOf(LocalDate.now())));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });





        Group group=new Group();
        group.getChildren().add(wrapper);
        return group;
    }
    private static Group createSignalerForm() {
        Button close=new Button("");
        close.getStyleClass().add("close_enCours_");



        Label signalerProbleme=new Label("Signaler un problème : ");
        signalerProbleme.setStyle("-fx-font-size: 22; -fx-text-alignment: center; -fx-padding: 0 0 0 8");


        Separator separator=new Separator();
        separator.setStyle("-fx-pref-width: 250; -fx-orientation: horizontal; -fx-padding: 5 0 25 0");

        Label description= new Label("Description:");
        description.setStyle("-fx-font-size: 21; -fx-padding: 0 15 15 0");

        HBox descriptionBox=new HBox();
        descriptionBox.getChildren().add(description);
        descriptionBox.setAlignment(Pos.BASELINE_LEFT);
        TextArea contenantTextArea=new TextArea();





        VBox subWrapper=new VBox();
        subWrapper.getChildren().addAll(signalerProbleme,separator,descriptionBox,contenantTextArea);
        subWrapper.setPrefHeight(450);
        subWrapper.setStyle("-fx-padding: 30 8 0 8");


        Button envoyer=new Button("Envoyer");
        envoyer.getStyleClass().add("envoyer");



        VBox wrapper=new VBox();
        wrapper.getChildren().addAll(close,subWrapper,envoyer);
        wrapper.getStyleClass().add("inEnCoursSignalerForm");
        wrapper.getStylesheets().add("/css/form.css");

        envoyer.setOnAction(event -> {
            try {
                ProblemeManager.insert(new Probleme(Compte.getIdCompte(), contenantTextArea.getText(), 1, Date.valueOf(LocalDate.now())));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        Group group=new Group();
        group.getChildren().add(wrapper);
        return group;
    }

}

