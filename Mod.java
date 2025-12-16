package com.lad.mmp;


import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableView;

import java.util.List;

public class Mod {
    public SimpleStringProperty name;
    public String link;
    public SimpleStringProperty version;
    public ModFolder currentFile;
    public Status status;
    public ModPackManagerController.Site site;
    /**
     * The indexes of each of the file written locations of each field. Note: Each field should be the start of each index.
     */
    public int[] index;
    SimpleBooleanProperty property;
    boolean isDeleted = false;
    public Mod(String n, String l, ModPackManagerController.Site s, Status stat, TableView<Mod> mods, CheckBox modBox) {
        name = new SimpleStringProperty(n);
        link = l;
        site = s;
        status = stat;
        property = new SimpleBooleanProperty();
        property.addListener((ObservableValue<? extends Boolean> obsVal, Boolean oldVal, Boolean newVal) ->
        {
            boolean allChecked = true;
            for (var item : mods.getItems())
            {
                if (!item.property.get()) allChecked = false;
            }
            modBox.setSelected(allChecked);
        });
    }
    public Mod(String n, String l, ModPackManagerController.Site s, String stat, TableView<Mod> mods, CheckBox modBox) {
        name = new SimpleStringProperty(n);
        link = l;
        site = s;
        if (stat.equals("Update Available!")) status = Status.UPDATEAVAILABLE;
        if (stat.equals("Installed")) status = Status.INSTALLED;
        if (stat.equals("Not installed")) status = Status.NOTINSTALLED;
        if (stat.equals("Downloading")) status = Status.DOWNLOADING;
        property = new SimpleBooleanProperty();
        property.addListener((ObservableValue<? extends Boolean> obsVal, Boolean oldVal, Boolean newVal) ->
        {
            boolean allChecked = true;
            for (var item : mods.getItems())
            {
                if (!item.property.get()) allChecked = false;
            }
            modBox.setSelected(allChecked);
        });
    }
    public SimpleStringProperty parseStatusObservable()
    {
        return new SimpleStringProperty()
        {
            @Override
            public String get()
            {
                if (status == Status.UPDATEAVAILABLE) return "Update Available!";
                else if (status == Status.INSTALLED) return "Installed";
                else if (status == Status.NOTINSTALLED) return "Not installed";
                else if (status == Status.DOWNLOADING) return "Downloading";
                return "";
            }
            @Override
            public void set(String set)
            {
                if (set.equals("Update Available!")) status = Status.UPDATEAVAILABLE;
                else if (set.equals("Installed")) status = Status.INSTALLED;
                else if (set.equals("Not installed")) status = Status.NOTINSTALLED;
                else if (set.equals("Downloading")) status = Status.DOWNLOADING;
                else status = Status.NULL;
            }
        };
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
            if (currentFile == null || !currentFile.deleteFolder())
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
            link = null;
            index = null;
        }
        catch (Exception e)
        {
            ModPackManagerController.showException(e);
        }
    }

    public enum Status
    {
        NULL,
        INSTALLED,
        NOTINSTALLED,
        UPDATEAVAILABLE,
        DOWNLOADING,
    }
}
