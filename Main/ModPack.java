package com.lad.mmp.Main;

import com.lad.mmp.Misc.*;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

public class ModPack {//TODO: add import and export button
    public ModPackName name = new ModPackName();
    public ObservableList<Mod> mods;
    public IntegerProperty size;
    public SimpleStringProperty modFilePath;
    public SimpleStringProperty version;
    public SimpleStringProperty game;
    public boolean isDeleted = false;
    public SimpleBooleanProperty isSelected;
    public SimpleIntegerProperty duplicateCount = new SimpleIntegerProperty(0);
    public ModPack(String n, ObservableList<Mod> modList, String mFP, String g, String v, ModPackManager instance)
    {
        name.set(n);
        name.addListener((ChangeListener<? super String>)
                (_,__,___) ->
                        instance.jsonManager.autoSave(instance.jsonManager.saveData));
        mods = modList;
        mods.addListener((ListChangeListener<? super Mod>)_ ->
                instance.jsonManager.autoSave(instance.jsonManager.saveData));
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
        game.addListener((ChangeListener<? super String>)
                (_,__,___) ->
                        instance.jsonManager.autoSave(instance.jsonManager.saveData));
        version = new SimpleStringProperty(v);
        version.addListener((ChangeListener<? super String>)
                (_,__,___) ->
                        instance.jsonManager.autoSave(instance.jsonManager.saveData));
        modFilePath = new SimpleStringProperty(mFP);
        modFilePath.addListener((ChangeListener<? super String>)
                (_,__,___) ->
                        instance.jsonManager.autoSave(instance.jsonManager.saveData));
        isSelected = new SimpleBooleanProperty(false);
        isSelected.addListener((ObservableValue<? extends Boolean> obsVal, Boolean oldVal, Boolean newVal) ->
        {
            if (newVal) instance.modList.combinedList.addAll(mods);
            else instance.modList.combinedList.removeAll(mods);
            boolean allChecked = true;
            for (var item : instance.modpacks.getItems())
            {
                if (!item.isSelected.get()) allChecked = false;
            }
            instance.modpackBox.setSelected(allChecked);
            instance.jsonManager.autoSave(instance.jsonManager.saveData);
        });
    }
    public ModPack(String n, ObservableList<Mod> modList, String mFP, String g, String v, ModPackManager instance, int dupes)
    {
        name.set(n);
        name.addListener((ChangeListener<? super String>)
                (_,__,___) ->
                        instance.jsonManager.autoSave(instance.jsonManager.saveData));
        mods = modList;
        for (var mod : mods)
        {
            mod.parentModPack = this;
        }
        mods.addListener((ListChangeListener<? super Mod>)_ ->
                instance.jsonManager.autoSave(instance.jsonManager.saveData));
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
        game.addListener((ChangeListener<? super String>)
                (_,__,___) ->
                        instance.jsonManager.autoSave(instance.jsonManager.saveData));
        version = new SimpleStringProperty(v);
        version.addListener((ChangeListener<? super String>)
                (_,__,___) ->
                        instance.jsonManager.autoSave(instance.jsonManager.saveData));
        modFilePath = new SimpleStringProperty(mFP);
        modFilePath.addListener((ChangeListener<? super String>)
                (_,__,___) ->
                        instance.jsonManager.autoSave(instance.jsonManager.saveData));
        isSelected = new SimpleBooleanProperty(false);
        isSelected.addListener((ObservableValue<? extends Boolean> obsVal, Boolean oldVal, Boolean newVal) ->
        {
            if (newVal) instance.modList.combinedList.addAll(mods);
            else instance.modList.combinedList.removeAll(mods);
            boolean allChecked = true;
            for (var item : instance.modpacks.getItems())
            {
                if (!item.isSelected.get()) allChecked = false;
            }
            instance.modpackBox.setSelected(allChecked);
            instance.jsonManager.autoSave(instance.jsonManager.saveData);
        });
        name.duplicateCount = duplicateCount = new SimpleIntegerProperty(dupes);
    }
}
