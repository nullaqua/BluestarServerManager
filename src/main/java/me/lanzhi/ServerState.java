package me.lanzhi;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public enum ServerState
{
    Running,
    Closed,
    Closing,
    Error;
    private static ServerState state=Closed;
    private Set<Thread> tasks=new HashSet<>();
    //长期任务
    private Set<Thread> longTasks=new HashSet<>();

    public static ServerState state()
    {
        return state;
    }

    public static void state(ServerState state)
    {
        if (Objects.isNull(state)||state==ServerState.state)
        {
            return;
        }
        ServerState.state=state;
        if (state==ServerState.Running)
        {
            System.out.println("Server started.");
            UI.status.setText("状态：运行中");
            UI.stop.setEnabled(true);
            UI.start.setEnabled(false);
            UI.restart.setEnabled(true);
        }
        else if (state==ServerState.Closed)
        {
            System.out.println("Server closed.");
            UI.status.setText("状态：未启动");
            UI.stop.setEnabled(false);
            UI.start.setEnabled(true);
            UI.restart.setEnabled(false);
        }
        else if (state==ServerState.Closing)
        {
            System.out.println("Server closing.");
            UI.status.setText("状态：正在关闭");
            UI.stop.setEnabled(false);
            UI.start.setEnabled(false);
            UI.restart.setEnabled(false);
        }
        else if (state==ServerState.Error)
        {
            System.out.println("Server error.");
            UI.status.setText("状态：错误,请尝试重启软件或联系开发者");
            UI.stop.setEnabled(false);
            UI.start.setEnabled(false);
            UI.restart.setEnabled(false);
        }
        for (var task: state.longTasks)
        {
            ThreadManager.runThread(task);
        }
        for (var task: state.tasks)
        {
            ThreadManager.runThread(task);
        }
        state.tasks.clear();
    }

    public void addTask(Thread task)
    {
        addTask(this,task);
    }

    public static void addTask(ServerState state,Thread task)
    {
        if (Objects.isNull(state)||Objects.isNull(task))
        {
            return;
        }
        if (state==ServerState.state)
        {
            task.start();
            return;
        }
        state.tasks.add(task);
    }

    public void removeTask(Thread task)
    {
        removeTask(this,task);
    }

    public static void removeTask(ServerState state,Thread task)
    {
        state.tasks.remove(task);
    }

    public void addLongTask(Thread task)
    {
        addLongTask(this,task);
    }

    public static void addLongTask(ServerState state,Thread task)
    {
        if (Objects.isNull(state)||Objects.isNull(task))
        {
            return;
        }
        state.longTasks.add(task);
    }

    public void removeLongTask(Thread task)
    {
        removeLongTask(this,task);
    }

    public static void removeLongTask(ServerState state,Thread task)
    {
        if (Objects.isNull(state)||Objects.isNull(task))
        {
            return;
        }
        state.longTasks.remove(task);
    }
}
