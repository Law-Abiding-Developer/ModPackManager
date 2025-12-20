package com.lad.mmp.Misc;

import javafx.beans.property.SimpleStringProperty;

public class SimpleStatusProperty extends SimpleStringProperty
{
    private Status status = Status.NOTDOWNLOADED;
    @Override
    public String get()
    {
        if (status == Status.UPDATEAVAILABLE) return "Update Available!";
        else if (status == Status.INSTALLED) return "Installed";
        else if (status == Status.NOTINSTALLED) return "Not installed";
        else if (status == Status.DOWNLOADING) return "Downloading";
        else if (status == Status.NOTDOWNLOADED) return "Not Downloaded";
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
        else if (set.equals("Not Downloaded")) status = Status.NOTDOWNLOADED;
        super.set(set);
    }
    public void set(Status stat)
    {
        status = stat;
        super.set(get());
    }

    public enum Status
    {
        NOTDOWNLOADED,
        INSTALLED,
        NOTINSTALLED,
        UPDATEAVAILABLE,
        DOWNLOADING
    }
}
