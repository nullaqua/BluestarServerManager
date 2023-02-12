package me.nullaqua.net.servercore;

import me.nullaqua.UI;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

class BungeeCordGetter implements ServerCoreGetter
{
    @Override
    public String name()
    {
        return "BungeeCord";
    }

    @Override
    public boolean hasVersion(String version)
    {
        return true;
    }

    @Override
    public URL getDownloadURL(String version)
    {
        try
        {
            return new URL("https://ci.md-5.net/job/BungeeCord/lastSuccessfulBuild/artifact/bootstrap/target"+
                           "/BungeeCord.jar");
        }
        catch (Throwable e)
        {
            return null;
        }
    }

    @Override
    public Image icon()
    {
        try
        {
            return new ImageIcon(UI.class.getResource("resources/spigot.png")).getImage();
        }
        catch (Throwable e)
        {
            return null;
        }
    }
}
