package me.lanzhi.net;

import java.awt.*;
import java.net.URL;

public interface ServerCoreGetter
{
    public String name();

    public boolean hasVersion(String version);

    public URL getDownloadURL(String version);

    public Image icon();
}
