package com.lad.mmp.Misc;

import com.lad.mmp.Main.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import jdk.jshell.spi.ExecutionControl;

public class Buttons {
    public static Button getModButton(ModPackManager mmp)
    {
        Button newModButton = new Button("Add Mod to Selected Mod Pack");
        newModButton.setOnAction(e ->
        {
            ModPack selected = mmp.modpacks.getSelectionModel().getSelectedItem();
            if (selected != null)
            {
                Dialog addMod = new Dialog();
                addMod.setTitle("Add Details");
                addMod.setHeaderText("Fill the boxes below with the mod info");
                ChoiceBox<String> choice = new ChoiceBox<>();
                ObservableList<String> list = FXCollections.observableArrayList();
                //list.add("Github");
                //list.add("Nexus Mods");
                list.add("Reika's Site");
                list.add("");
                choice.setItems(list);
                choice.setValue("");
                TextField nameField = new TextField();
                nameField.setPromptText("Name");
                TextField linkField = new TextField();
                linkField.setPromptText("Link");
                VBox content = new VBox(10, new Label("Name: "), nameField,
                        new Label("Link: "), linkField,
                        new Label("Site: "), choice);
                addMod.getDialogPane().setContent(content);
                addMod.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
                addMod.setResultConverter(button ->
                {
                    try
                    {
                        if (button == ButtonType.OK)
                        {
                            return new Mod(nameField.getText(), linkField.getText(),
                                    choice.getValue(),
                                    SimpleStatusProperty.Status.NOTDOWNLOADED, mmp);
                        }
                    }
                    catch (Exception f)
                    {
                        ModPackManagerController.showException(f);
                    }
                    return null;
                });
                var string = addMod.showAndWait();
                if (string.isPresent())
                {
                    Mod mod = (Mod) string.get();
                    selected.mods.add(mod);
                    mmp.mods.setItems(selected.mods);
                    mmp.mods.refresh();
                    mmp.modpacks.refresh();
                }
            }
        });
        return newModButton;
    }
    public static Button getModPackButton(ModPackManager mmp)
    {
        Button newModPackButton = new Button("New Mod Pack");
        newModPackButton.setOnAction(e ->
        {
            Dialog addModPack = new Dialog();
            addModPack.setTitle("Add Details");
            addModPack.setHeaderText("Please fill in the boxes below");
            addModPack.setContentText("Instructions: 5");
            ChoiceBox<String> gameChoice =
                    new ChoiceBox<>(FXCollections.observableArrayList("Subnautica", ""));
            gameChoice.setValue("");
            ChoiceBox<String> versionChoice =
                    new ChoiceBox<>(FXCollections.observableArrayList("Legacy", ""));
            versionChoice.setValue("");
            TextField name = new TextField();
            name.setPromptText("Name");
            TextField gamePath = new TextField();
            gamePath.setPromptText("Game Folder Path");
            VBox content = new VBox(10, new Label("Name: "), name, new Label("Game Folder Path: "),
                    gamePath, new Label("Game: "), gameChoice, new Label("Version: "), versionChoice);
            addModPack.getDialogPane().setContent(content);
            addModPack.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            addModPack.setResultConverter(object ->
            {
                try
                {
                    if (object == ButtonType.OK)
                    {
                        var modPack = new ModPack(name.getText(), FXCollections.observableArrayList(),
                                gamePath.getText(), gameChoice.getValue(), versionChoice.getValue(), mmp);
                        return modPack;
                    }
                    return null;
                }
                catch (Exception f)
                {
                    ModPackManagerController.showException(f);
                    return null;
                }
            });
            var string = addModPack.showAndWait();
            if (string.isPresent())
            {
                ModPack modPack = (ModPack) string.get();
                mmp.modpacks.getItems().add(modPack);
                mmp.modpacks.refresh();
            }
        });
        return newModPackButton;
    }
    public static Button getModImportButton(ModPackManager mmp) throws Exception
    {
        //TODO: Add file import button
        throw new ExecutionControl.NotImplementedException("Method getModImportButton not implemented!");
    }
    public static Button getAPIKeyButton(ModPackManager mmp) throws Exception
    {
        //TODO: Add a button to request API key for Nexus
        throw new ExecutionControl.NotImplementedException("Method getAPIKeyButton not implemented!");
    }
}
