package com.lad.mmp.DerivedClasses;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class ModPackName extends SimpleStringProperty {
    public SimpleIntegerProperty duplicateCount = new SimpleIntegerProperty(0);
    @Override
    public String get()
    {
        var orig = super.get();
        if (duplicateCount.get() > 0) return orig + " (" + duplicateCount.get() + ")";
        return orig;
    }
    public String getRaw()
    {
        return super.get();
    }
}
