package com.lad.mmp;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
public class ModPackManager extends Application {
    TableView<ModPack> modpacks = new TableView<>();
    ObservableList<ModPack> list = FXCollections.observableArrayList();

    TableView<Mod> mods = new TableView<>();
    String baseSavePath = System.getProperty("user.home");
    File saveData = new File(baseSavePath + File.separator + ".modpackmanager" + File.separator + "data" + File.separator + "MMPSaveData.txt");
    FileWriter saveDataWrite;
    CheckBox modBox = new CheckBox();
    CheckBox modpackBox = new CheckBox();
    boolean shiftKeyPressed = false;

    @Override
    public void start(Stage stage) {
        try {
            KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(
                    e -> {
                        shiftKeyPressed = e.isShiftDown();
                        return false;
                    });
            var parentDir = saveData.getParentFile();
            if (parentDir != null && !parentDir.exists())
                if (!parentDir.mkdirs())
                    throw new RuntimeException("Could not create the parent file for save data");
            Task<Void> task = new Task<>() {//TODO: Add way to share mods.
                @Override
                protected Void call() throws Exception {
                    saveDataWrite = new FileWriter(saveData, true);
                    if (!saveData.exists() || saveData.length() == 0) return null;
                    var string = Files.readString(saveData.toPath());
                    var subStrings = string.split("ModPack: ");
                    for (var item : subStrings)
                    {
                        if (item.isBlank()) continue;
                        var parts = item.split(", ");
                        if (parts.length < 5) continue;
                        var name = parts[0].trim();
                        var modFilePath = parts[1].trim();
                        var version = parts[2].trim();
                        var game = parts[3].trim();
                        var modPack = new ModPack(name, FXCollections.observableArrayList(),
                                modFilePath, version, game);
                        String mods = parts[4].replace("Mods: ", "").trim();
                        modPack.saveDataWriter = new FileWriter(mods, true);
                        var modsFile = new File(mods);
                        if (!modsFile.exists() || modsFile.length() == 0)
                        {
                            Platform.runLater(() -> modpacks.getItems().add(modPack));
                            continue;
                        }
                        var modSubStrings = Files.readString(modsFile.toPath());
                        for (var modItem : modSubStrings.split("Mod: ")) {
                            if (modItem.isBlank()) continue;
                            var modParts = modItem.split(", ");
                            if (modParts.length < 5) continue;
                            var modName = modParts[0].trim();
                            var modLink = modParts[1].trim();
                            var modIndex = modParts[2].trim();
                            var modSite = modParts[3].trim();
                            var site = ModPackManagerController.ParseFromString(modSite);
                            var modStatus = modParts[4].trim();
                            var mod = new Mod(modName, modLink, Integer.parseInt(modIndex), site, modStatus);
                            String modsFilePath = "";
                            if (modParts.length > 5) modsFilePath = modParts[5];
                            mod.currentFile = new ModFolder(modsFilePath);
                            modPack.mods.add(mod);
                        }
                        Platform.runLater(() -> modpacks.getItems().add(modPack));
                    }
                    return null;
                }
            };
            task.setOnFailed(e ->
            {
                try
                {
                    throw e.getSource().getException();
                }
                catch (Throwable ex)
                {
                    Platform.runLater(() ->
                    {
                    ModPackManagerController.showException(ex);});
                }
            });
            var thread = new Thread(task);
            thread.start();

            TableColumn<Mod, String> modColumn = new TableColumn<>("Mod Name");
            modColumn.setCellValueFactory(callBack ->
                    callBack.getValue().name);
            TableColumn<Mod, String> modVersion = new TableColumn<>("Current version");
            modVersion.setCellValueFactory(callBack ->
                    callBack.getValue().version);
            modVersion.setResizable(true);
            modVersion.setMaxWidth(200);
            modVersion.setMinWidth(50);
            modVersion.setPrefWidth(100);
            TableColumn<Mod, String> modSite = new TableColumn<>();
            modSite.setCellValueFactory(callBack ->
                    new SimpleStringProperty(ModPackManagerController.convertToString(callBack.getValue().site)));
            Label header = new Label("Site");
            header.setTooltip(new Tooltip("Click to copy link"));
            modSite.setGraphic(header);
            modSite.setCellFactory(column ->
            {
                TableCell<Mod, String> cell = new TableCell<>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                            setTooltip(null);
                        } else {
                            setText(item);
                            var tooltip = new Tooltip("Click to copy link");
                            tooltip.setShowDelay(new Duration(0.1));
                            setTooltip(tooltip);
                        }
                    }
                };
                cell.setOnMouseClicked(mouseEvent ->
                        {
                            Clipboard clipboard = Clipboard.getSystemClipboard();
                            ClipboardContent content = new ClipboardContent();
                            Mod mod = cell.getTableView().getItems().get(cell.getIndex());
                            String string = mod.link;
                            content.putString(string);
                            clipboard.setContent(content);
                        }
                );
                return cell;
            });
            TableColumn<Mod, String> modStatus = new TableColumn<>("Status");
            modStatus.setCellValueFactory(callBack ->
            {
                SimpleStringProperty string;
                string = callBack.getValue().parseStatusObservable();
                return string;
            });
            mods.getColumns().addAll(getCheckBoxColumn(), modColumn, modVersion, modSite, modStatus);
            mods.setPlaceholder(new Label("Select a modpack to view mods"));
            mods.setMaxWidth(670);
            mods.setMaxHeight(400);

            TableColumn<ModPack, String> modPackColumn = new TableColumn<>("Mod Pack");
            modPackColumn.setCellValueFactory(ModPackManagerController::cellFactory);
            TableColumn<ModPack, Integer> modPackValueColumn = new TableColumn<>("Size");
            modPackValueColumn.setCellValueFactory(callBack ->
                    callBack.getValue().size.asObject());
            modPackValueColumn.setPrefWidth(50);
            TableColumn<ModPack, String> modPackGameColumn = new TableColumn<>("Game");
            modPackGameColumn.setCellValueFactory(e -> e.getValue().game);
            TableColumn<ModPack, String> modPackVersionColumn = new TableColumn<>("Version");
            modPackVersionColumn.setCellValueFactory(e -> e.getValue().version);
            modpacks.getColumns().addAll(modPackColumn, modPackValueColumn, modPackGameColumn, modPackVersionColumn);
            modpacks.setItems(list);
            modpacks.setRowFactory(tv -> {
                TableRow<ModPack> row = new TableRow<>();
                row.setOnMouseClicked(event -> {
                    if (!row.isEmpty()) {
                        ModPack selectedItem = row.getItem();
                        mods.setItems(selectedItem.mods);
                        mods.refresh();
                    }
                });
                return row;
            });
            modpacks.setMaxWidth(592.5);
            modpacks.setMaxHeight(400);

            TextArea text = new TextArea("""
                    Unknown Data
                    """);
            text.setWrapText(true);
            text.setEditable(false);
            text.setMaxWidth(400);
            text.setMaxHeight(1000000000);
            text.setPrefHeight(1000000000);
            ScrollPane pane = new ScrollPane(text);
            pane.setMaxWidth(417.5);
            pane.setPrefWidth(417.5);
            pane.setMaxHeight(400);

            HBox tableBox = new HBox(10, modpacks, mods, pane);
            HBox buttonBox = new HBox(10.00, getModPackButton(), getDeleteButton(), getModButton(), getDownloadButton(),getModDeleteButton());
            Label topText = new Label("""
                    Welcome to Mod Pack Manager v0.5.0!
                    This is a mod manager where nothing happens without your explicit permission.
                    Please note that your 'explicit permission' is given by every click and button press""");
            HBox textBox = new HBox(10, topText);
            textBox.setStyle("-fx-font-size: 26px;");
            VBox root = new VBox(10, textBox, tableBox, buttonBox);
            Scene scene = new Scene(root, 1110, 550);
            stage.setTitle("Mod Pack Manager");
            stage.setScene(scene);
            var close = stage.getOnCloseRequest();
            stage.show();
        } catch (Exception e) {
            String message = "";
            for (var i : e.getStackTrace())
            {
                message += "at " + i + System.lineSeparator();
            }
            ModPackManagerController.showError("Error", e.getClass() + " " + System.lineSeparator() + message);
        }
    }

    private Button getModButton()
    {
        Button newModButton = new Button("Add Mod to Selected Mod Pack");
        newModButton.setOnAction(e ->
        {
            ModPack selected = modpacks.getSelectionModel().getSelectedItem();
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
                            Mod mod = new Mod(nameField.getText(), linkField.getText(), selected.mods.size(),
                                    ModPackManagerController.ParseFromString(choice.getValue()),
                                    Mod.Status.NOTINSTALLED);
                            mod.property.addListener(this::modListener);
                            var modsFileWriter = selected.saveDataWriter;
                            modsFileWriter.write("Mod: " + mod.name.get() + ", "
                                    + mod.link + ", " + mod.index + ", "
                                    + ModPackManagerController.convertToString(mod.site) + ", "
                                    + mod.parseStatusObservable().get() + ", ");
                            if (mod.currentFile != null)
                                modsFileWriter.write(mod.currentFile.getAbsolutePath()
                                        + System.lineSeparator());
                            else modsFileWriter.write(System.lineSeparator());
                            modsFileWriter.flush();
                            return mod;
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
                    mods.setItems(selected.mods);
                    mods.refresh();
                    modpacks.refresh();
                }
            }
        });
        return newModButton;
    }

    private Button getModPackButton()
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
                    new ChoiceBox<>(FXCollections.observableArrayList("Legacy", "March 2023", "2025", ""));
            versionChoice.setValue("");
            TextField name = new TextField();
            name.setPromptText("Name");
            TextField gamePath = new TextField();
            gamePath.setPromptText("Game Folder Path");
            VBox content = new VBox(10, new Label("Name: "), name, new Label("Game Folder Path: "),
                    gamePath, new Label("Game: "), gameChoice, new Label("Subnautica Version: "), versionChoice);
            addModPack.getDialogPane().setContent(content);
            addModPack.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            addModPack.setResultConverter(object ->
            {
                try
                {
                    if (object == ButtonType.OK)
                    {
                        var modPack = new ModPack(name.getText(), FXCollections.observableArrayList(),
                                gamePath.getText(), gameChoice.getValue(), versionChoice.getValue());
                        saveDataWrite.write("ModPack: " + modPack.name.get() + ", "
                                + modPack.modFilePath.get() + ", " + modPack.version.get()
                                + ", " + modPack.game.get() + ", Mods: " + saveData.getParentFile().getAbsolutePath() + "/" + modPack.name.get()
                                + "SaveData.txt" + System.lineSeparator());
                        saveDataWrite.flush();
                        modPack.saveDataWriter = new FileWriter(saveData.getParentFile().getAbsolutePath() + "/" + modPack.name.get() + "SaveData.ext", true);
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
                modpacks.getItems().add(modPack);
                modpacks.refresh();
            }
        });
        return newModPackButton;
    }
    public int count = 0;
    private Button getDownloadButton()
    {
        Button downloadButton = new Button("Download Selected Mod(s)");
        downloadButton.setOnAction(e ->
        {
            Dialog<Void> progress = new Dialog<>();
            count = 0;
            for (var item : mods.getItems())
            {
                if (!item.property.get()) continue;
                count++;
            }
            progress.setTitle("Downloading " + count + " mods...");
            progress.setHeaderText("Downloading " + count + " mods. You can keep"
                    + System.lineSeparator() + "working while this is going.");
            ProgressBar bar = new ProgressBar(0);
            progress.getDialogPane().setContent(bar);
            progress.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
            Task<File> task = new Task<>() {
                @Override
                protected File call() throws Exception {
                    var modPack = modpacks.getSelectionModel().getSelectedItem();
                    int maxProgress = (count * 13) + 7;
                    int pogress = 0;
                    updateProgress(pogress, maxProgress);
                    for (var mod : mods.getItems())
                    {
                        //TODO: code backend for downloading site
                        if (!mod.property.get()) continue;
                        if (modPack.isDeleted) cancel(true);
                        updateProgress(pogress++, maxProgress);
                        mod.status = Mod.Status.DOWNLOADING;
                        if (modPack.isDeleted) cancel(true);
                        updateProgress(pogress++, maxProgress);
                        if (mod.site == ModPackManagerController.Site.REIKA && modPack.version.get().equals("Legacy"))
                        {
                            if (modPack.isDeleted) cancel(true);
                            updateProgress(pogress++, maxProgress);
                            HttpClient client = HttpClient.newHttpClient();
                            if (modPack.isDeleted) cancel(true);
                            updateProgress(pogress++, maxProgress);
                            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(mod.link)).GET().build();
                            if (modPack.isDeleted) cancel(true);
                            updateProgress(pogress++, maxProgress);
                            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                            if (modPack.isDeleted) cancel(true);
                            updateProgress(pogress++, maxProgress);
                            String html = response.body();
                            if (modPack.isDeleted) cancel(true);
                            updateProgress(pogress++, maxProgress);
                            int startIndex = html.indexOf("<h3>Downloads</h3>") + 44;
                            if (modPack.isDeleted) cancel(true);
                            updateProgress(pogress++, maxProgress);
                            int endIndex = html.indexOf("Via GitHub Releases") - 8;
                            if (modPack.isDeleted) cancel(true);
                            updateProgress(pogress++, maxProgress);
                            String fileLink = html.substring(startIndex,endIndex);
                            if (modPack.isDeleted) cancel(true);
                            updateProgress(pogress++, maxProgress);
                            request = HttpRequest.newBuilder().uri(URI.create(fileLink)).build();
                            if (modPack.isDeleted) cancel(true);
                            updateProgress(pogress++, maxProgress);
                            HttpResponse<Path> downloadedFile = client.send(request,
                                    HttpResponse.BodyHandlers.ofFile(Path.of(URI.create(modPack.modFilePath.get()))));
                            if (modPack.isDeleted) cancel(true);
                            updateProgress(pogress++ + 7,maxProgress);
                        }
                        if (mod.site == ModPackManagerController.Site.NEXUSMODS)
                        {
                            //TODO: Code Nexus Mods API Access back end
                        }
                        mod.status = Mod.Status.NOTINSTALLED;
                        updateProgress(pogress++, maxProgress);
                    }
                    return null;
                }
            };
            bar.progressProperty().bind(task.progressProperty());
            progress.setResultConverter(f->
            {
                if (f == ButtonType.CANCEL)
                {
                    task.cancel();
                }
                return null;
            });
            task.setOnSucceeded(g ->
            {
                progress.close();
            });
            task.setOnFailed(event -> {
                progress.close();
                String message = "";
                for (var i : event.getSource().getException().getStackTrace())
                {
                    message += "at " + i + System.lineSeparator();
                }
                ModPackManagerController.showError("Error", e.getClass() + " " + System.lineSeparator() + message);
            });
            task.setOnCancelled(f -> progress.close());
            progress.initModality(Modality.NONE);
            progress.show();
            Thread thread = new Thread(task);
            thread.start();
        });
        return downloadButton;
    }

    private TableColumn<Mod, Boolean> getCheckBoxColumn()
    {
        TableColumn<Mod, Boolean> modsSelected = new TableColumn<>();
        modsSelected.setCellValueFactory(callBack ->
                callBack.getValue().property);
        modsSelected.setCellFactory(ModPackManagerController::checkboxFactory);
        box.setOnMouseClicked(mouseEvent ->
        {
            boolean set = box.isSelected();
            for (var item : mods.getItems())
            {
                item.property.set(set);
            }
        });
        modsSelected.setGraphic(box);
        modsSelected.setPrefWidth(30);
        return modsSelected;
    }
    private TableColumn<ModPack, Boolean> getModPackCheckBoxColumn()
    {
        TableColumn<ModPack, Boolean> modPacks = new TableColumn<>();
        modPacks.setCellValueFactory(callBack ->
                callBack.getValue().isSelected);
        modPacks.setCellFactory(column ->
        {
            var cell = CheckBoxTableCell.forTableColumn(column).call(column);
            cell.setOnMouseClicked(event ->
            {
                var modpack = cell.getTableView().getItems().get(cell.getIndex());
                modpack.isSelected.set(!modpack.isSelected.get());
            });
            return cell;
        });
        modpackBox.setOnMouseClicked(event ->
        {
            boolean set = modpackBox.isSelected();
            for (var item : modpacks.getItems())
            {
                item.isSelected.set(set);
            }
        });
        modPacks.setGraphic(modpackBox);
        modPacks.setPrefWidth(30);
        return modPacks;
    }
    private Button getDeleteButton()
    {
        Button deleteButton = new Button("Delete Selected Mod Pack");
        deleteButton.setOnAction(e ->
        {
            if (shiftKeyPressed)
            {
                for (var item : modpacks.getItems())
                {
                    if (item.isSelected.get())
                    {
                        item.isDeleted = true;
                        try {
                            var toDelete = new File(saveData.getParentFile().getAbsolutePath() + "/" + item.name.get() + "SaveData.txt");
                            if (!toDelete.delete()) throw new RuntimeException("Failed to delete save data file for modpack");
                            item.saveDataWriter.close();
                        } catch (Exception ex) {
                            ModPackManagerController.showException(ex);
                        }
                        modpacks.getItems().remove(item);
                        mods.setItems(null);
                        mods.refresh();
                    }
                }
            }
            else
            {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirm Deletion");
                alert.setHeaderText("Delete Selected Mod Pack(s)?");
                alert.setContentText("Are you sure you want to delete this(these) Mod Pack(s)? THIS WILL DELETE ANY AND ALL DATA THE MOD PACK HAS, INCLUDING MODS IN THE MODPACK (except mod save data or options)");
                var type = alert.showAndWait();
                type.ifPresent(response ->
                {
                    if (response == ButtonType.OK)
                        for (var item : modpacks.getItems())
                        {
                            if (item.isSelected.get())
                            {
                                item.isDeleted = true;
                                try {
                                    var toDelete = new File(saveData.getParentFile().getAbsolutePath() + "/" + item.name.get() + "SaveData.txt");
                                    if (!toDelete.delete()) throw new RuntimeException("Failed to delete save data file for modpack");
                                    item.saveDataWriter.close();
                                } catch (Exception ex) {
                                    ModPackManagerController.showException(ex);
                                }
                                modpacks.getItems().remove(item);
                                mods.setItems(null);
                                mods.refresh();
                            }
                        }
                });
            }
        });
        return deleteButton;
    }
    protected void modListener(ObservableValue<? extends Boolean> obsVal, Boolean oldVal, Boolean newVal)
    {
        boolean allChecked = true;
        for (var item : mods.getItems())
        {
            if (!item.property.get()) allChecked = false;
        }
        box.setSelected(allChecked);
    }
    protected void getAPIKeyButton()
    {
        //TODO: Add a button to request API key for Nexus
    }
    protected Button getModDeleteButton()
    {
        Button modDeleteButton = new Button("Delete Selected Mod(s)");
        modDeleteButton.setOnAction(e ->
        {
            if (shiftKeyPressed)
                for (var modItem : mods.getItems())
                    if (modItem.property.get())
                        modItem.delete(mods.getItems());
            else
            {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirm Deletion");
                alert.setHeaderText("Delete Selected Mod(s)?");
                alert.setContentText("Are you sure you want to delete this(these) Mod(s)? THIS WILL DELETE ANY AND ALL DATA THE MOD HAS (except mod save data or options)");
                var item = alert.showAndWait();
                item.ifPresent(response ->{
                    if (response == ButtonType.OK)
                        for (var mod : mods.getItems())
                            if (mod.property.get())
                                mod.delete(mods.getItems());
            });

            }
        });
        return modDeleteButton;
    }
    protected void getModImportButton()
    {
        //TODO: Add file import button
    }
}