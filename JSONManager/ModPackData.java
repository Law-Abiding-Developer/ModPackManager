package com.lad.mmp.JSONManager;

import com.lad.mmp.Mod;
import com.lad.mmp.ModPack;

import java.util.LinkedList;
import java.util.List;

public class ModPackData {
    public String name;
    public List<ModData> mods;
    public String modFilePath;
    public String version;
    public String game;
    public boolean isSelected;
    public ModPackData() {}
    public ModPackData(ModPack modpack)
    {
        name = modpack.name.get();
        mods = new LinkedList<>();
        for (var mod : modpack.mods)
        {
            mods.add(new ModData(mod));
        }
        modFilePath = modpack.modFilePath.get();
        version = modpack.version.get();
        game = modpack.game.get();
        isSelected = modpack.isSelected.get();
    }
}
