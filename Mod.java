package com.lad.mmp;


import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableValue;
import javafx.css.SimpleStyleableBooleanProperty;

import java.io.File;

public class Mod {
    SimpleStringProperty name;
    String link;
    SimpleStringProperty version;
    File currentFile;
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
        /*{
            @Override
            public boolean get()
            {
                return selected;
            }
            @Override
            public void set(boolean b)
            {
                selected = b;
                super.set(b);
            }
        }*/;
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

    public void delete()
    {
        isDeleted = true;
        //TODO: Add a way to safely delete the mod
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
