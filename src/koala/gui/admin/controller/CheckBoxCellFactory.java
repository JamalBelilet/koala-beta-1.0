package koala.gui.admin.controller;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.util.Callback;

/**
 * Created by bjama on 5/8/2017.
 */
public class CheckBoxCellFactory<S, T> implements Callback<TableColumn<S, T>, TableCell<S, T>>{
    @Override public TableCell<S, T> call(TableColumn<S, T> p) {
        return new CheckBoxTableCell<>();
    }
}
