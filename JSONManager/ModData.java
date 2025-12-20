package com.lad.mmp.JSONManager;

import com.lad.mmp.Mod;

import java.util.List;

public class ModData {
    public String name;
    public String link;
    public String version;
    public String path;
    public String site;
    public String status;
    public ModData(){}
    public ModData(Mod mod)
    {
        name = mod.name.get();
        link = mod.link;
        site = mod.site.get();
        if (mod.version != null) version = mod.version.get();
        else version = "";
        if (mod.currentFile != null) path = mod.currentFile.getAbsolutePath();
        else path = "";
        status = mod.observableStatus.get();

    }
}
