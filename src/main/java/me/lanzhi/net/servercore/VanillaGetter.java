package me.lanzhi.net.servercore;

import me.lanzhi.UI;
import me.lanzhi.net.NetManager;
import me.lanzhi.net.ServerCoreGetter;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.regex.Pattern;

class VanillaGetter implements ServerCoreGetter
{
    private static final Pattern pattern=Pattern.compile("https://getbukkit.org/get/[0-9a-zA-Z]+");
    private static final Pattern pattern2=Pattern.compile("https://[^\"]+/server.jar");

    @Override
    public String name()
    {
        return "原版服务器";
    }

    @Override
    public boolean hasVersion(String version)
    {
        try
        {
            var res=NetManager.get(new URL("https://getbukkit.org/download/vanilla"));
            if (res==null)
            {
                return false;
            }
            var p=Pattern.compile("[^\\.0-9]"+version.replace(".","\\.")+"[^\\.0-9]");
            return p.matcher(res).find();
        }
        catch (Throwable e)
        {
            return false;
        }
    }

    @Override
    public URL getDownloadURL(String version)
    {
        try
        {
            var res=NetManager.get(new URL("https://getbukkit.org/download/vanilla"));
            if (res==null)
            {
                return null;
            }
            var p=Pattern.compile("[^\\.0-9]"+version.replace(".","\\.")+"[^\\.0-9]");
            var temp=p.matcher(res);
            if (!temp.find())
            {
                return null;
            }
            var x=temp.start();
            if (x==-1)
            {
                return null;
            }
            var matcher=pattern.matcher(res);
            if (!matcher.find(x))
            {
                return null;
            }
            res=NetManager.get(new URL(matcher.group()));
            matcher=pattern2.matcher(res);
            //System.out.println(res);
            if (!matcher.find())
            {
                return null;
            }
            return new URL(matcher.group());
        }
        catch (Throwable throwable)
        {
            return null;
        }
    }

    @Override
    public Image icon()
    {
        try
        {
            return new ImageIcon(UI.class.getResource("resources/minecraft.png")).getImage();
        }
        catch (Throwable e)
        {
            return null;
        }
    }
}
