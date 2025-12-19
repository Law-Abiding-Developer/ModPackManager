package com.lad.mmp;

import javafx.beans.property.SimpleStringProperty;

public class SimpleStatusProperty extends SimpleStringProperty
{
    private Status status = Status.NULL;
    @Override
    public String get()
    {
        if (status == Status.UPDATEAVAILABLE) return "Update Available!";
        else if (status == Status.INSTALLED) return "Installed";
        else if (status == Status.NOTINSTALLED) return "Not installed";
        else if (status == Status.DOWNLOADING) return "Downloading";
        return "";
    }
    public Status getStatus()
    {
        return status;
    }
    @Override
    public void set(String set)
    {
        if (set.equals("Update Available!")) status = Status.UPDATEAVAILABLE;
        else if (set.equals("Installed")) status = Status.INSTALLED;
        else if (set.equals("Not installed")) status = Status.NOTINSTALLED;
        else if (set.equals("Downloading")) status = Status.DOWNLOADING;
        else status = Status.NULL;
        super.set(set);
    }
    public void set(Status stat)
    {
        status = stat;
        super.set(get());
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
