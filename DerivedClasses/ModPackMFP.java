package com.lad.mmp.DerivedClasses;

import javafx.beans.property.SimpleStringProperty;

import java.io.File;

//ModPackModFolderPath
public class ModPackMFP extends SimpleStringProperty {
    private final ModPackName name;
    public ModPackMFP(String basePath, ModPackName name) {
        super(basePath);
        this.name = name;
    }
    @Override
    public String get()
    {
        return super.get() + File.separator + ".modpackmanager" + File.separator + name.get();
    }
    public String getRaw()
    {
        return super.get();
    }
}
