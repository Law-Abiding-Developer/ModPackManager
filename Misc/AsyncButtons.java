package com.lad.mmp.Misc;

import com.lad.mmp.Main.Mod;
import com.lad.mmp.Main.ModFolder;
import com.lad.mmp.Main.ModPackManager;
import com.lad.mmp.Main.ModPackManagerController;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.scene.control.*;
import javafx.stage.Modality;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
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
                private int updateProgressCalls = 0;

                @Override
                protected void updateProgress(long workDone, long max) {
                    if (updateProgressCalls > 50) super.updateProgress(workDone, max);
                    updateProgressCalls++;
                }
                private void updateProgress(long workDone, long max, long bytesDownloaded, Mod mod)
                {
                    progress.setHeaderText("Downloading " + mmp.count + " mods. You can keep"
                            + System.lineSeparator() + "working while this is going."
                            + System.lineSeparator() + "Downloading " + mod.name.get() + "..."
                            + System.lineSeparator() + "Downloaded " + bytesDownloaded + " bytes...");
                    if (updateProgressCalls > 50) super.updateProgress(workDone, max);
                    updateProgressCalls++;
                }

                @Override
                protected Void call() throws Exception {
                    var modPack = mmp.modpacks.getSelectionModel().getSelectedItem();
                    long maxProgress = (mmp.count * 16L);
                    long pogress = 0;
                    updateProgress(pogress, maxProgress);
                    for (var mod : mmp.mods.getItems())
                    {
                        progress.setHeaderText("Downloading " + mmp.count + " mods. You can keep"
                                + System.lineSeparator() + "working while this is going."
                                + System.lineSeparator() + "Downloading " + mod.name.get() + "...");
                        try {//TODO: code nexus backend for downloading site
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
                                //TODO: Code Nexus Mods API Access back end
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
            progress.setTitle("Downloading " + mmp.count + " mods...");
            progress.setHeaderText("Downloading " + mmp.count + " mods. You can keep"
                    + System.lineSeparator() + "working while this is going.");
            ProgressBar bar = new ProgressBar(0);
            progress.getDialogPane().setContent(bar);
            progress.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
            Task<Void> task = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    var modPack = mmp.modpacks.getSelectionModel().getSelectedItem();
                    int maxProgress = (mmp.count * 15) + 7;
                    int pogress = 0;
                    updateProgress(pogress, maxProgress);
                    for (var mod : mmp.mods.getItems())
                    {
                        try
                        {
                            if (mod.property.get())
                            {

                                byte[] buffer = new byte[8096];
                                ZipInputStream zis = new ZipInputStream(new FileInputStream(mod.currentFile));
                                ZipEntry entry = zis.getNextEntry();
                                while (entry != null)
                                {

                                }
                                zis.closeEntry();
                                zis.close();
                                if (mod.site.getSite() == SimpleSiteProperty.Site.REIKA && modPack.version.get().equals("Legacy"))
                                {
                                    var version = Files.readString(Path.of(mod.currentFile.getAbsolutePath() + File.separator + "current-version.txt"));
                                    mod.version = new SimpleStringProperty(version.substring(0, 5));
                                }
                                else if (mod.site.getSite() == SimpleSiteProperty.Site.NEXUSMODS)
                                {

                                }
                            }
                        }
                        catch (Exception ex)
                        {
                            Platform.runLater(() -> ModPackManagerController.showException(ex, "Mod Installation Failed! Failed to download files for mod " + mod.name.get().trim() + "! Please attempt a manual install! Skipping..." + System.lineSeparator() + "Exception:"));
                        }
                    }
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
                    ModPackManagerController.showError("Download Failed!", "Failed to complete download!");
                }
                progress.close();
            });
            progress.initModality(Modality.NONE);
            progress.show();
            mmp.scheduleAsyncTask(task);
        });
        return button;
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
            if (mmp.shiftKeyPressed) {
                mmp.scheduleAsyncTask(mmp::modDeleteHelper);
            }
            else
            {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirm Deletion");
                alert.setHeaderText("Delete Selected Mod(s)?");
                alert.setContentText("Are you sure you want to delete this(these) Mod(s)? THIS WILL DELETE ANY AND ALL DATA THE MOD HAS (except mod save data or options)");
                var item = alert.showAndWait();
                item.ifPresent(response ->{
                    if (response == ButtonType.OK) {
                        mmp.scheduleAsyncTask(mmp::modDeleteHelper);
                    }
                });

            }
        });
        return modDeleteButton;
    }
}
