package com.lad.mmp;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;

public class ModPackManagerController {
    protected static ObservableValue<String> cellFactory(TableColumn.CellDataFeatures<ModPack, String> cell)
    {
        return cell.getValue().name;
    }

    protected static Site ParseFromString(String s)
    {
        if (s.equals("Reika's Site")) return Site.REIKA;
        if (s.equals("Github")) return Site.GITHUB;
        if (s.equals("Nexus Mods")) return Site.NEXUSMODS;
        return Site.NULL;
    }

    protected static String convertToString(Site s)
    {
        return switch (s) {
            case REIKA -> "Reika's Site";
            case NULL -> "";
            case GITHUB -> "Github";
            case NEXUSMODS -> "Nexus Mods";
        };
    }

    public static void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText("An error occurred");
        alert.setContentText(message);
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

    public enum Site {
        NULL,
        REIKA,
        GITHUB,
        NEXUSMODS
    }
}
