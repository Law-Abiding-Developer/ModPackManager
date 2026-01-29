package com.lad.mmp.Misc;

import com.lad.mmp.Main.*;
import javafx.collections.*;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import jdk.jshell.spi.ExecutionControl;

public class Buttons {
    public static Button getModButton(ModPackManager mmp)
    {
        Button newModButton = new Button("Add Mod to a Mod Pack");
        newModButton.setOnAction(e ->
        {
                Dialog addMod = new Dialog();
                addMod.setTitle("Add Details");
                addMod.setHeaderText("Fill the boxes below with the mod info");
                ChoiceBox<String> choice = new ChoiceBox<>();
                ChoiceBox<ModPack> modPackChoice = new ChoiceBox<>();
                modPackChoice.setItems(mmp.modpacks.getItems());
                modPackChoice.converterProperty().set(new StringConverter<>()
                {
                    @Override
                    public String toString(ModPack modPack)
                    {
                        if (modPack == null) return "";
                        return modPack.name.get();
                    }
                    @Override
                    public ModPack fromString(String s)
                    {
                        return null;
                    }
                });
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
                        new Label("Site: "), choice,
                        new Label("Mod Pack: "), modPackChoice);
                addMod.getDialogPane().setContent(content);
                addMod.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            var getModPack = new Object() {
                public ModPack modpack = null;
            };
                addMod.setResultConverter(button ->
                {
                    try
                    {
                        if (button == ButtonType.OK)
                        {
                            getModPack.modpack = modPackChoice.getValue();
                            return new Mod(nameField.getText(), linkField.getText(),
                                    choice.getValue(),
                                    SimpleStatusProperty.Status.NOTDOWNLOADED, mmp,modPackChoice.getValue());

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
                    if (getModPack.modpack != null) getModPack.modpack.mods.add(mod);
                    mmp.mods.refresh();
                    mmp.modpacks.refresh();
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
    public static Button getCopyButton(ModPackManager mmp)
    {
        Button copyButton = new Button("Create Duplicate Selected Mod Pack(s)");
        copyButton.setOnAction(e ->
        {
            ModPackManagerController.showException(new ExecutionControl.NotImplementedException("Mod Pack Duplicating not implemented yet!"));
            for (var modpack : mmp.modpacks.getItems())
                if (modpack.isSelected.get())
                {

                }
        });
        return copyButton;
    }
    public static Button getAPIKeyButton(ModPackManager mmp) throws Exception
    {
        //CUT TODO: Add a button to request API key for Nexus. Move to Async later.
        throw new ExecutionControl.NotImplementedException("Method getAPIKeyButton not implemented!");
    }
}
