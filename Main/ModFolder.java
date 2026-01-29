package com.lad.mmp.Main;

import javafx.application.Platform;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Objects;

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
        try
        {
            if (!exists()) return true;
            if (!isDirectory()) return delete();
            Files.walkFileTree(toPath(), new SimpleFileVisitor<>()
            {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
                {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException
                {
                    if (exc != null) throw exc;
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        }
        catch (Exception e)
        {
            Platform.runLater(()->ModPackManagerController.showException(e));
        }
        return false;
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
    public void ensureExists() throws Exception
    {
            if (!exists() && (!createNewFile() || (!getParentFile().exists() && !getParentFile().mkdirs())))
            {
                throw new RuntimeException("Failed to create parent directories for mod folder");
            }
    }
}
