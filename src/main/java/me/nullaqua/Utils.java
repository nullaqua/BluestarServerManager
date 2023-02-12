package me.nullaqua;

import com.github.weisj.darklaf.LafManager;
import com.github.weisj.darklaf.theme.IntelliJTheme;
import com.github.weisj.darklaf.theme.Theme;
import me.lanzhi.api.reflect.ReflectAccessor;
import me.lanzhi.api.util.collection.FastLinkedList;
import me.lanzhi.api.util.io.IOAccessor;
import me.lanzhi.api.util.io.KeyObjectInputStream;
import me.lanzhi.api.util.io.file.FileWithVersionReader;
import me.lanzhi.api.util.io.file.FileWithVersionWriter;
import me.lanzhi.api.util.io.file.ReadVersion;

import javax.swing.*;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;

import static me.nullaqua.UI.frame;

public class Utils implements Serializable
{
    @Serial
    private static final long serialVersionUID=1_000_000L;
    private static Utils instance;
    private static Utils unsaved;
    public String java_exe;
    public String serverJar;
    public String xmx;
    public String xms;
    public List<String> javaAgent;
    public List<String> javaArgs;
    public String lookAndFeel;

    public Utils()
    {
        serverJar=new File("server.jar").getAbsolutePath();
        xmx="";
        xms="";
        javaAgent=new FastLinkedList<>();
        javaArgs=new FastLinkedList<>();
        java_exe="java";
        lookAndFeel=com.github.weisj.darklaf.theme.DarculaTheme.class.getName();
    }

    public static Utils getInstance()
    {
        return instance;
    }

    public static Utils getUnsaved()
    {
        if (unsaved==null)
        {
            unsaved=instance.clone();
        }
        return unsaved;
    }

    @Override
    public Utils clone()
    {
        Utils utils=new Utils();
        utils.java_exe=java_exe;
        utils.serverJar=serverJar;
        utils.xmx=xmx;
        utils.xms=xms;
        utils.javaAgent=new FastLinkedList<>(javaAgent);
        utils.javaArgs=new FastLinkedList<>(javaArgs);
        utils.lookAndFeel=lookAndFeel;
        return utils;
    }

    public static void load()
    {
        File file=Main.data();
        read:
        {
            if (!file.exists())
            {
                Utils.instance=new Utils();
                break read;
            }
            var in=new FileWithVersionReader(file);
            in.read(new FileWithVersionReader.Worker()
            {
                @ReadVersion("1.0.0")
                public void read(KeyObjectInputStream in)
                {
                    System.out.println("Loading data...");
                    try
                    {
                        instance=(Utils) in.readObject();
                    }
                    catch (Throwable e)
                    {
                        e.printStackTrace();
                    }
                }

                @Override
                public void defaultRead(String version,KeyObjectInputStream stream)
                {
                    UI.putError("配置文件读取失败 数据将被重置");
                    System.out.println(version);
                    Utils.instance=new Utils();
                }
            },null,IOAccessor.hexKey());
        }
        save(false);
    }

    public static void save(boolean saveUnsaved)
    {
        if (saveUnsaved)
        {
            instance=unsaved;
            unsaved=null;
        }
        try
        {
            LafManager.setLogLevel(Level.OFF);
            LafManager.install(new IntelliJTheme());
            UIManager.setLookAndFeel(instance.lookAndFeel);
            SwingUtilities.updateComponentTreeUI(frame);
        }
        catch (Throwable e)
        {
            try
            {
                var x=ReflectAccessor.getClass(instance.lookAndFeel);
                Class<? extends Theme> themeClass=x.asSubclass(Theme.class);
                var theme=themeClass.getConstructor().newInstance();
                LafManager.install(theme);
            }
            catch (Throwable e1)
            {
                UI.putError("主题加载失败");
            }
        }
        File file=Main.data();
        if (!file.exists())
        {
            try
            {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            catch (Throwable e)
            {
                UI.putError("创建配置文件并报错时失败",e);
                return;
            }
        }
        var out=new FileWithVersionWriter(file);
        try
        {
            out.saveFile(null,IOAccessor.hexKey(),"1.0.0",s->s.writeObject(instance));
        }
        catch (Throwable e)
        {
            UI.putError("保存配置文件时失败",e);
        }
    }

    public static void save()
    {
        save(true);
    }

    public static List<String> getArgs()
    {
        var list=new FastLinkedList<String>();
        list.add(instance.java_exe);
        if (instance.xmx!=null&&instance.xmx.length()>2)
        {
            list.add("-Xmx"+instance.xmx.substring(0,instance.xmx.length()-1));
        }
        if (instance.xms!=null&&instance.xms.length()>2)
        {
            list.add("-Xms"+instance.xms.substring(0,instance.xms.length()-1));
        }
        for (var file: instance.javaAgent)
        {
            list.add("-javaagent:"+file);
        }
        list.addAll(instance.javaArgs);
        list.add("-jar");
        list.add(instance.serverJar);
        list.add("--nogui");
        return list;
    }

    public static void addJavaAgent(String file)
    {
        instance.javaAgent.add(file);
    }

    public static void addJavaArgs(String arg)
    {
        instance.javaArgs.add(arg);
    }

    public static void removeJavaAgent(String file)
    {
        instance.javaAgent.remove(file);
    }

    public static void removeJavaAgent(int x)
    {
        instance.javaAgent.remove(x);
    }

    public static void removeJavaArgs(String arg)
    {
        instance.javaArgs.remove(arg);
    }

    public static void clearJavaAgent()
    {
        instance.javaAgent.clear();
    }

    public static void clearJavaArgs()
    {
        instance.javaArgs.clear();
    }

    public static String getServerJar()
    {
        return instance.serverJar;
    }

    public static void setServerJar(String file)
    {
        instance.serverJar=file;
    }

    public static String getXmx()
    {
        return instance.xmx;
    }

    public static void setXmx(String xmx)
    {
        instance.xmx=xmx;
    }

    public static String getXms()
    {
        return instance.xms;
    }

    public static void setXms(String xms)
    {
        instance.xms=xms;
    }

    public static List<String> getJavaAgent()
    {
        return instance.javaAgent;
    }

    public static void setJavaAgent(List<String> list)
    {
        instance.javaAgent=list;
    }

    public static List<String> getJavaArgs()
    {
        return instance.javaArgs;
    }

    public static void setJavaArgs(List<String> list)
    {
        instance.javaArgs=list;
    }

    public static File getDir()
    {
        return new File(instance.serverJar).getParentFile();
    }

    public static String getJava()
    {
        return instance.java_exe;
    }

    public static void setJava(String file)
    {
        instance.java_exe=file;
    }

    public static String getCurrentJavaHome()
    {
        try
        {
            String pid=ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
            Process process=Runtime.getRuntime().exec(String.format("wmic process %s GET ExecutablePath",pid));
            try (Scanner scanner=new Scanner(process.getInputStream(),StandardCharsets.UTF_8))
            {
                while (scanner.hasNextLine())
                {
                    String line=scanner.nextLine();
                    if (line.contains("ExecutablePath"))
                    {
                        continue;
                    }
                    return line;
                }
            }
        }
        catch (Exception e)
        {
            UI.putError("获取默认java路径失败",e);
        }
        return null;
    }

    @Serial
    private void writeObject(ObjectOutputStream out) throws IOException
    {
        out.writeObject(serverJar);
        out.writeObject(xmx);
        out.writeObject(xms);
        out.writeInt(javaAgent.size());
        for (var file: javaAgent)
        {
            out.writeObject(file);
        }
        out.writeInt(javaArgs.size());
        for (var arg: javaArgs)
        {
            out.writeObject(arg);
        }
        out.writeObject(java_exe);
        out.writeObject(lookAndFeel);
    }

    @Serial
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
    {
        serverJar=(String) in.readObject();
        xmx=(String) in.readObject();
        xms=(String) in.readObject();
        int size=in.readInt();
        javaAgent=new FastLinkedList<>();
        for (int i=0;i<size;i++)
        {
            javaAgent.add((String) in.readObject());
        }
        size=in.readInt();
        javaArgs=new FastLinkedList<>();
        for (int i=0;i<size;i++)
        {
            javaArgs.add((String) in.readObject());
        }
        java_exe=(String) in.readObject();
        lookAndFeel=(String) in.readObject();
    }
}