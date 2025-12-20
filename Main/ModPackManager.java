package com.lad.mmp.Main;

import com.lad.mmp.JSONManager.JSONManager;
import com.lad.mmp.Misc.Buttons;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import jdk.jshell.spi.ExecutionControl;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


import static com.lad.mmp.Misc.AsyncButtons.*;
import static com.lad.mmp.Misc.Buttons.*;

public class ModPackManager extends Application {
    public TableView<ModPack> modpacks = new TableView<>();
    ObservableList<ModPack> list = FXCollections.observableArrayList();

    public TableView<Mod> mods = new TableView<>();
    String savePath = System.getProperty("user.home") + File.separator + ".modpackmanager" + File.separator + "data" + File.separator + "MMPSaveData.json";
    public CheckBox modBox = new CheckBox();
    public CheckBox modpackBox = new CheckBox();
    public boolean shiftKeyPressed = false;
    final JSONManager jsonManager = new JSONManager();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private Runnable update;
    @Override
    public void start(Stage stage) {
        try {
            update = () ->
            {
                Platform.runLater(() -> mods.refresh());
                Platform.runLater(() -> modpacks.refresh());
                scheduleAsyncTask(update, 500, TimeUnit.MILLISECONDS);
            };
            scheduleAsyncTask(update, 500, TimeUnit.MILLISECONDS);
            jsonManager.start(savePath, this);
            PrintStream log = new PrintStream(new FileOutputStream(
                    System.getProperty("user.home")
                            + File.separator + ".modpackmanager" + File.separator
                            + "data" + File.separator + "MMPLog.log", false));
            System.setOut(log);
            System.setErr(log);

            KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(
                    e -> {
                        shiftKeyPressed = e.isShiftDown();
                        return false;
                    });
            //TODO: Add way to share mods.
            TableColumn<Mod, String> modColumn = new TableColumn<>("Mod Name");
            modColumn.setCellValueFactory(callBack ->
                    callBack.getValue().name);
            modColumn.setPrefWidth(125);
            TableColumn<Mod, String> modVersion = new TableColumn<>("Current version");
            modVersion.setCellValueFactory(callBack ->
                    callBack.getValue().version);
            modVersion.setResizable(true);
            modVersion.setMaxWidth(200);
            modVersion.setMinWidth(50);
            modVersion.setPrefWidth(100);
            TableColumn<Mod, String> modSite = new TableColumn<>();
            modSite.setCellValueFactory(callBack ->
                    callBack.getValue().site);
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
                    callBack.getValue().observableStatus);
            mods.getColumns().addAll(getCheckBoxColumn(), modColumn, modVersion, modSite, modStatus);
            mods.setPlaceholder(new Label("Select a modpack to view mods" + System.lineSeparator() + "Unless the modpack has 0 mods"));
            final double SCROLLBAR_WIDTH = 17;
            double width = SCROLLBAR_WIDTH;
            for (var column : mods.getColumns())
            {
                width += column.getPrefWidth();
            }
            mods.setMaxWidth(width+200);
            mods.setPrefWidth(width);
            mods.setMaxHeight(400);

            TableColumn<ModPack, String> modPackColumn = new TableColumn<>("Mod Pack Name");
            modPackColumn.setCellValueFactory(ModPackManagerController::cellFactory);
            modPackColumn.setPrefWidth(125);
            TableColumn<ModPack, Integer> modPackValueColumn = new TableColumn<>("Size");
            modPackValueColumn.setCellValueFactory(callBack ->
                    callBack.getValue().size.asObject());
            modPackValueColumn.setPrefWidth(50);
            TableColumn<ModPack, String> modPackGameColumn = new TableColumn<>("Game");
            modPackGameColumn.setCellValueFactory(e -> e.getValue().game);
            TableColumn<ModPack, String> modPackVersionColumn = new TableColumn<>("Version");
            modPackVersionColumn.setCellValueFactory(e -> e.getValue().version);
            modpacks.getColumns().addAll(getModPackCheckBoxColumn(), modPackColumn, modPackValueColumn, modPackGameColumn, modPackVersionColumn);
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
                return row;//
            });
            width = SCROLLBAR_WIDTH;
            for (var column : modpacks.getColumns())
            {
                width += column.getPrefWidth();
            }
            modpacks.setMaxWidth(width+200);
            modpacks.setPrefWidth(width);
            modpacks.setMaxHeight(400);

            TextArea text = new TextArea("""
                    INSTRUCTIONS (REFERENCE FOR HELP)
                    
                    Introduction
                    This mod pack manager lets you choose which mods are installed, not installed, and when to update each mod. Please note that some of these tasks take time, and may cost some performance on this application and/or your computer. THIS WAS NOT ENCOURAGED OR MADE BY ANYONE BUT MYSELF, "LawAbidingDeveloper."
                    
                    Each Button's purpose
                    
                    """);//TODO: Add pages for text plane instructions
            text.setWrapText(true);
            text.setEditable(false);
            double textWidth = 330;
            text.setMaxWidth(textWidth);
            text.setMaxHeight(1000000000);
            text.setPrefHeight(1000000000);
            text.setStyle("-fx-font-size: 18px;");
            ScrollPane pane = new ScrollPane(text);
            double additionalPaneWidth = 17.5;
            pane.setMaxWidth(textWidth + additionalPaneWidth);
            pane.setPrefWidth(textWidth + additionalPaneWidth);
            pane.setMaxHeight(400);

            HBox tableBox = new HBox(10, modpacks, mods, pane);
            HBox buttonBox = new HBox(10.00, getModPackButton(this),
                    getDeleteButton(this), getModButton(this), getModDeleteButton(this),
                    getDownloadButton(this), getModInstallButton(this));
            Label topText = new Label("""
                    Welcome to Mod Pack Manager v0.5.0!
                    This is a mod manager where nothing happens without your explicit permission.
                    Please note that your 'explicit permission' is given by every click and button press""");
            HBox textBox = new HBox(10, topText);
            textBox.setStyle("-fx-font-size: 26px;");
            VBox root = new VBox(10, textBox, tableBox, buttonBox);
            Scene scene = new Scene(root, 1200, 550);
            stage.setTitle("Mod Pack Manager");
            stage.setScene(scene);
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
    @Override
    public void stop() throws Exception {
        jsonManager.stop();
        scheduler.shutdown();
        super.stop();
    }
    public int count = 0;

    private TableColumn<Mod, Boolean> getCheckBoxColumn()
    {
        TableColumn<Mod, Boolean> modsSelected = new TableColumn<>();
        modsSelected.setCellValueFactory(callBack ->
                callBack.getValue().property);
        modsSelected.setCellFactory(ModPackManagerController::checkboxFactory);
        modBox.setOnMouseClicked(mouseEvent ->
        {
            boolean set = modBox.isSelected();
            for (var item : mods.getItems())
            {
                if (item.isDeleted) continue;
                item.property.set(set);
            }
        });
        modsSelected.setGraphic(modBox);
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
    public void modPackDeleteHelper()
    {
        for (var item : new ArrayList<>(modpacks.getItems()))
        {
            if (item.isSelected.get())
            {
                item.isDeleted = true;
                try
                {
                    for (var mod : new ArrayList<>(item.mods))
                        mod.delete(item.mods);
                } catch (Exception ex) {
                    Platform.runLater(() ->ModPackManagerController.showException(ex));
                }
                Platform.runLater(() ->modpacks.getItems().remove(item));
                if (modpacks.getItems().isEmpty()) mods.setItems(null);
                else modpacks.getSelectionModel().select(0);
                mods.refresh();
            }
        }
    }
    public void modDeleteHelper()
    {
        for (var mod : new LinkedList<>(mods.getItems()))
        {
            if (mod.property.get())
            {
                mod.delete(modpacks.getSelectionModel().getSelectedItem().mods);
            }
        }

    }
    public void scheduleAsyncTask(Runnable task)
    {
        scheduler.submit(task);
    }
    public void scheduleAsyncTask(Runnable task, long timeDelay, TimeUnit unit)
    {
        scheduler.schedule(task, timeDelay, unit);
    }
}