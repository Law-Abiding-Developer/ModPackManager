package com.lad.mmp;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;

import java.io.FileWriter;

public class ModPack {//TODO: add import and export button
    SimpleStringProperty name;
    ObservableList<Mod> mods;
    IntegerProperty size;
    SimpleStringProperty modFilePath;
    SimpleStringProperty version;
    SimpleStringProperty game;
    boolean isDeleted = false;
    SimpleBooleanProperty isSelected;
    FileWriter saveDataWriter;
    public ModPack(String n, ObservableList<Mod> modList, String mFP, String g, String v)
    {
        name = new SimpleStringProperty(n);
        mods = modList;
        size = new SimpleIntegerProperty()
        {
            @Override
            public int get()
            {
                return mods.size();
            }
            @Override
            public void set(int i)
            {

            }
        };
        game = new SimpleStringProperty(g);
        version = new SimpleStringProperty(v);
        modFilePath = new SimpleStringProperty(mFP);
        isSelected = new SimpleBooleanProperty(false);
    }
}
