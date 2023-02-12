package me.nullaqua;

import me.nullaqua.ui.ColorPane;
import me.nullaqua.ui.ConfigPaneDrawer;
import me.nullaqua.ui.DownloadPaneDrawer;
import me.nullaqua.ui.LimDocument;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class UI
{
    //输入框
    public static JTextField input;
    //输出框
    public static ColorPane output;
    //启动按钮
    public static JButton start;
    //停止按钮
    public static JButton stop;
    //重启按钮
    public static JButton restart;
    //标签页
    public static JTabbedPane tabbedPane;
    //窗口
    public static JFrame frame;

    //putMessages
    //状态栏
    public static JLabel status=new JLabel("状态：未启动");

    //提示图标
    private static final BufferedImage tipIcon;

    //添加外观面板
    public static void drawLookAndFeelPanel()
    {
        ActionListener listener;
        //外观面板
        var lookAndFeelPanel=new JPanel();
        lookAndFeelPanel.setLayout(new BoxLayout(lookAndFeelPanel,BoxLayout.Y_AXIS));
        //外观列表
        var lookAndFeelList=new JList<>(getLookAndFeelNameList());
        //外观列表滚动面板
        var lookAndFeelListScrollPane=new JScrollPane(lookAndFeelList);
        lookAndFeelPanel.add(lookAndFeelListScrollPane);
        //外观按钮面板
        var lookAndFeelButtonPanel=new JPanel();
        lookAndFeelButtonPanel.setLayout(new BoxLayout(lookAndFeelButtonPanel,BoxLayout.X_AXIS));
        //设置外观按钮
        var setLookAndFeelButton=new JButton("设置外观");
        listener=e->
        {
            var index=lookAndFeelList.getSelectedIndex();
            if (index!=-1)
            {
                try
                {
                    Utils.getUnsaved().lookAndFeel=getLookAndFeelList()[index];
                    Utils.getInstance().lookAndFeel=getLookAndFeelList()[index];
                    Utils.save(false);
                }
                catch (Throwable e1)
                {
                    UI.putError(e1);
                }
            }
        };
        setLookAndFeelButton.addActionListener(listener);
        lookAndFeelButtonPanel.add(setLookAndFeelButton);
        lookAndFeelPanel.add(lookAndFeelButtonPanel);
        //在主面板中添加外观面板
        tabbedPane.addTab("外观",lookAndFeelPanel);
        output.repaint();
        //output.getGraphics().setColor(Color.BLACK);
    }

    //lookAndFeelList

    public static String[] getLookAndFeelNameList()
    {
        List<String> tempList=new ArrayList<>();
        for (var x: getLookAndFeelList())
        {
            var _1=x.lastIndexOf('.');
            var name=x.substring(_1+1);
            tempList.add(name);
        }
        return tempList.toArray(new String[0]);
    }

    public static String[] getLookAndFeelList()
    {
        var tempList=new ArrayList<String>();
        tempList.add(com.github.weisj.darklaf.theme.DarculaTheme.class.getName());
        tempList.add(com.github.weisj.darklaf.theme.IntelliJTheme.class.getName());
        tempList.add(com.github.weisj.darklaf.theme.HighContrastDarkTheme.class.getName());
        tempList.add(com.github.weisj.darklaf.theme.HighContrastLightTheme.class.getName());
        tempList.add(com.github.weisj.darklaf.theme.OneDarkTheme.class.getName());
        tempList.add(com.github.weisj.darklaf.theme.SolarizedDarkTheme.class.getName());
        tempList.add(com.github.weisj.darklaf.theme.SolarizedLightTheme.class.getName());
        for (var x: UIManager.getInstalledLookAndFeels())
        {
            tempList.add(x.getClassName());
        }
        tempList.add(com.jtattoo.plaf.fast.FastLookAndFeel.class.getName());
        tempList.add(com.jtattoo.plaf.acryl.AcrylLookAndFeel.class.getName());
        tempList.add(com.jtattoo.plaf.aero.AeroLookAndFeel.class.getName());
        tempList.add(com.jtattoo.plaf.aluminium.AluminiumLookAndFeel.class.getName());
        tempList.add(com.jtattoo.plaf.bernstein.BernsteinLookAndFeel.class.getName());
        tempList.add(com.jtattoo.plaf.fast.FastLookAndFeel.class.getName());
        tempList.add(com.jtattoo.plaf.graphite.GraphiteLookAndFeel.class.getName());
        tempList.add(com.jtattoo.plaf.hifi.HiFiLookAndFeel.class.getName());
        tempList.add(com.jtattoo.plaf.luna.LunaLookAndFeel.class.getName());
        tempList.add(com.jtattoo.plaf.mcwin.McWinLookAndFeel.class.getName());
        tempList.add(com.jtattoo.plaf.mint.MintLookAndFeel.class.getName());
        tempList.add(com.jtattoo.plaf.noire.NoireLookAndFeel.class.getName());
        tempList.add(com.jtattoo.plaf.smart.SmartLookAndFeel.class.getName());
        tempList.add(com.jtattoo.plaf.texture.TextureLookAndFeel.class.getName());

        return tempList.toArray(new String[0]);
    }

    public static void putError(Throwable e)
    {
        putError(e.getMessage());
        e.printStackTrace();
    }

    public static void putError(String error)
    {
        //弹出错误对话框
        JOptionPane.showMessageDialog(frame,error,"错误",JOptionPane.ERROR_MESSAGE);
    }

    public static void putError()
    {
        putError("未知错误");
    }

    public static void putError(String message,Throwable e)
    {
        putError(message+"\n"+e.getMessage());
        e.printStackTrace();
    }

    public static void putInfo(String info)
    {
        //弹出信息对话框
        JOptionPane.showMessageDialog(frame,info,"信息",JOptionPane.INFORMATION_MESSAGE);
    }

    //------icon------//
    //图标字体
    private static final Font iconFont;
    //下载图标
    private static final BufferedImage downloadIcon;

    static
    {
        Font font=null;
        try
        {
            font=Font.createFont(0,UI.class.getResourceAsStream("resources/iconFont.ttf")).deriveFont(15f);
        }
        catch (Throwable e)
        {
            UI.putError("加载图标字体失败",e);
        }
        iconFont=font;
        //提示图标是字体文件的字符U+E783
        tipIcon=new BufferedImage(32,32,BufferedImage.TYPE_INT_ARGB);
        var g=tipIcon.getGraphics();
        g.setColor(Color.CYAN);
        assert iconFont!=null;
        g.setFont(iconFont.deriveFont(32f));
        g.drawString("\uE783",0,0);
    }

    static
    {
        downloadIcon=new BufferedImage(32,32,BufferedImage.TYPE_INT_ARGB);
        var g=downloadIcon.getGraphics();
        g.setColor(Color.CYAN);
        int[] xPoints=new int[]{12,19,19,24,16,15,6,12};
        int[] yPoints=new int[]{5,5,21,21,28,28,21,21};
        g.fillPolygon(xPoints,yPoints,8);
    }

    public static void init()
    {
        frame=new JFrame("控制台");
        input=new JTextField();
        //监听输入框回车事件
        input.addActionListener(e->
                                {
                                    Main.useCommand(input.getText());
                                    input.setText("");
                                });
        //监听输入tab事件
        input.addKeyListener(new KeyListener()
        {
            @Override
            public void keyTyped(KeyEvent e)
            {
            }
            @Override
            public void keyPressed(KeyEvent e)
            {
                if (e.getKeyCode()==KeyEvent.VK_TAB)
                {
                    String text=input.getText();
                    Main.useCommand(text+"\t");
                }
            }

            @Override
            public void keyReleased(KeyEvent e)
            {
            }
        });
        //初始化输出框
        output=new ColorPane();
        output.setStyledDocument(new LimDocument(output,200));
        output.setText("\n".repeat(100));
        //设置输出框不可编辑
        output.setEditable(false);
        //初始化启动按钮
        start=new JButton("启动");
        start.setEnabled(true);
        //监听启动按钮点击事件
        start.addActionListener(e->new Thread(Main::start).start());
        //初始化停止按钮
        stop=new JButton("停止");
        stop.setEnabled(false);
        //监听停止按钮点击事件
        stop.addActionListener(e->new Thread(Main::stop).start());
        //初始化重启按钮
        restart=new JButton("重启");
        restart.setEnabled(false);
        //监听重启按钮点击事件
        restart.addActionListener(e->new Thread(Main::restart).start());
        //初始化标签页
        tabbedPane=new JTabbedPane();
        //创建控制台标签页
        var console=new JPanel();
        //上面是输出框，下面是输入框,中间是启动和停止按钮。输入框占一行，按钮占一行，输出框占剩下的行
        console.setLayout(new BoxLayout(console,BoxLayout.Y_AXIS));
        console.add(new JScrollPane(output));
        //创建按钮面板
        var buttonPanel=new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel,BoxLayout.X_AXIS));
        //居左对齐
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        //按钮面板添加启动和停止按钮
        buttonPanel.add(start);
        buttonPanel.add(stop);
        buttonPanel.add(restart);
        buttonPanel.add(status);
        //添加窗口置顶按钮
        var top=new JCheckBox("窗口置顶");
        top.addActionListener(e->frame.setAlwaysOnTop(top.isSelected()));
        buttonPanel.add(top);
        console.add(buttonPanel);
        console.add(input);
        //自动重启
        var autoRestart=new JCheckBox("自动重启");
        autoRestart.addActionListener(e->ThreadManager.restart(autoRestart.isSelected()));
        buttonPanel.add(autoRestart);
        //添加控制台标签页
        tabbedPane.addTab("控制台",console);
        //设置窗口大小
        frame.setSize(800,600);
        frame.setMinimumSize(new Dimension(500,400));
        //窗口居中
        frame.setLocationRelativeTo(null);
        //设置窗口关闭事件
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        //监听窗口关闭事件
        frame.addWindowListener(new java.awt.event.WindowAdapter()
        {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent)
            {
                if (ServerState.state()==ServerState.Closed)
                {
                    System.exit(0);
                }
                else
                {
                    Thread t=new Thread(()->System.exit(0));
                    t.setName("CloseThread");
                    ServerState.Closed.addTask(t);
                    var x=JOptionPane.showOptionDialog(frame,
                                                       "服务器运行中,请先关闭服务器",
                                                       "关闭服务器",
                                                       JOptionPane.YES_NO_OPTION,
                                                       JOptionPane.QUESTION_MESSAGE,
                                                       null,
                                                       new String[]{"确定","强制关闭"},
                                                       null);
                    //如果点击了"强制关闭"按钮
                    if (x!=1)
                    {
                        ServerState.Closed.removeTask(t);
                        return;
                    }
                    x=JOptionPane.showOptionDialog(frame,
                                                   "你确定要强制关闭服务器吗?\n强制关闭服务器可能引起未知错误",
                                                   "强制关闭服务器",
                                                   JOptionPane.YES_NO_OPTION,
                                                   JOptionPane.ERROR_MESSAGE,
                                                   null,
                                                   new String[]{"取消","确定"},
                                                   null);
                    if (x!=1)
                    {
                        ServerState.Closed.removeTask(t);
                        return;
                    }

                    Main.process.destroy();
                }
            }
        });
        //设置窗口内容
        frame.setContentPane(tabbedPane);

        Utils.load();
        ConfigPaneDrawer.draw();
        tabbedPane.add("下载",DownloadPaneDrawer.create());
        drawLookAndFeelPanel();


        //显示窗口
        frame.setVisible(true);
    }

    public static void downloadIcon(BooleanSupplier x,Consumer<Image> setIcon)
    {
        moveImage(downloadIcon,0,1,50,x,setIcon);
    }

    public static void moveImage(BufferedImage image,int dx,int dy,int sleep,BooleanSupplier run,Consumer<Image> getter)
    {
        Runnable task=()->
        {
            int x=0, y=0;
            int xz=image.getWidth();
            int yz=image.getHeight();
            while (run.getAsBoolean())
            {
                var image2=new BufferedImage(xz,yz,BufferedImage.TYPE_INT_ARGB);
                for (int i=0;i<xz;i++)
                {
                    for (int j=0;j<yz;j++)
                    {
                        image2.setRGB((i+x)%xz,(j+y)%yz,image.getRGB(i,j));
                    }
                }
                getter.accept(image2);
                x+=dx;
                y+=dy;
                try
                {
                    Thread.sleep(sleep);
                }
                catch (Throwable ignored)
                {
                }
            }
        };
        ThreadManager.addTask(task,"updateIcon-"+UUID.randomUUID());
    }

    public static Font iconFont()
    {
        return iconFont;
    }

    public static BufferedImage tipImage()
    {
        return tipIcon;
    }
}
