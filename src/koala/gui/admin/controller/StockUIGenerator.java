package koala.gui.admin.controller;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;
import koala.gestionStock.IngredientManager;
import koala.gui.magasinier.controller.IngredientProperty;
import koala.gui.magasinier.controller.IngredientUIManager;

import java.io.IOException;
import java.sql.SQLException;

import static koala.gui.magasinier.controller.IngredientUIManager.data;

/**
 * Created by bjama on 4/22/2017.
 */
public class StockUIGenerator {



    public static void appendStock(StackPane centerStack, ScrollPane centerScrollPane, FlowPane centerPane, HBox actionBarForAll, TreeView treeView) {
        centerPane.getChildren().clear();
        centerPane.getStylesheets().add("/css/magasinier.css");

        TableView tableView = null;
        try {

            tableView = FXMLLoader.load(CompteUIManager.class.getResource("/fxml/stock.fxml"));


            tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

            TableView finalTableView1 = tableView;
            tableView.setRowFactory(new Callback<TableView, TableRow>() {
                @Override
                public TableRow call(TableView param) {
                    final TableRow row = new TableRow<>();

                    MenuItem mi1 = new MenuItem("Supprimer");
                    mi1.setOnAction((ActionEvent event) -> {
                        System.out.println("Menu item 1");
                        Object item = finalTableView1.getSelectionModel().getSelectedItem();
                        System.out.println(row.getIndex());

                        IngredientManager.Delete(Integer.parseInt(((IngredientProperty) finalTableView1.getSelectionModel().getSelectedItem()).getId()));
                        data.remove(finalTableView1.getSelectionModel().getSelectedItem());

                    });

                    ContextMenu menu = new ContextMenu();
                    menu.getItems().add(mi1);
                    finalTableView1.setContextMenu(menu);

                    return row;
                }
            });



            tableView.setEditable(true);
            IngredientUIManager.initTable(tableView);

            tableView.setEditable(true);




            try {
                data = IngredientUIManager.appendData(tableView, null);
                centerPane.getChildren().add(tableView);
                FlowPane.setMargin(tableView, new Insets(0, 0,0,0));
                centerPane.setPadding(new Insets(85, 5,0,15));

                tableView.setPrefWidth(centerPane.getScene().getWidth()-287);
                tableView.setPrefHeight(centerPane.getScene().getHeight()-260);


                TableView finalTableView = tableView;
                centerPane.getScene().widthProperty().addListener(observable -> {
                    finalTableView.setPrefWidth(centerPane.getScene().getWidth()-287);
                    finalTableView.setPrefHeight(centerPane.getScene().getHeight()-260);

                });

            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }




        Button addAll = new Button();
        addAll.getStyleClass().add("addAll");

        Button deleteAll = new Button();
        deleteAll.getStyleClass().add("deleteAll");


        actionBarForAll.getChildren().clear();
        actionBarForAll.getChildren().addAll(addAll, deleteAll);

        actionBarForAll.getStyleClass().add("categorie-vction-bar");
        actionBarForAll.getStyleClass().add("categorie-top-bar");


        addAll.setOnAction(event -> {
            FormesUIGenerator.getEditForCompteForm(centerStack, data, treeView);
        });
        TableView finalTableView2 = tableView;
        deleteAll.setOnAction(event -> {
            System.out.println("___________________       " +finalTableView2.getSelectionModel().getSelectedItems().size() + "        ____________________________________");
            ObservableList<IngredientProperty> selectedItems= finalTableView2.getSelectionModel().getSelectedItems();

            for (IngredientProperty ingredient :
                    selectedItems) {

                IngredientManager.Delete(Integer.parseInt(((IngredientProperty) ingredient).getId()));

                data.remove( ingredient );
                data.remove( ingredient );
                data.remove( ingredient );
            }

            data.removeAll( selectedItems );


        });


    }

}
