package com.lad.mmp;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;

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
    public ModPack(String n, ObservableList<Mod> modList, String mFP, String g, String v, TableView<ModPack> modpacks, CheckBox modpackBox)
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
        isSelected.addListener((ObservableValue<? extends Boolean> obsVal, Boolean oldVal, Boolean newVal) ->
        {
            boolean allChecked = true;
            for (var item : modpacks.getItems())
            {
                if (!item.isSelected.get()) allChecked = false;
            }
            modpackBox.setSelected(allChecked);
        });

    }
}
