package com.lad.mmp;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.net.URI;

public class ModFolder extends File {
    public ModFolder(@NotNull String pathname) {
        super(pathname);
    }

    public ModFolder(String parent, @NotNull String child) {
        super(parent, child);
    }

    public ModFolder(File parent, @NotNull String child) {
        super(parent, child);
    }

    public ModFolder(@NotNull URI uri) {
        super(uri);
    }
    public boolean deleteFolder()
    {
        var files = listFiles();
        boolean failed = false;
        if (files != null)
            for (var item : files)
            {
                if (item.isDirectory()) failed = !item.deleteFolder();
                else failed = !item.delete();
            }
        return !failed && delete();
    }
    @Override
    public ModFolder[] listFiles()
    {
        var items = super.listFiles();
        if (items != null)
        {
            ModFolder[] modFolder = new ModFolder[items.length];
            int i = 0;
            for (var item : items)
            {
                modFolder[i] = new ModFolder(this, item.getPath());
                i++;
            }
            return modFolder;
        }
        return null;
    }
}
