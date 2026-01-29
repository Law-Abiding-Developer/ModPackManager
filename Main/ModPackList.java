package com.lad.mmp.Main;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

public class ModPackList extends ObservableListBase<Mod>
{
    public final ObservableList<ModPack> mainList = FXCollections.observableArrayList();
    public ObservableList<Mod> combinedList = FXCollections.observableArrayList();
    @Override
    public Mod get(int index) {
        return combinedList.get(index);
    }
    public boolean add(ModPack e)
    {
        return mainList.add(e);
    }
    public boolean addAll(@NotNull List<ModPack> c)
    {
        return mainList.addAll(c);
    }

    /**
     * Returns the total number of mods in all mod packs. Use numberOfModPacks() to get the number of mod packs.
     * @return The total number of mods in all mod packs
     */
    @Override
    public int size() {
        var total = 0;
        for (var modpack : mainList)
            total += modpack.mods.size();
        return total;
    }
    /**
     * Returns the total number of mods in all selected mod packs.
     * @return The total number of mods in selected mod packs
     */
    public int selectedModPacksSize()
    {
        var size = 0;
        for (var modpack : mainList)
            if (modpack.isSelected.getValue()) size += modpack.mods.size();
        return size;
    }
    /**
     * Returns the number of mod packs in this list.
     * @return The number of mod packs
     */
    public int numberOfModPacks()
    {
        return mainList.size();
    }
}
