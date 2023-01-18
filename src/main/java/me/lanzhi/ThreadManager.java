package me.lanzhi;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ThreadManager
{
    private static Thread restartThread=new Thread(Main::start);

    private static Map<String,Thread> tasks=new HashMap<>();

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

    public static void addTask(Runnable task,String name)
    {
        if (Objects.isNull(task))
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
                    UI.putError("Task \""+name+"\" failed to run",throwable);
                }
                System.out.println("Task \""+name+"\" finished");
                tasks.values().remove(this);
            }
        };
        synchronized (ThreadManager.class)
        {
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

    public static boolean hasTask(String name)
    {
        synchronized (ThreadManager.class)
        {
            return tasks.containsKey(name);
        }
    }
}
