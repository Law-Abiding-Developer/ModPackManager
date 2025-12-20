package com.lad.mmp.JSONManager;


import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.lad.mmp.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Task;
import javafx.scene.control.Cell;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableView;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;
import java.util.concurrent.*;

public class JSONManager {
    public final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
    private List<ModPackData> modpacks = new LinkedList<>();
    private ModFolder folder;
    private ModPackManager mmp;
    private ScheduledFuture<?> save;
    public final Runnable saveData = () ->
    {
        try (var fileWriter = new FileWriter(folder))
        {
            gson.toJson(modpacks, fileWriter);
        }
        catch (Exception e)
        {
            Platform.runLater(() -> ModPackManagerController.showException(e));
        }
    };
    public Task<Void> loadData = new Task<>() {
        @Override
        protected Void call() {
            try (var fileReader = new FileReader(folder))
            {
            //noinspection unchecked because it should be that
            var dtoList = (List<ModPackData>) gson.fromJson(fileReader,
                    new TypeToken<List<ModPackData>>() {}.getType());
            if (dtoList != null) for (var item : dtoList)
            {
                List<Mod> mods = new LinkedList<>();
                for (var mod : item.mods)
                {
                    var actualMod = new Mod(mod.name, mod.link, mod.site,
                        mod.status, mmp);
                    if (mod.path != null && !mod.path.isBlank())
                    {
                        var modFolder = new ModFolder(mod.path);
                        if (modFolder.exists()) actualMod.currentFile = modFolder;
                    }
                    mods.add(actualMod);
                }
                var observableMods = FXCollections.observableArrayList(mods);
                Platform.runLater(() -> mmp.modpacks.getItems().add(new ModPack(item.name, observableMods, item.modFilePath, item.game, item.version, mmp)));
            }
            } catch (Exception e) {
                Platform.runLater(() -> ModPackManagerController.showException(e));
            }
            return null;
        }
    };
    /**
     * Creates a new JSON Reader/Writer instance, and starts saving in the background
     * @param filePath - File path for the JSON
     * @param i - MMP's instance.
     */
    public void start(String filePath, ModPackManager i)
    {
        try
        {
            mmp = i;
            folder = new ModFolder(filePath);
            folder.ensureExists();
            service.submit(loadData);
            modpacks = new LinkedList<>();
            mmp.modpacks.getItems().addListener((ListChangeListener<? super ModPack>)
                    listener -> autoSave(saveData));
            for (var item : mmp.modpacks.getItems())
            {
                modpacks.add(new ModPackData(item));
            }
        }
        catch (Exception e)
        {
            Platform.runLater(() -> ModPackManagerController.showException(e, "Failed to start JSONManager"));
        }
    }
    public void autoSave(Runnable task)
    {
        if (save != null && !save.isDone())
            save.cancel(false);
        modpacks = new LinkedList<>();
        for (var item : mmp.modpacks.getItems())
        {
            modpacks.add(new ModPackData(item));
        }
        save = service.schedule(task, 750, TimeUnit.MILLISECONDS);
    }
    public void stop()
    {
        autoSave(saveData);
        service.shutdown();
    }
}
