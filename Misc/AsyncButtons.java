package com.lad.mmp.Misc;

import com.lad.mmp.DerivedClasses.SimpleSiteProperty;
import com.lad.mmp.DerivedClasses.SimpleStatusProperty;
import com.lad.mmp.Main.Mod;
import com.lad.mmp.Main.ModFolder;
import com.lad.mmp.Main.ModPackManager;
import com.lad.mmp.Main.ModPackManagerController;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.*;
import javafx.stage.Modality;
import jdk.jshell.spi.ExecutionControl;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class AsyncButtons {
    public static Button getDownloadButton(ModPackManager mmp)
    {
        Button downloadButton = new Button("Download Selected Mod(s)");
        downloadButton.setOnAction(e ->
        {
            Dialog<Void> progress = new Dialog<>();
            mmp.count = 0;
            for (var item : mmp.mods.getItems())
            {
                if (!item.property.get()) continue;
                mmp.count++;
            }
            progress.setTitle("Downloading " + mmp.count + " mods...");
            progress.setHeaderText("Downloading " + mmp.count + " mods. You can keep"
                    + System.lineSeparator() + "working while this is going.");
            ProgressBar bar = new ProgressBar(0);
            progress.getDialogPane().setContent(bar);
            progress.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
            Task<Void> task = new Task<>() {
                // Replace call-count throttling with time-based throttling (250ms).
                private volatile long lastProgressUpdate = 0;
                private static final long PROGRESS_THROTTLE_MS = 250;

                @Override
                protected void updateProgress(long workDone, long max) {
                    long now = System.currentTimeMillis();
                    // always flush immediately at completion
                    if (max > 0 && workDone >= max) {
                        super.updateProgress(workDone, max);
                        lastProgressUpdate = now;
                        return;
                    }
                    if (now - lastProgressUpdate >= PROGRESS_THROTTLE_MS) {
                        super.updateProgress(workDone, max);
                        lastProgressUpdate = now;
                    }
                }

                private void updateProgress(long workDone, long max, long bytesDownloaded, Mod mod)
                {
                    long now = System.currentTimeMillis();
                    // always flush immediately at completion
                    if (max > 0 && workDone >= max) {
                        super.updateProgress(workDone, max);
                        lastProgressUpdate = now;
                        Platform.runLater(() -> progress.setHeaderText("Downloading " + mmp.count + " mods. You can keep"
                                + System.lineSeparator() + "working while this is going."
                                + System.lineSeparator() + "Downloading " + mod.name.get() + "..."
                                + System.lineSeparator() + "Downloaded " + bytesDownloaded + " bytes..."));
                        return;
                    }
                    if (now - lastProgressUpdate >= PROGRESS_THROTTLE_MS) {
                        super.updateProgress(workDone, max);
                        lastProgressUpdate = now;
                        Platform.runLater(() -> progress.setHeaderText("Downloading " + mmp.count + " mods. You can keep"
                                + System.lineSeparator() + "working while this is going."
                                + System.lineSeparator() + "Downloading " + mod.name.get() + "..."
                                + System.lineSeparator() + "Downloaded " + bytesDownloaded + " bytes..."));
                    }
                }

                @Override
                protected Void call() throws Exception {
                    long maxProgress = (mmp.count * 27L);
                    long pogress = 0;
                    updateProgress(pogress, maxProgress);
                    for (var mod : mmp.mods.getItems())
                    {
                        var modPack = mod.parentModPack;
                        Platform.runLater(() ->progress.setHeaderText("Downloading " + mmp.count + " mods. You can keep"
                                + System.lineSeparator() + "working while this is going."
                                + System.lineSeparator() + "Downloading " + mod.name.get() + "..."));
                        try {
                            if (!mod.property.get()) continue;
                            if (modPack.isDeleted) cancel(true);
                            updateProgress(pogress++, maxProgress);

                            mod.observableStatus.set(SimpleStatusProperty.Status.DOWNLOADING);
                            if (modPack.isDeleted) cancel(true);
                            updateProgress(pogress++, maxProgress);

                            if (mod.site.getSite() == SimpleSiteProperty.Site.REIKA) {
                                if (modPack.isDeleted) cancel(true);
                                updateProgress(pogress++, maxProgress);

                                HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS).build();
                                if (modPack.isDeleted) cancel(true);
                                updateProgress(pogress++, maxProgress);

                                HttpRequest request = HttpRequest.newBuilder().uri(URI.create(mod.link)).GET().build();
                                if (modPack.isDeleted) cancel(true);
                                updateProgress(pogress++, maxProgress);

                                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                                if (modPack.isDeleted) cancel(true);
                                updateProgress(pogress++, maxProgress);

                                String html = response.body();
                                if (modPack.isDeleted) cancel(true);
                                updateProgress(pogress++, maxProgress);

                                int startIndex = html.indexOf("<h3>Downloads</h3>") + 44;
                                if (modPack.isDeleted) cancel(true);

                                updateProgress(pogress++, maxProgress);
                                int endIndex = html.indexOf("Via GitHub Releases") - 8;
                                if (modPack.isDeleted) cancel(true);
                                updateProgress(pogress++, maxProgress);

                                String fileLink = html.substring(startIndex, endIndex);
                                if (modPack.isDeleted) cancel(true);
                                updateProgress(pogress++, maxProgress);

                                var requestURI = URI.create(fileLink);
                                if (modPack.isDeleted) cancel(true);
                                updateProgress(pogress++, maxProgress);

                                long contLength = 0;
                                if (modPack.isDeleted) cancel(true);
                                updateProgress(pogress++, maxProgress);

                                String filePath = modPack.modFilePath.get() + File.separator + ".modpackmanager" + File.separator + mod.name.get().trim() + ".zip";
                                if (modPack.isDeleted) cancel(true);
                                updateProgress(pogress++, maxProgress);

                                mod.currentFile = new ModFolder(filePath);
                                if (modPack.isDeleted) cancel(true);
                                updateProgress(pogress++, maxProgress);

                                mod.currentFile.ensureExists();
                                if (modPack.isDeleted) cancel(true);
                                updateProgress(pogress++, maxProgress);

                                request = HttpRequest.newBuilder(URI.create(fileLink))
                                        .timeout(java.time.Duration.ofMinutes(3)).GET().build();
                                if (modPack.isDeleted) cancel(true);
                                updateProgress(pogress++, maxProgress);

                                HttpResponse<InputStream> IStream = client.send(request,
                                        HttpResponse.BodyHandlers.ofInputStream());
                                if (modPack.isDeleted) cancel(true);
                                updateProgress(pogress++, maxProgress);

                                int status = IStream.statusCode();
                                if (modPack.isDeleted) cancel(true);
                                updateProgress(pogress++, maxProgress);

                                if (status != 200) Platform.runLater(() ->
                                        ModPackManagerController.showError("HTTP Error!",
                                                "HTTP Request IS NOT OK! HTTP Code: "
                                                        + status));
                                if (modPack.isDeleted) cancel(true);
                                updateProgress(pogress++, maxProgress);

                                try {contLength = IStream.headers().firstValue("Content-Length")
                                        .map(Long::parseLong).orElse(-1L);
                                    if (modPack.isDeleted) cancel(true);
                                    updateProgress(pogress++, maxProgress);

                                    if (contLength > 0) maxProgress += contLength;
                                    if (modPack.isDeleted) cancel(true);
                                    updateProgress(pogress++, maxProgress);} catch (Exception ex) {
                                    Platform.runLater(() -> ModPackManagerController.showException(ex));}

                                try (InputStream in = IStream.body(); OutputStream file =
                                        Files.newOutputStream(mod.currentFile.toPath()))
                                {
                                    if (modPack.isDeleted) cancel(true);
                                    updateProgress(pogress++, maxProgress);

                                    byte[] buffer = new byte[64 * 1024];
                                    if (modPack.isDeleted) cancel(true);
                                    updateProgress(pogress++, maxProgress);

                                    int read;
                                    if (modPack.isDeleted) cancel(true);
                                    updateProgress(pogress++, maxProgress);

                                    while ((read = in.read(buffer)) != -1)
                                    {
                                        if (modPack.isDeleted) cancel(true);
                                        updateProgress(pogress++, maxProgress, read, mod);

                                        file.write(buffer, 0, read);
                                        if (modPack.isDeleted) cancel(true);
                                        if (contLength > 0) pogress += read;
                                        updateProgress(pogress, maxProgress, read, mod);
                                    }
                                }
                            }
                            if (mod.site.getSite() == SimpleSiteProperty.Site.NEXUSMODS)
                            {
                                if (modPack.isDeleted) cancel(true);
                                updateProgress(pogress++, maxProgress);
                                //CUT TODO: Code Nexus Mods API Access back end
                            }
                            mod.observableStatus.set(SimpleStatusProperty.Status.NOTINSTALLED);
                            updateProgress(pogress++, maxProgress);
                        }
                        catch (Exception ex)
                        {
                            mod.observableStatus.set(SimpleStatusProperty.Status.NOTDOWNLOADED);
                            if (!(ex instanceof InterruptedException)) {
                                Platform.runLater(() -> ModPackManagerController.showException(ex, "Mod Download Failed! Failed to download files for mod " + mod.name.get().trim() + "! Skipping..." + System.lineSeparator() + "Exception:"));
                            }
                        }
                    }
                    // ensure we flush final progress
                    updateProgress(maxProgress, maxProgress);
                    return null;
                }
            };
            bar.progressProperty().bind(task.progressProperty());
            progress.setResultConverter(f->
            {
                if (f == ButtonType.CANCEL)
                {
                    task.cancel();
                }
                return null;
            });
            task.setOnSucceeded(g ->
            {
                progress.close();
            });
            task.setOnFailed(event -> {
                String message = "";
                for (var i : event.getSource().getException().getStackTrace())
                {
                    message += "at " + i + System.lineSeparator();
                }
                ModPackManagerController.showError("Error", e.getClass() + " " + System.lineSeparator() + message);
                progress.close();
            });
            task.setOnCancelled(f ->
            {
                if (f.getSource().getException() != null)
                {
                    ModPackManagerController.showException(f.getSource().getException());
                }
                else
                {
                    ModPackManagerController.showError("Download Failed!", "Download Canceled!");
                }
                progress.close();
            });
            progress.initModality(Modality.NONE);
            progress.show();
            mmp.scheduleAsyncTask(task);
        });
        return downloadButton;
    }
    public static Button getModInstallButton(ModPackManager mmp)
    {
        //TODO: Add install button
        Button button = new Button("Install Selected Mod(s)");
        button.setOnAction(event ->
        {
            Dialog<Void> progress = new Dialog<>();
            mmp.count = 0;
            for (var item : mmp.mods.getItems())
            {
                if (!item.property.get()) continue;
                mmp.count++;
            }
            progress.setTitle("Installing " + mmp.count + " mods...");
            progress.setHeaderText("Installing " + mmp.count + " mods. You can keep"
                    + System.lineSeparator() + "working while this is going.");
            ProgressBar bar = new ProgressBar(0);
            progress.getDialogPane().setContent(bar);
            progress.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
            Task<Void> task = new Task<>() {
                // Time-based throttling for install task as well.
                private volatile long lastProgressUpdate = 0;
                private static final long PROGRESS_THROTTLE_MS = 250;

                @Override
                protected void updateProgress(long workDone, long max) {
                    long now = System.currentTimeMillis();
                    // always flush immediately at completion
                    if (max > 0 && workDone >= max) {
                        super.updateProgress(workDone, max);
                        lastProgressUpdate = now;
                        return;
                    }
                    if (now - lastProgressUpdate >= PROGRESS_THROTTLE_MS) {
                        super.updateProgress(workDone, max);
                        lastProgressUpdate = now;
                    }
                }
                private void updateProgress(long workDone, long max, long bytesUnzipped, Mod mod)
                {
                    long now = System.currentTimeMillis();
                    if (max > 0 && workDone >= max) {
                        super.updateProgress(workDone, max);
                        lastProgressUpdate = now;
                        Platform.runLater(() -> progress.setHeaderText("Installing " + mmp.count + " mods. You can keep"
                                + System.lineSeparator() + "working while this is going."
                                + System.lineSeparator() + "Installing " + mod.name.get() + "..."
                                + System.lineSeparator() + "Unzipped " + bytesUnzipped + " bytes..."));
                        return;
                    }
                    if (now - lastProgressUpdate >= PROGRESS_THROTTLE_MS) {
                        super.updateProgress(workDone, max);
                        lastProgressUpdate = now;
                        Platform.runLater(() -> progress.setHeaderText("Installing " + mmp.count + " mods. You can keep"
                                + System.lineSeparator() + "working while this is going."
                                + System.lineSeparator() + "Installing " + mod.name.get() + "..."
                                + System.lineSeparator() + "Unzipped " + bytesUnzipped + " bytes..."));
                    }
                }
                ZipEntry entry;
                @Override
                protected Void call() throws Exception {
                    long maxProgress = (mmp.count * 40L);
                    long pogress = 0;
                    updateProgress(pogress, maxProgress);
                    for (var mod : mmp.mods.getItems())
                    {
                        Platform.runLater(() ->progress.setHeaderText("Installing " + mmp.count + " mods. You can keep"
                                + System.lineSeparator() + "working while this is going."
                                + System.lineSeparator() + "Installing " + mod.name.get() + "..."));
                        var modPack = mod.parentModPack;
                        if (modPack.isDeleted) cancel(true);
                        updateProgress(pogress++, maxProgress);

                        mod.backUpFile = mod.currentFile;
                        if (modPack.isDeleted) cancel(true);
                        updateProgress(pogress++, maxProgress);

                        try
                        {
                            if (modPack.isDeleted) cancel(true);
                            updateProgress(pogress++, maxProgress);

                            if (mod.property.get())
                            {
                                if (modPack.isDeleted) cancel(true);
                                updateProgress(pogress++, maxProgress);

                                byte[] buffer = new byte[8096];
                                if (modPack.isDeleted) cancel(true);
                                updateProgress(pogress++, maxProgress);

                                mod.observableStatus.set(SimpleStatusProperty.Status.INSTALLING);
                                if (modPack.isDeleted) cancel(true);
                                updateProgress(pogress++, maxProgress);

                                long fileSize = 0;
                                if (modPack.isDeleted) cancel(true);
                                updateProgress(pogress++, maxProgress);

                                try {fileSize = Files.size(mod.currentFile.toPath());} catch (Exception _) {}
                                if (modPack.isDeleted) cancel(true);
                                updateProgress(pogress++, maxProgress);

                                if (fileSize > 0) maxProgress += fileSize;
                                if (modPack.isDeleted) cancel(true);
                                updateProgress(pogress++, maxProgress);

                                ZipInputStream zis = new ZipInputStream(new FileInputStream(mod.currentFile));
                                if (modPack.isDeleted) cancel(true);
                                updateProgress(pogress++, maxProgress);

                                entry = zis.getNextEntry();
                                if (modPack.isDeleted) cancel(true);
                                updateProgress(pogress++, maxProgress);

                                boolean subnauticaLegacy = modPack.game.get().equals("Subnautica") && modPack.version.get().equals("Legacy");
                                if (modPack.isDeleted) cancel(true);
                                updateProgress(pogress++, maxProgress);

                                boolean subnauticaBepinex = modPack.game.get().equals("Subnautica") && !modPack.version.get().equals("Legacy");
                                if (modPack.isDeleted) cancel(true);
                                updateProgress(pogress++, maxProgress);

                                if (subnauticaLegacy)
                                    mod.currentFile = new ModFolder(modPack.modFilePath.get() + File.separator + "QMods");
                                else if (subnauticaBepinex)
                                    //mod.currentFile = new ModFolder(modPack.modFilePath.get() + File.separator + "BepInEx" + File.separator + "plugins");
                                    //CUT TODO: Manage BepInEx Installs
                                if (modPack.isDeleted) cancel(true);
                                updateProgress(pogress++, maxProgress);

                                ModFolder modFolder = null;
                                if (modPack.isDeleted) cancel(true);
                                updateProgress(pogress++, maxProgress);

                                while (entry != null)
                                {
                                    if (modPack.isDeleted) cancel(true);
                                    updateProgress(pogress++, maxProgress);

                                    ModFolder newFile = new ModFolder(mod.currentFile.getAbsolutePath() + File.separator + entry.getName());
                                    if (modPack.isDeleted) cancel(true);
                                    updateProgress(pogress++, maxProgress);

                                    if (entry.isDirectory())
                                    {
                                        if (modPack.isDeleted) cancel(true);
                                        updateProgress(pogress++, maxProgress);

                                        if (!newFile.isDirectory() && !newFile.mkdirs())
                                        {
                                            if (modPack.isDeleted) cancel(true);
                                            updateProgress(pogress++, maxProgress);

                                            throw new Exception("Failed to create directory " + newFile);
                                        }
                                        if (modPack.isDeleted) cancel(true);
                                        updateProgress(pogress++, maxProgress);
                                    }
                                    else
                                    {
                                        if (modPack.isDeleted) cancel(true);
                                        updateProgress(pogress++, maxProgress);

                                        File parent = newFile.getParentFile();
                                        if (modPack.isDeleted) cancel(true);
                                        updateProgress(pogress++, maxProgress);

                                        if (!parent.isDirectory() && !parent.mkdirs())
                                        {
                                            if (modPack.isDeleted) cancel(true);
                                            updateProgress(pogress++, maxProgress);

                                            throw new Exception("Failed to create directory " + parent);
                                        }
                                        if (modPack.isDeleted) cancel(true);
                                        updateProgress(pogress++, maxProgress);

                                        try (var fos = Files.newOutputStream(newFile.toPath()))
                                        {
                                            if (modPack.isDeleted) cancel(true);
                                            updateProgress(pogress++, maxProgress);

                                            int len;
                                            if (modPack.isDeleted) cancel(true);
                                            updateProgress(pogress++, maxProgress);

                                            while ((len = zis.read(buffer)) > 0)
                                            {
                                                if (modPack.isDeleted) cancel(true);
                                                updateProgress(pogress++, maxProgress);

                                                fos.write(buffer, 0, len);
                                                if (modPack.isDeleted) cancel(true);
                                                if (fileSize > 0) pogress += len;
                                                updateProgress(pogress, maxProgress, len, mod);
                                            }
                                        }
                                    }
                                    if (subnauticaLegacy && entry.getName().endsWith(".dll"))
                                        modFolder = new ModFolder(new File(mod.currentFile.getAbsolutePath() + File.separator + entry.getName()).getParent());
                                    if (modPack.isDeleted) cancel(true);
                                    updateProgress(pogress++, maxProgress);

                                    entry = zis.getNextEntry();
                                    if (modPack.isDeleted) cancel(true);
                                    updateProgress(pogress++, maxProgress);

                                }
                                zis.closeEntry();
                                if (modPack.isDeleted) cancel(true);
                                updateProgress(pogress++, maxProgress);

                                zis.close();
                                if (modPack.isDeleted) cancel(true);
                                updateProgress(pogress++, maxProgress);
                                Platform.runLater(() ->progress.setHeaderText("Installing " + mmp.count + " mods. You can keep"
                                        + System.lineSeparator() + "working while this is going."
                                        + System.lineSeparator() + "Installing " + mod.name.get() + "..."
                                        + System.lineSeparator() + "Moving to mod folder..."));

                                if (mod.site.getSite() == SimpleSiteProperty.Site.REIKA && mod.parentModPack.version.get().equals("Legacy"))
                                {
                                    if (modPack.isDeleted) cancel(true);
                                    updateProgress(pogress++, maxProgress);

                                    if (modFolder != null)
                                        mod.currentFile = modFolder;
                                    if (modPack.isDeleted) cancel(true);
                                    updateProgress(pogress++, maxProgress);

                                    /*var version = Files.readString(Path.of(mod.currentFile.getAbsolutePath()
                                            + File.separator + "current-version.txt"));
                                    if (modPack.isDeleted) cancel(true);
                                    updateProgress(pogress++, maxProgress);

                                    if (mod.version == null || mod.version.get().isBlank())
                                        mod.version = new SimpleStringProperty(version.substring(0, 5));
                                    else if (!mod.version.get().equals(version.substring(0, 5)))
                                        mod.version.set(version.substring(0, 5));
                                    if (modPack.isDeleted) cancel(true);
                                    updateProgress(pogress++, maxProgress);

                                    var qmodsFolder = new File(mod.parentModPack.modFilePath.get()
                                            + File.separator + "QMods");
                                    if (modPack.isDeleted) cancel(true);
                                    updateProgress(pogress++, maxProgress);

                                    if (!qmodsFolder.exists()) if (!qmodsFolder.mkdirs()) throw new IOException("Failed to create mod folder under QMods folder at "
                                            + qmodsFolder.getAbsolutePath());
                                    if (modPack.isDeleted) cancel(true);
                                    updateProgress(pogress++, maxProgress);

                                    mod.currentFile = new ModFolder(Files.move(mod.currentFile.toPath(), qmodsFolder.toPath(),
                                            StandardCopyOption.REPLACE_EXISTING).toUri());
                                    if (modPack.isDeleted) cancel(true);
                                    updateProgress(pogress++, maxProgress);*/

                                    mod.observableStatus.set(SimpleStatusProperty.Status.INSTALLED);
                                    if (modPack.isDeleted) cancel(true);
                                    updateProgress(pogress++, maxProgress);
                                }
                                else if (mod.site.getSite() == SimpleSiteProperty.Site.NEXUSMODS)
                                {
                                    if (modPack.isDeleted) cancel(true);
                                    updateProgress(pogress++, maxProgress);

                                    //CUT TODO: Code Nexus Mods install back end. Including BepInEx & QMod installation
                                }
                            }
                        }
                        catch (Exception ex)
                        {
                            Platform.runLater(() -> ModPackManagerController.showException(ex, "Mod Installation Failed! Failed to install files for mod "
                                    + mod.name.get().trim() + "! Please attempt a manual install! Skipping..." + System.lineSeparator() + "Exception:"));
                            mod.observableStatus.set(SimpleStatusProperty.Status.NOTINSTALLED);
                            mod.currentFile.deleteFolder();
                            mod.currentFile = mod.backUpFile;
                            mod.backUpFile = null;
                        }
                    }
                    // flush final progress
                    updateProgress(maxProgress, maxProgress);
                    return null;
                }
            };
            bar.progressProperty().bind(task.progressProperty());
            progress.setResultConverter(f->
            {
                if (f == ButtonType.CANCEL)
                {
                    task.cancel();
                }
                return null;
            });
            task.setOnSucceeded(g ->
            {
                progress.close();
            });
            task.setOnFailed(e -> {
                String message = "";
                for (var i : e.getSource().getException().getStackTrace())
                {
                    message += "at " + i + System.lineSeparator();
                }
                ModPackManagerController.showError("Error", e.getClass() + " " + System.lineSeparator() + message);
                progress.close();
            });
            task.setOnCancelled(f ->
            {
                if (f.getSource().getException() != null)
                {
                    ModPackManagerController.showException(f.getSource().getException());
                }
                else
                {
                    ModPackManagerController.showError("Installation Failed!", "Failed to complete download!");
                }
                progress.close();
            });
            progress.initModality(Modality.NONE);
            progress.show();
            mmp.scheduleAsyncTask(task);
        });
        return button;
    }
    public static Button getUninstallButton(ModPackManager mmp) //cut because of time
    {
        Button uninstallButton = new Button("Uninstall Selected Mod(s)");
        uninstallButton.setOnAction(e ->
        {
            try {
                if (mmp.shiftKeyPressed) {
                    mmp.scheduleAsyncTask(mmp::modUninstallHelper);
                } else {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Confirm Uninstallation");
                    alert.setHeaderText("Uninstall Selected Mod(s)?");
                    alert.setContentText("Are you sure you want to uninstall this(these) Mod(s)? THIS WILL DELETE ANY AND ALL DATA THE MOD HAS INSTALLED");
                    var item = alert.showAndWait();
                    item.ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            mmp.scheduleAsyncTask(mmp::modUninstallHelper);
                        }
                    });

                }
            } catch (Exception ex) {
                ModPackManagerController.showException(ex);
            }
        });
        return uninstallButton;
    }
    public static Button getDeleteButton(ModPackManager mmp)
    {
        Button deleteButton = new Button("Delete Selected Mod Pack(s)");
        deleteButton.setOnAction(e ->
        {
            if (mmp.shiftKeyPressed)
            {
                mmp.scheduleAsyncTask(mmp::modPackDeleteHelper);
            }
            else
            {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirm Deletion");
                alert.setHeaderText("Delete Selected Mod Pack(s)?");
                alert.setContentText("Are you sure you want to delete this(these) Mod Pack(s)? THIS WILL DELETE ANY AND ALL DATA THE MOD PACK HAS, INCLUDING MODS IN THE MODPACK (except mod save data or options)");
                var type = alert.showAndWait();
                type.ifPresent(response ->
                {
                    if (response == ButtonType.OK)
                        mmp.scheduleAsyncTask(mmp::modPackDeleteHelper);
                });
            }
        });
        return deleteButton;
    }
    public static Button getModDeleteButton(ModPackManager mmp)
    {
        Button modDeleteButton = new Button("Delete Selected Mod(s)");
        modDeleteButton.setOnAction(e ->
        {
            try {
                if (mmp.shiftKeyPressed) {
                    mmp.scheduleAsyncTask(mmp::modDeleteHelper);
                } else {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Confirm Deletion");
                    alert.setHeaderText("Delete Selected Mod(s)?");
                    alert.setContentText("Are you sure you want to delete this(these) Mod(s)? THIS WILL DELETE ANY AND ALL DATA THE MOD HAS (except mod save data or options)");
                    var item = alert.showAndWait();
                    item.ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            mmp.scheduleAsyncTask(mmp::modDeleteHelper);
                        }
                    });

                }
            } catch (Exception ex) {
                ModPackManagerController.showException(ex);
            }
        });
        return modDeleteButton;
    }
    public static Button getModImportButton(ModPackManager mmp) throws Exception
    {
        //TODO: Add file import button
        throw new ExecutionControl.NotImplementedException("Method getModImportButton not implemented!");
    }
}
