package com.lad.mmp.Misc;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Dialog;

import java.util.concurrent.TimeUnit;

public abstract class DelayedTask<E> extends Task<E>
{
    private long lastUpdateMS = 0;
    private long delay = 250;
    private TimeUnit unit = TimeUnit.MILLISECONDS;
    public void setDelay(long d, TimeUnit u)
    {
        delay = d;
        unit = u;
    }
    @Override
    protected void updateProgress(long workDone, long max)
    {
        long now = unit.convert(System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        if (max > 0 && workDone >= max)
        {
            super.updateProgress(workDone, max);
            return;
        }
        if (now - lastUpdateMS >= delay)
        {
            lastUpdateMS = now;
            super.updateProgress(workDone, max);
        }
    }
    protected void updateProgress(long workDone, long max, String message, Dialog<E> dialog)
    {
        long now = unit.convert(System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        if (max > 0 && workDone >= max)
        {
            super.updateProgress(workDone, max);
            Platform.runLater(()-> dialog.setHeaderText(message));
            return;
        }
        if (now - lastUpdateMS >= delay)
        {
            lastUpdateMS = now;
            Platform.runLater(()-> dialog.setHeaderText(message));
            super.updateProgress(workDone, max);
        }
    }
}
