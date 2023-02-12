package me.nullaqua.net;


import me.nullaqua.net.servercore.ServerCoreGetter;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class NetManager
{
    private static final List<ServerCoreGetter> serverCores=new ArrayList<>();

    static
    {
        ServerCoreGetter.reg();
    }

    public static void register(ServerCoreGetter getter)
    {
        serverCores.add(getter);
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
