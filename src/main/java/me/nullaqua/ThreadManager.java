package me.nullaqua;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class ThreadManager
{
    private static Map<String,Thread> tasks=new HashMap<>();
    private static Thread restartThread=new Thread(Main::start);

    static
    {
        restartThread.setName("RestartThread");
    }

    public static Thread restartThread()
    {
        return restartThread;
    }

    public static void restartThread(Thread restartThread)
    {
        if (!Objects.isNull(restartThread))
        {
            ThreadManager.restartThread=restartThread;
            restartThread.setName("RestartThread");
            restart(false);
            restart(true);
        }
    }

    public static void restart(boolean b)
    {
        if (b)
        {
            ServerState.Closed.addLongTask(restartThread);
        }
        else
        {
            ServerState.Closed.removeLongTask(restartThread);
        }
    }

    public static boolean hasTask(String name)
    {
        synchronized (ThreadManager.class)
        {
            return tasks.containsKey(name);
        }
    }

    public static void addTask(Runnable task,String name)
    {
        if (Objects.isNull(task))
        {
            return;
        }
        synchronized (ThreadManager.class)
        {
            var x=tasks.get(name);
            if (Objects.nonNull(x))
            {
                return;
            }
            Thread thread=new Thread()
            {
                @Override
                public void run()
                {
                    try
                    {
                        task.run();
                    }
                    catch (Throwable throwable)
                    {
                        putError("Task \""+name+"\" failed to run",throwable);
                    }
                    System.out.println("Task \""+name+"\" finished");
                    tasks.values().remove(this);
                }
            };
            thread.setName(name);
            tasks.put(name,thread);
            runThread(thread);
        }
    }

    public static void runThread(Thread thread)
    {
        if (Objects.isNull(thread))
        {
            return;
        }
        if (thread.isAlive())
        {
            System.out.println("Trying to start \""+thread.getName()+"\" but it's already running.");
            return;
        }
        System.out.println("Starting thread "+thread.getName());
        try
        {
            thread.start();
        }
        catch (Throwable e)
        {
            UI.putError("Thread \""+thread.getName()+"\" failed to start",e);
            e.printStackTrace();
        }
    }

    //------show dialog in new thread------//

    public static void putError(Throwable e)
    {
        putNamedError(e,"Error:"+e.getClass().getName()+"@"+e.hashCode()+"_"+UUID.randomUUID());
    }

    public static void putError()
    {
        putNamedError("Error:Unnamed_"+UUID.randomUUID());
    }

    public static void putError(String error)
    {
        var x=error.replaceAll("\r","");
        x=x.replaceAll("\n"," ");
        x=x.replaceAll("_"," ");
        putNamedError(error,"Error:"+x+"_"+UUID.randomUUID());
    }

    public static void putError(String message,Throwable e)
    {
        var x=message.replaceAll("\r","");
        x=x.replaceAll("\n"," ");
        x=x.replaceAll("_"," ");
        putNamedError(message,e,"Error:"+e.getClass().getName()+"@"+e.hashCode()+"_"+x+"_"+UUID.randomUUID());
    }

    public static void putInfo(String info)
    {
        var x=info.replaceAll("\r","");
        x=x.replaceAll("\n"," ");
        x=x.replaceAll("_"," ");
        putNamedInfo(info,"Info:"+x+"_"+UUID.randomUUID());
    }

    //------with name------//

    public static void putNamedError(Throwable e,String name)
    {
        if (name==null||name.isEmpty())
        {
            putError(e);
            return;
        }
        addTask(()->UI.putError(e),"Error:"+name);
    }

    public static void putNamedError(String name)
    {
        if (name==null||name.isEmpty())
        {
            putError();
            return;
        }
        addTask(UI::putError,"Error:"+name);
    }

    public static void putNamedError(String error,String name)
    {
        if (name==null||name.isEmpty())
        {
            putError(error);
            return;
        }
        addTask(()->UI.putError(error),"Error:"+name);
    }

    public static void putNamedError(String message,Throwable e,String name)
    {
        if (name==null||name.isEmpty())
        {
            putError(message,e);
            return;
        }
        addTask(()->UI.putError(message,e),"Error:"+name);
    }

    public static void putNamedInfo(String info,String name)
    {
        if (name==null||name.isEmpty())
        {
            putInfo(info);
            return;
        }
        addTask(()->UI.putInfo(info),"Info:"+name);
    }
}
