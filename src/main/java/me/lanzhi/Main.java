package me.lanzhi;


import me.lanzhi.api.util.collection.ByteVector;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.function.Consumer;

public class Main
{
    //当前目录
    public static final String dir=new File("").getAbsolutePath();
    public static OutputStream in;
    public static InputStream out;
    public static InputStream err;
    public static Thread outThread;
    public static Thread errThread;
    public static Process process;

    public static void main(String[] args)
    {
        PrintStream out=new PrintStream(new OutputStream()
        {
            @Override
            public void write(int b)
            {
            }
        });
        //out=System.err;
        for (var x: args)
        {
            if (x.equals("debug")||x.equals("-debug")||x.equals("--debug"))
            {
                out=System.err;
                break;
            }
        }
        System.setOut(out);
        System.setErr(out);
        UI.init();
        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run()
            {
                if (process!=null)
                {
                    process.destroy();
                }
            }
        });
    }

    public static void start()
    {
        synchronized (Main.class)
        {
            if (process!=null)
            {
                return;
            }
            try
            {
                process=new ProcessBuilder(Utils.getArgs()).directory(Utils.getDir()).start();
            }
            catch (Throwable e)
            {
                UI.putError("尝试启动服务器失败",e);
                return;
            }
            ServerState.state(ServerState.Running);
            new Thread()
            {
                public void run()
                {
                    try
                    {
                        process.waitFor();
                    }
                    catch (Throwable e)
                    {
                        UI.putError("服务器进程异常终止",e);
                        process.destroy();
                        ServerState.state(ServerState.Error);
                        errThread.interrupt();
                        outThread.interrupt();
                        process=null;
                        errThread=null;
                        outThread=null;
                        in=null;
                        out=null;
                        err=null;
                        return;
                    }
                    errThread.interrupt();
                    outThread.interrupt();
                    process=null;
                    errThread=null;
                    outThread=null;
                    in=null;
                    out=null;
                    err=null;
                    ServerState.state(ServerState.Closed);
                }
            }.start();
            for (int i=0;i<100;i++)
            {
                UI.output.append("\r\n");
            }
            in=process.getOutputStream();
            out=process.getInputStream();
            err=process.getErrorStream();
            outThread=createOutputListener(out,s->
            {
                UI.output.append(s);
                //换行
                UI.output.append("\r\n");
            });
            errThread=createOutputListener(err,s->UI.output.append(s+"\r\n"));
            outThread.start();
            errThread.start();
        }
    }

    public static Thread createOutputListener(InputStream in,Consumer<String> consumer)
    {
        return new Thread()
        {
            @Override
            public void run()
            {
                var vector=new ByteVector();
                while (true)
                {
                    try
                    {
                        var b=in.read();
                        if (b==27)
                        {
                            System.out.println("ESC");
                        }
                        if (b==-1)
                        {
                            break;
                        }
                        if (b=='\n')
                        {
                            consumer.accept(new String(vector.toByteArray(),"GBK"));
                            vector.clear();
                        }
                        else
                        {
                            vector.add((byte) b);
                        }
                    }
                    catch (Throwable e)
                    {
                        UI.putError("监听服务器输出时出现错误,可能导致无法监听",e);
                    }
                }
            }
        };
    }

    public static void restart()
    {
        ServerState.Closed.addTask(ThreadManager.restartThread());
        stop();
    }

    public static void stop()
    {
        synchronized (Main.class)
        {
            if (process.isAlive())
            {
                try
                {
                    useCommand("stop");
                    ServerState.state(ServerState.Closing);
                }
                catch (Throwable e)
                {
                    UI.putError("尝试关闭服务器 但关闭失败",e);
                }
            }
        }
    }

    public static void useCommand(String command)
    {
        if (process==null)
        {
            UI.putError("无法执行命令 服务器未启动");
            return;
        }
        try
        {
            in.write((command+"\r\n").getBytes());
            in.flush();
        }
        catch (Throwable e)
        {
            UI.putError("无法执行命令:",e);
        }
    }

    public static File data()
    {
        return new File(dataDir(),"BluestarServerManager.data");
    }

    public static File dataDir()
    {
        File f=new File(new File(dir),"data");
        try
        {
            f.mkdirs();
        }
        catch (Throwable e)
        {
            UI.putError("创建data文件夹失败",e);
        }
        return f;
    }
}