package me.lanzhi.net.servercore;

import me.lanzhi.UI;
import me.lanzhi.net.ServerCoreGetter;

import javax.swing.*;
import java.awt.*;
import java.net.HttpURLConnection;
import java.net.URL;

class PurpurGetter implements ServerCoreGetter
{
    @Override
    public String name()
    {
        return "Purpur";
    }

    @Override
    public URL getDownloadURL(String version)
    {
        try
        {
            if (hasVersion(version))
            {
                return new URL("https://api.purpurmc.org/v2/purpur/"+version+"/latest/download");
            }
            else
            {
                return null;
            }
        }
        catch (Throwable e)
        {
            return null;
        }
    }

    @Override
    public boolean hasVersion(String version)
    {
        try
        {
            URL url=new URL("https://api.purpurmc.org/v2/purpur/"+version);
            var conn=(HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            return conn.getResponseCode()==200;
        }
        catch (Throwable e)
        {
            return false;
        }
    }

    @Override
    public Image icon()
    {
        return new ImageIcon(UI.class.getResource("resources/purpur.png")).getImage();
    }
}