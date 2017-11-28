package koala.gui.admin.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import koala.administration.Compte;
import koala.administration.MD5;
import koala.db.ConnectionManager;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by bjama on 4/21/2017.
 */
public class CompteUIManager {


    private static ObservableList<CompteProperty> data = FXCollections.observableArrayList();

    public static void appendComptes(StackPane centerStack, ScrollPane centerScrollPane, FlowPane centerPane, HBox actionBarForAll, TreeView treeView) {

        TableView tableView = null;
        try {
            centerPane.getChildren().clear();
            centerPane.getStylesheets().add("/css/magasinier.css");



            tableView = FXMLLoader.load(CompteUIManager.class.getResource("/fxml/compte.fxml"));


        tableView.setEditable(true);
        CompteUIManager.initTable(tableView);

        tableView.setEditable(true);
        try {
            CompteUIManager.appendData(tableView, null);
            centerPane.getChildren().add(tableView);
            FlowPane.setMargin(tableView, new Insets(55, 0,0,0));

            tableView.setPrefWidth(centerPane.getScene().getWidth()-300);

            TableView finalTableView = tableView;
            centerPane.getScene().widthProperty().addListener(observable -> {
                finalTableView.setPrefWidth(centerPane.getScene().getWidth()-300);

            });

        } catch (SQLException e) {
            e.printStackTrace();
        }
        } catch (IOException e) {
            e.printStackTrace();
        }




        Button addAll = new Button();
        addAll.getStyleClass().add("addAll");


        TableView finalTableView1 = tableView;
        addAll.setOnAction(event -> {
            FormesUIGenerator.getEditForCompteForm(centerStack, new Compte(-1), data, treeView);
        });



        Button deleteAll = new Button();
        deleteAll.getStyleClass().add("deleteAll");


        actionBarForAll.getChildren().clear();
        actionBarForAll.getChildren().addAll(addAll);

        actionBarForAll.getStyleClass().add("categorie-vction-bar");
        actionBarForAll.getStyleClass().add("categorie-top-bar");


    }



    public static void initTable(TableView tableView) {

        for ( Object column :
                tableView.getColumns()) {

            ((TableColumn<CompteProperty, String>) column).setOnEditCommit( (TableColumn.CellEditEvent<CompteProperty, String> t) -> {

                CompteProperty compteProperty = (t.getTableView().getItems().get(t.getTablePosition().getRow()));

                int initId = Integer.parseInt(compteProperty.getId());


                switch (t.getTableColumn().getText()) {
                    case "Id":
                        compteProperty.setId(t.getNewValue());
                        break;
                    case "Nom":
                        compteProperty.setId(t.getNewValue());
                        break;
                    case "Login":
                        compteProperty.setLogin(t.getNewValue());
                        break;
                    case "Password":
                        compteProperty.setPassword(new MD5(t.getNewValue()).toString());
                        break;
                    case "Pin":
                        compteProperty.setPin(t.getNewValue());
                        break;
                    case "Pin enable":
                        compteProperty.setPinEnabled(t.getNewValue());
                        break;
                }
                Compte.loadObjectToRow(initId, Integer.parseInt(compteProperty.getId()), compteProperty.getNom(), compteProperty.getLogin(), compteProperty.getPassword(), Integer.parseInt(compteProperty.getPin()), Boolean.parseBoolean(compteProperty.getPinEnabled()));

            });
        }
    }


    public static void appendData(TableView tableView, TextField textField) throws SQLException {

        data.clear();

        Connection connection = ConnectionManager.getInstance().getConnection();
        String sqladmin =
                "SELECT e.nom, c.idCompte, c.login, c.password, c.pin, c.pinEnabled from admin AS e NATURAL JOIN compte AS c WHERE c.idCompte > 0";
        String sqlmagasinier =
                "SELECT e.nom, c.idCompte, c.login, c.password, c.pin, c.pinEnabled from magasinier AS e NATURAL JOIN compte AS c WHERE c.idCompte != 0";
        String sqlcaissier =
                "SELECT e.nom, c.idCompte, c.login, c.password, c.pin, c.pinEnabled from caissier AS e NATURAL JOIN compte AS c  WHERE c.idCompte != 0";
        String sqlcuisiner =
                "SELECT e.nom, c.idCompte, c.login, c.password, c.pin, c.pinEnabled from cuisiner AS e NATURAL JOIN compte AS c WHERE c.idCompte != 0";

        Statement stmtadmin  = connection.createStatement();
        Statement stmtmagasinier  = connection.createStatement();
        Statement stmtcaissier  = connection.createStatement();
        Statement stmtcuisiner  = connection.createStatement();


        ResultSet rsadmin = stmtadmin.executeQuery(sqladmin);
        ResultSet rsmagasinier = stmtmagasinier.executeQuery(sqlmagasinier);
        ResultSet rscaissier = stmtcaissier.executeQuery(sqlcaissier);
        ResultSet rscuisiner = stmtcuisiner.executeQuery(sqlcuisiner);

        while (rsadmin.next()){

            tableView.setItems(FXCollections.observableArrayList(

                    data.add(new CompteProperty(
                            Integer.toString(rsadmin.getInt("idCompte")),
                            rsadmin.getString("nom"),
                            rsadmin.getString("login"),
                            rsadmin.getString("password"),
                            Integer.toString(rsadmin.getInt("pin")),
                            Boolean.toString(rsadmin.getBoolean("pinEnabled"))
                            ))));

        }
        while (rsmagasinier.next()){

            tableView.setItems(FXCollections.observableArrayList(

                    data.add(new CompteProperty(
                            Integer.toString(rsmagasinier.getInt("idCompte")),
                            rsmagasinier.getString("nom"),
                            rsmagasinier.getString("login"),
                            rsmagasinier.getString("password"),
                            Integer.toString(rsmagasinier.getInt("pin")),
                            Boolean.toString(rsmagasinier.getBoolean("pinEnabled"))
                            ))));

        }
        while (rscaissier.next()){

            tableView.setItems(FXCollections.observableArrayList(

                    data.add(new CompteProperty(
                            Integer.toString(rscaissier.getInt("idCompte")),
                            rscaissier.getString("nom"),
                            rscaissier.getString("login"),
                            rscaissier.getString("password"),
                            Integer.toString(rscaissier.getInt("pin")),
                            Boolean.toString(rscaissier.getBoolean("pinEnabled"))
                            ))));

        }
        while (rscuisiner.next()){

            tableView.setItems(FXCollections.observableArrayList(

                    data.add(new CompteProperty(
                            Integer.toString(rscuisiner.getInt("idCompte")),
                            rscuisiner.getString("nom"),
                            rscuisiner.getString("login"),
                            rscuisiner.getString("password"),
                            Integer.toString(rscuisiner.getInt("pin")),
                            Boolean.toString(rscuisiner.getBoolean("pinEnabled"))
                            ))));

        }

        FilteredList<CompteProperty> filteredData = new FilteredList<>(data, p -> true);

        SortedList<CompteProperty> sortedData = new SortedList<>(filteredData);

        sortedData.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.getItems().clear();

        tableView.setItems(sortedData);

    }
}
