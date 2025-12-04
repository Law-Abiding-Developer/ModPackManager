package com.lad.mmp;


import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import java.util.List;

public class Mod {
    SimpleStringProperty name;
    String link;
    SimpleStringProperty version;
    ModFolder currentFile;
    Status status;
    ModPackManagerController.Site site;
    int index;
    SimpleBooleanProperty property;
    boolean isDeleted = false;
    public Mod(String n, String l, int i, ModPackManagerController.Site s, Status stat) {
        name = new SimpleStringProperty(n);
        link = l;
        index = i;
        site = s;
        status = stat;
        property = new SimpleBooleanProperty();
    }
    public Mod(String n, String l, int i, ModPackManagerController.Site s, String stat) {
        name = new SimpleStringProperty(n);
        link = l;
        index = i;
        site = s;
        if (stat.equals("Update Available!")) status = Status.UPDATEAVAILABLE;
        if (stat.equals("Installed")) status = Status.INSTALLED;
        if (stat.equals("Not installed")) status = Status.NOTINSTALLED;
        if (stat.equals("Downloading")) status = Status.DOWNLOADING;
        property = new SimpleBooleanProperty();
    }
    protected SimpleStringProperty parseStatusObservable()
    {
        SimpleStringProperty string;
        if (status == Mod.Status.UPDATEAVAILABLE) string = new SimpleStringProperty("Update Available!");
        else if (status == Mod.Status.INSTALLED) string = new SimpleStringProperty("Installed");
        else if (status == Mod.Status.NOTINSTALLED) string = new SimpleStringProperty("Not installed");
        else if (status == Status.DOWNLOADING) string = new SimpleStringProperty("Downloading");
        else string = new SimpleStringProperty("null");
        return string;
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
            index = -1;
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
        DOWNLOADING
    }
}
