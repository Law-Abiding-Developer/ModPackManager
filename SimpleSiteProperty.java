package com.lad.mmp;

import javafx.beans.property.SimpleStringProperty;

public class SimpleSiteProperty extends SimpleStringProperty
{
    private Site site = Site.NULL;
    public String get()
    {
        return switch (site) {
            case REIKA -> "Reika's Site";
            case NULL -> "";
            case GITHUB -> "Github";
            case NEXUSMODS -> "Nexus Mods";
        };
    }
    public Site getSite()
    {
        return site;
    }
    @Override
    public void set(String s)
    {
        if (s.equals("Reika's Site")) site = SimpleSiteProperty.Site.REIKA;
        else if (s.equals("Github")) site = SimpleSiteProperty.Site.GITHUB;
        else if (s.equals("Nexus Mods")) site = SimpleSiteProperty.Site.NEXUSMODS;
        else site = SimpleSiteProperty.Site.NULL;
        super.set(s);
    }
    public void set(Site site)
    {
        this.site = site;
        super.set(get());
    }
    public enum Site {
        NULL,
        REIKA,
        GITHUB,
        NEXUSMODS
    }
}
