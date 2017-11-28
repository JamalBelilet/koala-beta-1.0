package koala.gui.magasinier.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import koala.db.ConnectionManager;
import koala.gestionStock.Ingredient;
import koala.gestionStock.StockManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;


/**
 * Created by bjama on 3/23/2017.
 */
public class IngredientUIManager {

    public static ObservableList<IngredientProperty> data =FXCollections.observableArrayList();


    public static void initTable(TableView tableView) {



        for ( Object column :
                tableView.getColumns()) {

            ((TableColumn<IngredientProperty, String>) column).setOnEditCommit( (TableColumn.CellEditEvent<IngredientProperty, String> t) -> {

                IngredientProperty ingredientProperty = (t.getTableView().getItems().get(t.getTablePosition().getRow()));

                int initId = Integer.parseInt(ingredientProperty.getId());
                int initMagasin = Integer.parseInt(ingredientProperty.getMagasin());


                switch (t.getTableColumn().getText()) {
                    case "Magasin":
                        ingredientProperty.setMagasin(t.getNewValue());
                        break;
                    case "Id":
                        ingredientProperty.setId(t.getNewValue());
                        break;
                    case "Nom":
                        ingredientProperty.setName(t.getNewValue());
                        break;
                    case "Quantité":
                        ingredientProperty.setQuantitie(t.getNewValue());
                        break;
                    case "Prix achat":
                        ingredientProperty.setPrixAchat(t.getNewValue());
                        break;
                    case "Date péremption":
                        ingredientProperty.setDatePeremption(t.getNewValue());
                        break;
                    case "Description":
                        ingredientProperty.setDescription(t.getNewValue());
                        break;
                    case "Calories":
                        ingredientProperty.setCalorie(t.getNewValue());
                        break;
                    case "Protéines":
                        ingredientProperty.setProteine(t.getNewValue());
                        break;
                    case "Glucides":
                        ingredientProperty.setGlucide(t.getNewValue());
                        break;
                    case "Lipides":
                        ingredientProperty.setLipide(t.getNewValue());
                        break;
                    case "Gluten":
                        ingredientProperty.setGluten(t.getNewValue());
                        break;
                }
                Ingredient.updateIngredientTable(initId, Integer.parseInt(ingredientProperty.getId()), ingredientProperty.getName(), ingredientProperty.getDescription(), Integer.parseInt(ingredientProperty.getCalorie()), Integer.parseInt(ingredientProperty.getProteine()), Integer.parseInt(ingredientProperty.getGlucide()), Integer.parseInt(ingredientProperty.getLipide()), Boolean.parseBoolean(ingredientProperty.getGluten()));
                StockManager.update(Integer.parseInt(ingredientProperty.getMagasin()), Integer.parseInt(ingredientProperty.getId()), Integer.parseInt(ingredientProperty.getQuantitie()), Double.parseDouble(ingredientProperty.getPrixAchat().substring(0, ingredientProperty.getPrixAchat().length()-1)), LocalDate.parse(ingredientProperty.getDatePeremption()));

            });
        }
    }


    public static ObservableList<IngredientProperty> appendData(TableView tableView, TextField textField) throws SQLException {

        Connection connection = ConnectionManager.getInstance().getConnection();
        String sql =
                "SELECT s.idMagasin, s.idIngredient, i.nom, s.quantitie, s.prixAchat, s.datePeremption, i.description, i.calorie, i.proteine, i.glucide, i.lipide, i.gluten FROM stock as s JOIN ingredient as i WHERE idMagasin = ? and s.idIngredient = i.idIngredient";

        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, 1);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()){

            tableView.setItems(FXCollections.observableArrayList(

            data.add(new IngredientProperty(
                    Integer.toString(rs.getInt("idMagasin")),
                    Integer.toString(rs.getInt("idIngredient")),
                    rs.getString("nom"),
                    Integer.toString(rs.getInt("quantitie")),
                    Double.toString(rs.getDouble("prixAchat")) + "DA",
                    rs.getDate("datePeremption").toString(),
                    rs.getString("description"),
                    Integer.toString(rs.getInt("calorie")),
                    Integer.toString(rs.getInt("proteine")),
                    Integer.toString(rs.getInt("glucide")),
                    Integer.toString(rs.getInt("lipide")),
                    Integer.toString(rs.getInt("gluten"))))));

        }

        FilteredList<IngredientProperty> filteredData = new FilteredList<>(data, p -> true);

        if (textField != null)
            textField.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredData.setPredicate(ingredientProperty -> {

                    if (newValue == null || newValue.isEmpty()) {
                        return true;
                    }

                    String lowerCaseFilter = newValue.toLowerCase();

                    try {
                        if (ingredientProperty.getName().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                            return true;
                        } else if (ingredientProperty.getDescription().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                            return true;
                        }
                    } catch (NullPointerException es) {

                    }
                    return false;
                });
            });

        SortedList<IngredientProperty> sortedData = new SortedList<>(filteredData);

        sortedData.comparatorProperty().bind(tableView.comparatorProperty());

        tableView.setItems(sortedData);

        return data;

    }
}
