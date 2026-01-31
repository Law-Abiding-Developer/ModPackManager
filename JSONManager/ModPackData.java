package com.lad.mmp.JSONManager;

import com.lad.mmp.Main.ModPack;

import java.util.LinkedList;
import java.util.List;

public class ModPackData {
    public String name;
    public List<ModData> mods;
    public String modFilePath;
    public String version;
    public String game;
    public boolean isSelected;
    public int duplicateCount;
    public ModPackData() {}
    public ModPackData(ModPack modpack)
    {
        name = modpack.name.getRaw();
        mods = new LinkedList<>();
        for (var mod : modpack.mods)
        {
            mods.add(new ModData(mod));
        }
        modFilePath = modpack.modFilePath.getRaw();
        version = modpack.version.get();
        game = modpack.game.get();
        isSelected = modpack.isSelected.get();
        duplicateCount = modpack.duplicateCount.get();
    }
}
