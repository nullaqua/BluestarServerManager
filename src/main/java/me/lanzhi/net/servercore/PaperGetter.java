package me.lanzhi.net.servercore;

import me.lanzhi.UI;
import me.lanzhi.net.NetManager;
import me.lanzhi.net.ServerCoreGetter;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Pattern;

class PaperGetter implements ServerCoreGetter
{
    private static final Pattern pattern=Pattern.compile("\"builds\":\\[([0-9,]+)]");

    @Override
    public String name()
    {
        return "Paper";
    }

    @Override
    public boolean hasVersion(String version)
    {
        try
        {
            URL url=new URL("https://api.papermc.io/v2/projects/paper/versions/"+version);
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
    public URL getDownloadURL(String version)
    {
        String build=getBuild(version);
        var res=String.format("https://api.papermc.io/v2/projects/paper/versions/%s/builds/%s/downloads/paper-%s-%s"+
                              ".jar",version,build,version,build);
        try
        {
            return new URL(res);
        }
        catch (Throwable e)
        {
            return null;
        }
    }

    private String getBuild(String version)
    {
        try
        {
            URL url=new URL("https://api.papermc.io/v2/projects/paper/versions/"+version);
            var result=NetManager.get(url);
            if (result==null)
            {
                return null;
            }
            System.out.println(result);
            var matcher=pattern.matcher(result);
            if (!matcher.find())
            {
                return null;
            }
            String build=matcher.group(1);
            String[] builds=build.split(",");
            return builds[builds.length-1];
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
            return new ImageIcon(UI.class.getResource("resources/paper.png")).getImage();
        }
        catch (Throwable e)
        {
            return new BufferedImage(1,1,BufferedImage.TYPE_INT_ARGB);
        }
    }
}
