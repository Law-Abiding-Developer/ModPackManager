package com.lad.mmp;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;

public class ModPackManagerController {
    protected static ObservableValue<String> cellFactory(TableColumn.CellDataFeatures<ModPack, String> cell)
    {
        return cell.getValue().name;
    }

    public static void showError(String title, String message) {
        TextArea text = new TextArea(message);
        text.setEditable(false);
        text.setWrapText(true);

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText("An error occurred");
        alert.getDialogPane().setContent(text);
        alert.showAndWait();
    }

    public static void showException(Throwable e)
    {
        String message = "";
        for (var i : e.getStackTrace())
        {
            message += "at " + i + System.lineSeparator();
        }
        showError("Error", e.getClass() + " " + e.getMessage() + System.lineSeparator() + message);
    }
    public static void showException(Throwable e, String message)
    {
        message += System.lineSeparator() + e.getClass() + " " + e.getMessage() + System.lineSeparator();
        for (var i : e.getStackTrace())
            message += "at " + i + System.lineSeparator();
        showError("Error", message);
    }
    protected static TableCell<Mod, Boolean> checkboxFactory(TableColumn<Mod, Boolean> c)
    {
        var cell = CheckBoxTableCell.forTableColumn(c).call(c);
        cell.setOnMouseClicked(e ->
        {
            var mod = cell.getTableView().getItems().get(cell.getIndex());
            mod.property.set(!mod.property.get());
        });
        return cell;
    }

}
