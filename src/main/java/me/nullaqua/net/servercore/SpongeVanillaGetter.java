package me.nullaqua.net.servercore;

import me.nullaqua.UI;
import me.nullaqua.net.NetManager;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.regex.Pattern;

class SpongeVanillaGetter implements ServerCoreGetter
{
    private static final Pattern versionP=Pattern.compile("<version>[^<]+</version>");

    public static void main(String[] args)
    {
        var x=new SpongeVanillaGetter();
        System.out.println(x.getDownloadURL("1.19.3"));
    }

    @Override
    public URL getDownloadURL(String version)
    {
        try
        {
            var ver=getVersion(version);
            return new URL("https://repo.spongepowered.org/maven/org/spongepowered/spongevanilla/spongevanilla/"+
                           ver+
                           "/spongevanilla-"+
                           ver+
                           ".jar");
        }
        catch (Throwable e)
        {
            return null;
        }
    }

    private static String getVersion(String ver)
    {
        try
        {
            var url=new URL("https://repo.spongepowered.org/maven/org/spongepowered/spongevanilla/maven-metadata.xml");
            var res=NetManager.get(url);
            if (res==null)
            {
                return null;
            }
            var temp=versionP.matcher(res);
            String lastVer=null;
            while (temp.find())
            {
                var x=temp.group();
                x=x.substring(9,x.length()-10);
                if (x.startsWith(ver+"-"))
                {
                    //选择字典序最大的版本
                    if (lastVer==null||lastVer.compareTo(x)<0)
                    {
                        lastVer=x;
                    }
                }
            }
            return lastVer;
        }
        catch (Throwable e)
        {
            return null;
        }
    }

    @Override
    public String name()
    {
        return "SpongeVanilla";
    }

    @Override
    public boolean hasVersion(String version)
    {
        return getVersion(version)!=null;
    }

    @Override
    public Image icon()
    {
        return new ImageIcon(UI.class.getResource("resources/sponge.png")).getImage();
    }
}
