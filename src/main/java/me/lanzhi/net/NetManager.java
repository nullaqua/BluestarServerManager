package me.lanzhi.net;


import me.lanzhi.UI;
import me.lanzhi.api.reflect.ConstructorAccessor;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class NetManager
{
    private static final List<ServerCoreGetter> serverCores;

    static
    {
        serverCores=new java.util.ArrayList<>();

        var classloader=NetManager.class.getClassLoader();
        Enumeration<URL> resources=null;
        try
        {
            resources=classloader.getResources("me/lanzhi/net/servercore");
        }
        catch (Throwable e)
        {
            UI.putError("Failed to load server core getter.",e);
        }
        while (resources.hasMoreElements())
        {
            var url=resources.nextElement();
            var path=url.getPath();
            var files=new java.io.File(path).listFiles();
            if (files==null)
            {
                continue;
            }
            for (var file: files)
            {
                if (file.getName().endsWith(".class"))
                {
                    var name=file.getName().substring(0,file.getName().length()-6);
                    try
                    {
                        var clazz=Class.forName("me.lanzhi.net.servercore."+name);
                        if (ServerCoreGetter.class.isAssignableFrom(clazz))
                        {
                            var constructor=ConstructorAccessor.getDeclaredConstructor(clazz);
                            serverCores.add((ServerCoreGetter) constructor.invoke());
                        }
                    }
                    catch (Throwable e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static String get(URL url)
    {
        try
        {
            var conn=(HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.connect();
            var code=conn.getResponseCode();
            if (code!=200)
            {
                return null;
            }
            return new String(conn.getInputStream().readAllBytes());
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static List<ServerCoreGetter> getServerCores()
    {
        return new ArrayList<>(serverCores);
    }
}
