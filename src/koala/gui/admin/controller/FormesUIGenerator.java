package koala.gui.admin.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import koala.administration.Compte;
import koala.db.ConnectionManager;
import koala.gestionEmployes.*;
import koala.gestionPlats.categorie.Categorie;
import koala.gestionPlats.categorie.CategorieManager;
import koala.gestionPlats.plat.Plat;
import koala.gestionPlats.plat.PlatManager;
import koala.gestionStock.IngredientManager;
import koala.gui.magasinier.controller.IngredientProperty;

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
import java.util.ArrayList;

import static koala.gui.admin.controller.EmployeUIGenerator.getStackPane;

/**
 * Created by bjama on 4/23/2017.
 */

public class FormesUIGenerator {
    public static Connection connection = ConnectionManager.getInstance().getConnection();

    public static void getEditForCategorieForm(StackPane centerStack, Categorie categorie, Boolean isUpdate, FlowPane categories, ArrayList<StackPane> tobeDeleted, ArrayList<Object> tobeDeletedCategories, ArrayList<CheckBox> selected, boolean[] deleteAllSelectionIsActive, CheckBox selectAll, StackPane recStack, TreeView treeView){




        Rectangle rect = new Rectangle(centerStack.getScene().getWidth()- 245, centerStack.getScene().getHeight() -135 -7, Color.rgb(0,0,0,.8));
        centerStack.getChildren().add(rect);

        ImageView cImage = null;
        try {
            cImage = new ImageView(new Image(categorie.getImage().getBinaryStream()));

            cImage.setFitHeight(250);
            cImage.setPreserveRatio(true);
        } catch (SQLException e) {
            cImage =new ImageView("/img/white.png");
            e.printStackTrace();
        } catch (NullPointerException nl) {
            cImage =new ImageView("/img/white.png");
        }

        ImageView finalCImage = cImage;
        cImage.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                importImg(centerStack, finalCImage);


            }
        });

        StackPane imgStackPane =new StackPane(cImage);

        Button importBtn = new Button("");
        importBtn.getStyleClass().add("import");
        importBtn.setGraphic(new ImageView(new Image("/img/import.png")));
        importBtn.setOnAction(event -> {
            importImg(centerStack, finalCImage);

        });

        imgStackPane.setOnMouseEntered(event -> {
            imgStackPane.getChildren().addAll(importBtn);
            StackPane.setAlignment(importBtn, Pos.CENTER);
        });
        imgStackPane.setOnMouseExited(event -> {
            imgStackPane.getChildren().removeAll(importBtn);
        });



        Label name = new Label("Nom");
        TextField nameEdit = new TextField(categorie.getNom());

        Label description = new Label("Description");
        TextField descriptionEdit = new TextField(categorie.getDescription());

        Button valider = new Button("Valider");
        Button annuler = new Button("Annuler");



        HBox avContainer = new HBox(15, annuler, valider);
        avContainer.setAlignment(Pos.CENTER_RIGHT);


//        GridPane gridPane = new GridPane();
//        gridPane.add(imgStackPane, 0,0, 2, 1);
//        gridPane.add(name,0,1);
//        gridPane.add(nameEdit, 1,1);
//        gridPane.add(description,0,2);
//        gridPane.add(descriptionEdit,1,2);


        VBox nameContainer = new VBox(name, nameEdit );
        VBox descriptionContainer = new VBox(description, descriptionEdit);
        VBox vBox = new VBox(imgStackPane, nameContainer, descriptionContainer, avContainer);

        centerStack.getChildren().add(vBox);


        StackPane.setAlignment(vBox, Pos.CENTER);
        vBox.setSpacing(20);
        vBox.getStyleClass().add("edit-form");

        vBox.setMaxHeight(centerStack.getScene().getHeight() -150);
        rect.setHeight(centerStack.getScene().getHeight() -135 -7);
        rect.setWidth(centerStack.getScene().getWidth() -247);

        centerStack.getScene().heightProperty().addListener(observable -> {
            vBox.setMaxHeight(centerStack.getScene().getHeight() -150);
            rect.setHeight(centerStack.getScene().getHeight() -135 -7);
        });
        centerStack.getScene().widthProperty().addListener(observable -> {
            rect.setWidth(centerStack.getScene().getWidth() -247);

        });

        rect.setOnMouseClicked(event -> {
            centerStack.getChildren().removeAll(rect, vBox);
        });

        annuler.setOnAction( event ->  {
            centerStack.getChildren().removeAll(rect, vBox);
        });
        valider.setOnAction( event -> {
            categorie.setNom(nameEdit.getText());
            categorie.setDescription(description.getText());
            centerStack.getChildren().removeAll(rect, vBox);


            if (recStack != null) {

                CategorieUIGenerator.initArrayOfCatigories.add(categorie);
            }


            try {

                ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
                ImageIO.write(SwingFXUtils.fromFXImage(finalCImage.getImage(), null), "png", byteOutput);
                Blob logoBlob = connection.createBlob();
                logoBlob.setBytes(1, byteOutput.toByteArray());

                categorie.setImage(logoBlob);

            } catch (IOException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            try {
                StackPane stackPane = getStackPane(centerStack, categories, tobeDeleted, tobeDeletedCategories, selected, deleteAllSelectionIsActive, selectAll, categorie, treeView);
                categories.getChildren().add( stackPane );
                categories.getChildren().remove( recStack );




            } catch (SQLException e) {
                e.printStackTrace();
            }


            try {
                if (isUpdate) {

                    CategorieManager.update(categorie);
                } else  {
                    CategorieManager.insert(categorie);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }



        });
        treeView.getSelectionModel().selectedItemProperty().addListener( observable -> {
            centerStack.getChildren().removeAll(rect, vBox);

        });

    }

    public static void getEditForCategorieForm(StackPane centerStack, Plat plat, Boolean isUpdate, FlowPane platsContainer, ArrayList<StackPane> tobeDeleted, ArrayList<Object> tobeDeletedCategories, ArrayList<CheckBox> selected, boolean[] deleteAllSelectionIsActive, CheckBox selectAll, StackPane recStack, TreeView treeView){

        Rectangle rect = new Rectangle(centerStack.getScene().getWidth()- 245, centerStack.getScene().getHeight() -135 -7, Color.rgb(0,0,0,.8));
        centerStack.getChildren().add(rect);

        ImageView cImage = null;
        try {
            cImage = new ImageView(new Image(plat.getImage().getBinaryStream()));

            cImage.setFitHeight(250);
            cImage.setPreserveRatio(true);
        } catch (SQLException e) {
            cImage =new ImageView("/img/white.png");
            e.printStackTrace();
        } catch (NullPointerException nl) {
            cImage =new ImageView("/img/white.png");
        }
        ImageView finalCImage = cImage;
        cImage.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                importImg(centerStack, finalCImage);


            }
        });

        StackPane imgStackPane =new StackPane(cImage);

        Button importBtn = new Button("");
        importBtn.getStyleClass().add("import");
        importBtn.setGraphic(new ImageView(new Image("/img/import.png")));
        importBtn.setOnAction(event -> {
            importImg(centerStack, finalCImage);

        });

        imgStackPane.setOnMouseEntered(event -> {
            imgStackPane.getChildren().addAll(importBtn);
            StackPane.setAlignment(importBtn, Pos.CENTER);
        });
        imgStackPane.setOnMouseExited(event -> {
            imgStackPane.getChildren().removeAll(importBtn);
        });



        Label name = new Label("Nom");
        TextField nameEdit = new TextField(plat.getNom());

        Label description = new Label("Description");
        TextField descriptionEdit = new TextField(plat.getDescription());

        Label categorie = new Label("Catégorie");
        ComboBox<Categorie> categorieComboBox = new ComboBox<>();
        try {
            categorieComboBox.setItems(
                    FXCollections.observableArrayList(
                            CategorieManager.loadTableToList()
                    )
            );
            categorieComboBox.getSelectionModel().selectFirst();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            categorieComboBox.setValue(CategorieManager.getCategorie(plat.getIdCategorie()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Label prix = new Label("Prix");
        TextField prixEdit = new TextField(Double.toString(plat.getPrix()));

        Label tempsPreparation = new Label("Temps préparation");
        TextField tempsPreparationEdit = new TextField(Double.toString(plat.getTempsPreparation()));


        Label parallele = new Label("Parallèle");
        TextField paralleleEdit = new TextField(Integer.toString(plat.getParallele()));

        Button valider = new Button("Valider");
        Button annuler = new Button("Annuler");
        HBox avContainer = new HBox(15, annuler, valider);
        avContainer.setAlignment(Pos.CENTER_RIGHT);


//        GridPane gridPane = new GridPane();
//        gridPane.add(imgStackPane, 0,0, 2, 1);
//        gridPane.add(name,0,1);
//        gridPane.add(nameEdit, 1,1);
//        gridPane.add(description,0,2);
//        gridPane.add(descriptionEdit,1,2);


        VBox nameContainer = new VBox(name, nameEdit );
        VBox descriptionContainer = new VBox(description, descriptionEdit);
        HBox categorieContainer = new HBox(15, categorie, categorieComboBox);
        categorieContainer.setAlignment(Pos.CENTER_LEFT);

        VBox prixContainer = new VBox(prix, prixEdit);
        VBox tempsPreparationContainer = new VBox(tempsPreparation, tempsPreparationEdit);
        VBox paralleleContainer = new VBox(parallele, paralleleEdit);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(15);
        gridPane.setVgap(15);
        gridPane.add(prixContainer, 0, 0);
        gridPane.add(tempsPreparationContainer, 1, 0);
        gridPane.add(paralleleContainer, 0, 1);
        gridPane.setStyle("-fx-background-color: transparent");

        VBox vBox = new VBox(
                imgStackPane,
                nameContainer,
                descriptionContainer,
                categorieContainer,
                gridPane,
                avContainer
        );
        ScrollPane scrollPane = new ScrollPane(vBox);

        centerStack.getChildren().add(scrollPane);
        scrollPane.setMaxWidth(vBox.getWidth());


        StackPane.setAlignment(scrollPane, Pos.CENTER);
        vBox.setSpacing(20);
        vBox.getStyleClass().add("edit-form");
        scrollPane.getStyleClass().add("edit-form");

        scrollPane.setMaxHeight(centerStack.getScene().getHeight() -150);
        rect.setHeight(centerStack.getScene().getHeight() -135 -7);
        rect.setWidth(centerStack.getScene().getWidth() -247);

        centerStack.getScene().heightProperty().addListener(observable -> {
            scrollPane.setMaxHeight(centerStack.getScene().getHeight() -150);
            rect.setHeight(centerStack.getScene().getHeight() -135 -7);
        });
        centerStack.getScene().widthProperty().addListener(observable -> {
            rect.setWidth(centerStack.getScene().getWidth() -247);

        });



        rect.setOnMouseClicked(event -> {
            centerStack.getChildren().removeAll(rect, scrollPane);
        });

        annuler.setOnAction( event ->  {
            centerStack.getChildren().removeAll(rect, scrollPane);
        });
        valider.setOnAction( event -> {

            centerStack.getChildren().removeAll(rect, scrollPane);

            plat.setNom(nameEdit.getText());
            plat.setDescription(descriptionEdit.getText());
            plat.setIdCategorie(categorieComboBox.getValue().getIdCategorie());
            plat.setPrix(Double.parseDouble(prixEdit.getText()));
            plat.setTempsPreparation(((int) Double.parseDouble(tempsPreparationEdit.getText())));
            plat.setParallele(Integer.parseInt(paralleleEdit.getText()));

            try {

                ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
                ImageIO.write(SwingFXUtils.fromFXImage(finalCImage.getImage(), null), "png", byteOutput);
                Blob logoBlob = connection.createBlob();
                logoBlob.setBytes(1, byteOutput.toByteArray());

                plat.setImage(logoBlob);

            } catch (IOException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }


            StackPane stackPane = null;
            try {
                stackPane = PlatUIGenerateur.getStackPane(centerStack, platsContainer, tobeDeleted, tobeDeletedCategories, selected, deleteAllSelectionIsActive, selectAll, plat, treeView);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            platsContainer.getChildren().add( stackPane );
            platsContainer.getChildren().remove( recStack );





            try {
                if (isUpdate) {
                    PlatManager.update(plat);
                } else {
                    PlatManager.insert(plat);
                }



            } catch (Exception e) {
                e.printStackTrace();
            }


        });
        treeView.getSelectionModel().selectedItemProperty().addListener( observable -> {
            try {
                centerStack.getChildren().removeAll(rect, scrollPane);
            } catch (Exception esfad) {
                System.out.println("null pointer frob tree !");
            }

        });



    }

    public static void getEditForCategorieForm(StackPane centerStack, Employe employe, boolean isUpdate, FlowPane platsContainer, ArrayList<StackPane> tobeDeleted, ArrayList<Object> tobeDeletedCategories, ArrayList<CheckBox> selected, boolean[] deleteAllSelectionIsActive, CheckBox selectAll, StackPane recStack, TreeView treeView){

        Rectangle rect = new Rectangle(centerStack.getScene().getWidth()- 245, centerStack.getScene().getHeight() -135 -7, Color.rgb(0,0,0,.8));
        centerStack.getChildren().add(rect);

        ImageView cImage = null;
        try {
            cImage = new ImageView(new Image(employe.getImage().getBinaryStream()));


        } catch (SQLException e) {
            cImage =new ImageView("/img/employe.png");
            e.printStackTrace();
        } catch (NullPointerException nl) {
            cImage =new ImageView("/img/employe.png");
        }
        cImage.setFitHeight(187);
        cImage.setPreserveRatio(true);

        ImageView finalCImage = cImage;
        cImage.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                importImg(centerStack, finalCImage);


            }
        });

        StackPane imgStackPane =new StackPane(cImage);

        Button importBtn = new Button("");
        importBtn.getStyleClass().add("import");
        importBtn.setGraphic(new ImageView(new Image("/img/import.png")));
        importBtn.setOnAction(event -> {
            importImg(centerStack, finalCImage);

        });

        imgStackPane.setOnMouseEntered(event -> {
            imgStackPane.getChildren().addAll(importBtn);
            StackPane.setAlignment(importBtn, Pos.CENTER);
        });
        imgStackPane.setOnMouseExited(event -> {
            imgStackPane.getChildren().removeAll(importBtn);
        });



        Label name = new Label("Nom");
        TextField nameEdit = new TextField();
        if (isUpdate) {

            nameEdit.setText(employe.getNom());

        }

        Label prenom = new Label("Prénom");
        TextField prenomEdit = new TextField();
        if (isUpdate) {

            prenomEdit.setText(employe.getPrenom());


        }

        Label telephone = new Label("Téléphone");
        TextField telephoneEdit = new TextField();
        if (isUpdate) {

            telephoneEdit.setText(employe.getTelephone());


        }

        Label email = new Label("Email");
        TextField emailEdit = new TextField();
        if (isUpdate) {

            emailEdit.setText(employe.getEmail());


        }



        Label dateNaissance = new Label("Date de naissance");
        DatePicker dateNaissancePicker;
        try {
            dateNaissancePicker = new DatePicker(employe.getDateNaissance().toLocalDate());

        } catch (NullPointerException nl) {
            dateNaissancePicker = new DatePicker(LocalDate.now().plusYears(-18));

        }


        dateNaissancePicker.getStyleClass().add("date-picker");

        Label sexe = new Label("Sexe");
        ComboBox<String> sexeComboBox = new ComboBox<>(
                FXCollections.observableArrayList(
                        "M",
                        "F"
                )
        );
        sexeComboBox.setValue("M");
        if (isUpdate) {

            sexeComboBox.setValue(employe.getSexe());


        }

        Label typeLabel = new Label("Type d'employé");
        ComboBox<String> typeComboBox = new ComboBox<>(
                FXCollections.observableArrayList(
                        "Cuisiner",
                        "Magasinier",
                        "Caissier",
                        "Admin"
                )
        );
        typeComboBox.getSelectionModel().selectFirst();
        if (isUpdate) {

            typeComboBox.setValue(employe.getClass().getSimpleName());


        }

        Label compteLabel = new Label("Numéro de compte");

        ComboBox<String> compteComboBox = new ComboBox<>(
                FXCollections.observableArrayList(
                        "Cuisiner",
                        "Magasinier",
                        "Caissier",
                        "Admin"
                )
        );
        typeComboBox.getSelectionModel().selectFirst();
        if (isUpdate) {

            typeComboBox.setValue(employe.getClass().getSimpleName());


        }

        Button valider = new Button("Valider");
        Button annuler = new Button("Annuler");
        HBox avContainer = new HBox(15, annuler, valider);
        avContainer.setAlignment(Pos.CENTER_RIGHT);


        VBox nameContainer = new VBox(name, nameEdit );
        VBox prenomContainer = new VBox(prenom, prenomEdit);
        VBox telephoneContainer = new VBox(telephone, telephoneEdit);
        VBox emailContainer = new VBox(email, emailEdit);
        VBox dateNaissanceContainer = new VBox(dateNaissance, dateNaissancePicker);
        VBox sexeContainer = new VBox(sexe, sexeComboBox);
        VBox typeContainer = new VBox(typeLabel, typeComboBox);

        sexeContainer.setAlignment(Pos.CENTER_LEFT);
        typeContainer.setAlignment(Pos.CENTER_LEFT);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(35);
        gridPane.setVgap(25);
        gridPane.add(nameContainer, 0, 0);
        gridPane.add(prenomContainer, 1, 0);
        gridPane.add(telephoneContainer, 0, 1);
        gridPane.add(emailContainer, 1, 1);
        gridPane.add(dateNaissanceContainer, 0, 2);
        gridPane.add(sexeContainer, 1, 2);
        gridPane.add(typeContainer, 1, 3);
        gridPane.add(avContainer, 1, 4);
        gridPane.setStyle("-fx-background-color: transparent");

        GridPane.setMargin(avContainer, new Insets(30, 0, 0, 0));

        VBox vBox = new VBox(
                imgStackPane,
                gridPane
        );
        ScrollPane scrollPane = new ScrollPane(vBox);

        centerStack.getChildren().add(scrollPane);
        scrollPane.setMaxWidth(vBox.getWidth());


        StackPane.setAlignment(scrollPane, Pos.CENTER);
        vBox.setSpacing(20);
        vBox.getStyleClass().add("edit-form");
        gridPane.getStyleClass().add("edit-form");
        gridPane.setPadding(new Insets(0));
        scrollPane.getStyleClass().add("edit-form");

        scrollPane.setMaxHeight(centerStack.getScene().getHeight() -150);
        rect.setHeight(centerStack.getScene().getHeight() -135 -7);
        rect.setWidth(centerStack.getScene().getWidth() -247);

        centerStack.getScene().heightProperty().addListener(observable -> {
            scrollPane.setMaxHeight(centerStack.getScene().getHeight() -150);
            rect.setHeight(centerStack.getScene().getHeight() -135 -7);
        });
        centerStack.getScene().widthProperty().addListener(observable -> {
            rect.setWidth(centerStack.getScene().getWidth() -247);

        });



        rect.setOnMouseClicked(event -> {
            centerStack.getChildren().removeAll(rect, scrollPane);
        });

        annuler.setOnAction( event ->  {
            centerStack.getChildren().removeAll(rect, scrollPane);
        });
        DatePicker finalDateNaissancePicker = dateNaissancePicker;


        if (isUpdate) {
            typeComboBox.setDisable(true);
        }



        valider.setOnAction(event -> {
            Employe finalEmploye = employe;

            if (!isUpdate) {


                switch (typeComboBox.getSelectionModel().getSelectedItem()) {
                    case "Cuisiner":
                        finalEmploye = new Cuisiner();
                        finalEmploye.setIdMagasin(1);

                        finalEmploye.setIdCompte(0);
                        System.out.println(typeComboBox.getValue() + "------------" + typeComboBox.getSelectionModel().getSelectedItem());

                        break;
                    case "Magasinier":
                        finalEmploye = new Magasinier();
                        finalEmploye.setIdMagasin(1);

                        finalEmploye.setIdCompte(0);

                        break;
                    case "Caissier":
                        finalEmploye = new Caissier();
                        finalEmploye.setIdMagasin(1);

                        finalEmploye.setIdCompte(0);

                        break;
                    case "Admin":
                        finalEmploye = new Admin();
                        finalEmploye.setIdMagasin(1);

                        finalEmploye.setIdCompte(0);

                        break;
                    default:
//                    System.out.println(typeComboBox.getValue());
                }


            } else {
                // TODO: 5/26/2017
            }



            rect.setVisible(false);
            rect.setManaged(false);

            centerStack.getChildren().removeAll(rect, scrollPane);





            Employe finalEmploye1 = finalEmploye;
            new Thread(new Runnable() {
                @Override
                public void run() {

                    finalEmploye1.setNom(nameEdit.getText());
                    finalEmploye1.setPrenom(prenomEdit.getText());
                    finalEmploye1.setTelephone(telephoneEdit.getText());
                    finalEmploye1.setEmail(emailEdit.getText());
                    finalEmploye1.setDateNaissance(Date.valueOf(finalDateNaissancePicker.getValue()));
                    finalEmploye1.setSexe(sexeComboBox.getValue());

                    try {

                        ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
                        ImageIO.write(SwingFXUtils.fromFXImage(finalCImage.getImage(), null), "png", byteOutput);
                        Blob logoBlob = connection.createBlob();
                        logoBlob.setBytes(1, byteOutput.toByteArray());

                        finalEmploye1.setImage(logoBlob);

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }


                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                StackPane stackPane = getStackPane(centerStack, platsContainer, tobeDeleted, tobeDeletedCategories, selected, deleteAllSelectionIsActive, selectAll, finalEmploye1, treeView);
                                platsContainer.getChildren().add( stackPane );
                                platsContainer.getChildren().remove( recStack );


                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                    });


                    try {
                        if (isUpdate){
                            EmployeManager.update(finalEmploye1);
                        } else {
                            EmployeManager.insert(finalEmploye1);
                        }

                    } catch (SQLException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }





                }
            }).start();


        });

        treeView.getSelectionModel().selectedItemProperty().addListener( observable -> {
            try {
                centerStack.getChildren().removeAll(rect, scrollPane);
            } catch (Exception esfad) {
                System.out.println("null pointer frob tree !");
            }

        });



    }

    public static void getEditForCompteForm(StackPane centerStack, Compte compte, ObservableList<CompteProperty> data, TreeView treeView){

        Rectangle rect = new Rectangle(centerStack.getScene().getWidth()- 245, centerStack.getScene().getHeight() -135 -7, Color.rgb(0,0,0,.8));
        centerStack.getChildren().add(rect);





        Label login = new Label("Login");
        login.setStyle("-fx-font-size: 16");
        TextField loginEdit = new TextField();
        loginEdit.setPromptText("ex: doe02");

        Label password = new Label("Password");
        password.setStyle("-fx-font-size: 16");
        PasswordField passwordEdit = new PasswordField();

        Label pinEn = new Label("Pin");
        PasswordField pinEdit = new PasswordField();
        pinEn.setStyle("-fx-font-size: 16");

        Label employe = new Label("Employé");
        employe.setStyle("-fx-font-size: 16");
        ComboBox<Employe> employeComboBox = new ComboBox();



        Button valider = new Button("Valider");
        Button annuler = new Button("Annuler");
        HBox avContainer = new HBox(15, annuler, valider);
        avContainer.setPadding(new Insets(0, 25, 0, 0));
        avContainer.setAlignment(Pos.CENTER_RIGHT);


        VBox loginContainer = new VBox(5, login, loginEdit);
        VBox passwordContainer = new VBox(5, password, passwordEdit);
        VBox pinContainer = new VBox(5, pinEn, pinEdit);
        VBox employeContainer = new VBox(5, employe, employeComboBox);


        HBox rowContainerH = new HBox(
                20,
                loginContainer,
                passwordContainer,
                pinContainer,
                employeContainer
        );
        rowContainerH.setAlignment(Pos.BOTTOM_CENTER);
        VBox container = new VBox(
                rowContainerH,
                avContainer
        );

        centerStack.getChildren().add(container);


        StackPane.setAlignment(container, Pos.CENTER);
        container.setSpacing(20);
        container.getStyleClass().addAll("no-max-table", "edit-form");


        container.setMaxHeight(150);
        container.setMaxWidth(870);




        rect.setHeight(centerStack.getScene().getHeight() - 135 - 7);
        rect.setWidth(centerStack.getScene().getWidth() - 247);

        centerStack.getScene().heightProperty().addListener(observable -> {

            rect.setHeight(centerStack.getScene().getHeight() - 135 - 7);
        });
        centerStack.getScene().widthProperty().addListener(observable -> {
            rect.setWidth(centerStack.getScene().getWidth() - 247);

        });
        Label errorLabel = null;

        try {

            try {
                employeComboBox.setItems(
                        FXCollections.observableArrayList(
                                EmployeManager.employesNonCompte()
                        )
                );

            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                employeComboBox.getSelectionModel().selectFirst();
            } catch (Exception e) {
                e.printStackTrace();
            }


            annuler.setOnAction(event -> {
                centerStack.getChildren().removeAll(rect, container);
            });


            valider.setOnAction(event -> {

                centerStack.getChildren().removeAll(rect, container);


                Compte c = new Compte(-1, loginEdit.getText(), passwordEdit.getText(), pinEdit.getText(), false);
                int idCompte = c.ajouter_compte();

                employeComboBox.getSelectionModel().getSelectedItem().setIdCompte(idCompte);


                data.add(
                        new CompteProperty(
                                Integer.toString(idCompte),
                                employeComboBox.getValue().getNom(),
                                loginEdit.getText(),
                                passwordEdit.getText(),
                                pinEdit.getText(),
                                "false"
                        )
                );


                EmployeManager.updateIdCompte(employeComboBox.getSelectionModel().getSelectedItem());

            });




        } catch (NullPointerException nullPE) {
            System.out.println("none employe !");

            centerStack.getChildren().remove(container);
            errorLabel = new Label("Il n'y a plus d'employé sans compte.");
            errorLabel.setStyle("-fx-font-size: 18; -fx-text-fill: white");
            StackPane.setAlignment(errorLabel, Pos.CENTER);
            centerStack.getChildren().remove(container);
            centerStack.getChildren().add(errorLabel);


        }


        Label finalErrorLabel = errorLabel;
        rect.setOnMouseClicked(event -> {
            centerStack.getChildren().removeAll(finalErrorLabel, rect, container);
        });

        treeView.getSelectionModel().selectedItemProperty().addListener( observable -> {
            centerStack.getChildren().removeAll(finalErrorLabel, rect, container);

        });


    }
    public static void getEditForCompteForm(StackPane centerStack, ObservableList<IngredientProperty> data, TreeView treeView){

        Rectangle rect = new Rectangle(centerStack.getScene().getWidth()- 245, centerStack.getScene().getHeight() -135 -7, Color.rgb(0,0,0,.8));
        centerStack.getChildren().add(rect);





        Label nomLabel = new Label("Nom");
        nomLabel.setStyle("-fx-font-size: 16");
        TextField nomEdit = new TextField();
        nomEdit.setPromptText("ex: doe02");

        Label descriptionLabel = new Label("Description");
        descriptionLabel.setStyle("-fx-font-size: 16");
        TextArea descriptionArea = new TextArea();

        Label quantitieLabel = new Label("quantite");
        quantitieLabel.setStyle("-fx-font-size: 16");
        TextField quantitieEdit = new TextField();
        quantitieEdit.setPrefColumnCount(5);

        Label prixLabel = new Label("prix");
        prixLabel.setStyle("-fx-font-size: 16");
        TextField prixEdit = new TextField();
        prixEdit.setPrefColumnCount(5);


        Label dateLabel = new Label("date peremption");
        dateLabel.setStyle("-fx-font-size: 16");
        DatePicker datePicker = new DatePicker();


        Label caloriesLabel = new Label("calories");
        caloriesLabel.setStyle("-fx-font-size: 16");
        TextField caloriesEdit = new TextField();
        caloriesEdit.setPrefColumnCount(5);


        Label proteinesLabel = new Label("proteines");
        proteinesLabel.setStyle("-fx-font-size: 16");
        TextField proteinesEdit = new TextField();
        proteinesEdit.setPrefColumnCount(5);


        Label glucidesLabel = new Label("glucides");
        glucidesLabel.setStyle("-fx-font-size: 16");
        TextField glucidesEdit = new TextField();
        glucidesEdit.setPrefColumnCount(5);


        Label lipidesLabel = new Label("lipides");
        lipidesLabel.setStyle("-fx-font-size: 16");
        TextField lipidesEdit = new TextField();
        lipidesEdit.setPrefColumnCount(5);


        CheckBox glutenCheckBox = new CheckBox("gluten");
        glutenCheckBox.setStyle("-fx-font-size: 16");
        glutenCheckBox.getStyleClass().add("select");
        glutenCheckBox.getStyleClass().add("in-form");


        Button valider = new Button("Valider");
        Button annuler = new Button("Annuler");
        HBox avContainer = new HBox(15, annuler, valider);
        avContainer.setPadding(new Insets(0, 25, 0, 0));
        avContainer.setAlignment(Pos.CENTER_RIGHT);


        VBox nomContainer = new VBox(5, nomLabel, nomEdit);
        VBox descriptionContainer = new VBox(5, descriptionLabel, descriptionArea);
        VBox quantiteContainer = new VBox(5, quantitieLabel, quantitieEdit);
        VBox prixContainer = new VBox(5, prixLabel, prixEdit);
        VBox dateContainer = new VBox(5, dateLabel, datePicker);
        VBox caloriesContainer = new VBox(5, caloriesLabel, caloriesEdit);
        VBox proteinesContainer = new VBox(5, proteinesLabel, proteinesEdit);
        VBox glucidesContainer = new VBox(5, glucidesLabel, glucidesEdit);
        VBox lipidesContainer = new VBox(5, lipidesLabel, lipidesEdit);
        VBox glutenContainer = new VBox(5, glutenCheckBox);



        HBox row_0 = new HBox(
                20,
                nomContainer,
                prixContainer,
                quantiteContainer,
                dateContainer
        );
        HBox row_1 = new HBox(
                dateContainer

        );
        HBox row_2 = new HBox(

                glutenContainer

        );
        row_2.setPadding( new Insets(10, 0, 0, 0));
        row_2.setAlignment(Pos.BOTTOM_LEFT);
        HBox row_3 = new HBox(
                15,
                caloriesContainer,
                proteinesContainer,
                glucidesContainer,
                lipidesContainer
        );
        HBox row_4 = new HBox(
                5,
                descriptionContainer
        );


        VBox container = new VBox(
                15,
                row_0,
                row_1,
                row_2,
                row_3,
                row_4,
                avContainer
        );

        centerStack.getChildren().add(container);


        StackPane.setAlignment(container, Pos.CENTER);
        StackPane.setMargin(container, new Insets(15,0,15,0));

        container.getStyleClass().addAll("edit-form");


//        container.setMaxHeight(150);
        container.setMaxWidth(870);




        rect.setHeight(centerStack.getScene().getHeight() - 135 - 7);
        rect.setWidth(centerStack.getScene().getWidth() - 247);

        centerStack.getScene().heightProperty().addListener(observable -> {

            rect.setHeight(centerStack.getScene().getHeight() - 135 - 7);
        });
        centerStack.getScene().widthProperty().addListener(observable -> {
            rect.setWidth(centerStack.getScene().getWidth() - 247);

        });

        annuler.setOnAction(event -> {
            centerStack.getChildren().removeAll(rect, container);
        });


        valider.setOnAction(event -> {

            centerStack.getChildren().removeAll(rect, container);

//
            int idIngredient = IngredientManager.addIngredient(
                    nomEdit.getText(),
                    descriptionArea.getText(),
                    Double.parseDouble(caloriesEdit.getText()),
                    Double.parseDouble(proteinesEdit.getText()),
                    Double.parseDouble(glucidesEdit.getText()),
                    Double.parseDouble(lipidesEdit.getText()),
                    glutenCheckBox.isSelected(),
                    1,
                    Integer.parseInt(quantitieEdit.getText()),
                    Double.parseDouble(prixEdit.getText()),
                    datePicker.getValue()

            );


            data.add(
                    new IngredientProperty(
                        "1",
                        Integer.toString(idIngredient),
                        nomEdit.getText(),
                        quantitieEdit.getText(),
                        prixEdit.getText() + "DA",
                        datePicker.getValue().toString(),
                        descriptionArea.getText(),
                        caloriesEdit.getText(),
                        proteinesEdit.getText(),
                        glucidesEdit.getText(),
                        lipidesEdit.getText(),
                        Boolean.toString(glutenCheckBox.isSelected())
                    )
            );

        });


        rect.setOnMouseClicked(event -> {
            centerStack.getChildren().removeAll(rect, container);
        });

        treeView.getSelectionModel().selectedItemProperty().addListener( observable -> {
            centerStack.getChildren().removeAll(rect, container);

        });


    }

    private static void importImg(StackPane centerStack, ImageView finalCImage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open new image File");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.gif", "*.png", "*.jpg"),
                new FileChooser.ExtensionFilter("GIF", "*.gif"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("PNG", "*.png")
        );
        File file = fileChooser.showOpenDialog(centerStack.getScene().getWindow());
        try {
            BufferedImage bufferedImage = ImageIO.read(file);
            Image image = SwingFXUtils.toFXImage(bufferedImage, null);
            finalCImage.setImage(image);
        } catch (IOException ex) {

        } catch (IllegalArgumentException ex) {}
    }


}
