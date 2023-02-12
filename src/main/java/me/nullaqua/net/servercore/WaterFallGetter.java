package me.nullaqua.net.servercore;

import me.nullaqua.UI;
import me.nullaqua.net.NetManager;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.regex.Pattern;

class WaterFallGetter implements ServerCoreGetter
{
    private static final Pattern buildPattern=Pattern.compile("\"builds\":\\[([0-9,]+)]");
    private static final Pattern versionPattern=Pattern.compile("\"versions\":\\[(\\\"([0-9\\.]+)\\\",?)+]");

    public static void main(String[] args)
    {
        var getter=new WaterFallGetter();
        System.out.println(getter.getDownloadURL("1.19"));
    }

    @Override
    public URL getDownloadURL(String i)
    {
        var x=getBuild();
        var version=x[0];
        var build=x[1];
        var res=String.format(
                "https://api.papermc.io/v2/projects/waterfall/versions/%s/builds/%s/downloads/waterfall-%s-%s"+".jar",
                version,
                build,
                version,
                build);
        try
        {
            return new URL(res);
        }
        catch (Throwable e)
        {
            return null;
        }
    }

    private String[] getBuild()
    {
        var ver=getVersion();
        var build=getBuild(ver);
        return new String[]{ver,build};
    }

    private String getVersion()
    {
        try
        {
            URL url=new URL("https://api.papermc.io/v2/projects/waterfall");
            var result=NetManager.get(url);
            if (result==null)
            {
                return null;
            }
            var matcher=versionPattern.matcher(result);
            if (!matcher.find())
            {
                return null;
            }
            return matcher.group(2);
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
            URL url=new URL("https://api.papermc.io/v2/projects/waterfall/versions/"+version);
            var result=NetManager.get(url);
            if (result==null)
            {
                return null;
            }
            var matcher=buildPattern.matcher(result);
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
    public String name()
    {
        return "WaterFall";
    }

    @Override
    public boolean hasVersion(String version)
    {
        return true;
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
