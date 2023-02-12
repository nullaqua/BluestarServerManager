package me.nullaqua.net.servercore;

import me.nullaqua.UI;
import me.nullaqua.net.NetManager;

import java.awt.*;
import java.net.URL;
import java.util.ArrayList;

public interface ServerCoreGetter
{
    static void reg()
    {
        var list=new ArrayList<Class<? extends ServerCoreGetter>>();
        list.add(BungeeCordGetter.class);
        list.add(PaperGetter.class);
        list.add(PurpurGetter.class);
        list.add(SpigotGetter.class);
        list.add(SpongeVanillaGetter.class);
        list.add(VanillaGetter.class);
        list.add(WaterFallGetter.class);

        for (var x: list)
        {
            try
            {
                x.getDeclaredConstructor().newInstance().register();
            }
            catch (Throwable throwable)
            {
                UI.putError("Error on load ServerCoreGetters: "+x.getName(),throwable);
            }
        }
    }

    public default void register()
    {
        NetManager.register(this);
    }

    public String name();

    public boolean hasVersion(String version);

    public URL getDownloadURL(String version);

    public Image icon();
}
