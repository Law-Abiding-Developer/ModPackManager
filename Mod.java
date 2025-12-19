package com.lad.mmp;


import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.util.List;

public class Mod {
    public SimpleStringProperty name;
    public final String link;
    public SimpleStringProperty version;
    public ModFolder currentFile;
    public SimpleStatusProperty observableStatus = new SimpleStatusProperty();
    public SimpleSiteProperty site = new SimpleSiteProperty();
    public SimpleBooleanProperty property;
    boolean isDeleted = false;
    public Mod(String n, String l, SimpleSiteProperty.Site s, SimpleStatusProperty.Status stat, ModPackManager instance) {
        name = new SimpleStringProperty(n);
        name.addListener((ChangeListener<? super String>)
                (_,__,___) ->
                        instance.jsonManager.autoSave(instance.jsonManager.saveData));
        link = l;
        site.set(s);
        observableStatus.set(stat);
        observableStatus.addListener((ChangeListener<? super String>)
                (_,__,___) ->
                        instance.jsonManager.autoSave(instance.jsonManager.saveData));
        property = new SimpleBooleanProperty();
        property.addListener((ObservableValue<? extends Boolean> obsVal, Boolean oldVal, Boolean newVal) ->
        {
            if (isDeleted) return;
            boolean allChecked = true;
            for (var item : instance.mods.getItems())
            {
                if (!item.property.get()) allChecked = false;
            }
            instance.modBox.setSelected(allChecked);
            instance.jsonManager.autoSave(instance.jsonManager.saveData);
        });
    }
    public Mod(String n, String l, String s, SimpleStatusProperty.Status stat, ModPackManager instance) {
        name = new SimpleStringProperty(n);
        name.addListener((ChangeListener<? super String>)
                (_,__,___) ->
                        instance.jsonManager.autoSave(instance.jsonManager.saveData));
        link = l;
        site.set(s);
        observableStatus.set(stat);
        observableStatus.addListener((ChangeListener<? super String>)
                (_,__,___) ->
                        instance.jsonManager.autoSave(instance.jsonManager.saveData));
        property = new SimpleBooleanProperty();
        property.addListener((ObservableValue<? extends Boolean> obsVal, Boolean oldVal, Boolean newVal) ->
        {
            if (isDeleted) return;
            boolean allChecked = true;
            for (var item : instance.mods.getItems())
            {
                if (!item.property.get()) allChecked = false;
            }
            instance.modBox.setSelected(allChecked);
            instance.jsonManager.autoSave(instance.jsonManager.saveData);
        });
    }
    public Mod(String n, String l, String s, String stat, ModPackManager instance) {
        name = new SimpleStringProperty(n);
        name.addListener((ChangeListener<? super String>)
                (_,__,___) ->
                        instance.jsonManager.autoSave(instance.jsonManager.saveData));
        link = l;
        site.set(s);
        observableStatus.set(stat);
        observableStatus.addListener((ChangeListener<? super String>)
                (_,__,___) ->
                        instance.jsonManager.autoSave(instance.jsonManager.saveData));
        property = new SimpleBooleanProperty();
        property.addListener((ObservableValue<? extends Boolean> obsVal, Boolean oldVal, Boolean newVal) ->
        {
            if (isDeleted) return;
            boolean allChecked = true;
            for (var item : instance.mods.getItems())
            {
                if (!item.property.get()) allChecked = false;
            }
            instance.modBox.setSelected(allChecked);
            instance.jsonManager.autoSave(instance.jsonManager.saveData);
        });
    }

    /**
     * Method to safely delete a mod and stop all of it's tasks
     * @param activeList The list which contains the mod
     * @return false if something throws an exception. true if all was successful
     */
    public void delete(List<Mod> activeList)
    {
        try
        {
            isDeleted = true;
            if (currentFile != null && !currentFile.deleteFolder())
                ModPackManagerController.showError("Mod File Delete Failure",
                    "Failed to delete selected mod, " + name.get() + "'s, file");
            if (currentFile != null)
            {
                currentFile = null;
            }
            if (!activeList.remove(this))
                ModPackManagerController.showError("Mod List Removal Failure", "Mod, " + name + ", does not exist in the list");
            if (name != null)
            {
                name.unbind();
                name = null;
            }
            if (version != null)
            {
                version.unbind();
                version = null;
            }
            if (property != null)
            {
                property.unbind();
                property = null;
            }
        }
        catch (Exception e)
        {
            Platform.runLater(() ->ModPackManagerController.showException(e));
        }
    }

}
