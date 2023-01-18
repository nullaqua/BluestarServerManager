package me.lanzhi.ui;

import me.lanzhi.Main;
import me.lanzhi.ThreadManager;
import me.lanzhi.UI;
import me.lanzhi.api.awt.BluestarLayout;
import me.lanzhi.api.awt.BluestarLayoutData;
import me.lanzhi.api.net.dowloader.Downloader;
import me.lanzhi.api.net.dowloader.MultiThreadDownload;
import me.lanzhi.api.util.collection.FastLinkedList;
import me.lanzhi.api.util.function.Run;
import me.lanzhi.net.NetManager;
import me.lanzhi.net.ServerCoreGetter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DownloadPaneDrawer
{
    private static final int blank=new JLabel("一一一一一一").getPreferredSize().width;
    private static final JTextField url=new JTextField();
    private static final List<JButton> allDownloadButton=new FastLinkedList<>();
    private static final JButton downloadButton=new JButton("下载");
    private static ActionListener download;

    public static JPanel create()
    {
        ActionListener listener;
        JPanel panel=new JPanel();
        panel.setLayout(new BluestarLayout());
        //第一行为下载链接输入框
        var data=new BluestarLayoutData(3,11,0,0,1,1,new Insets(0,5,0,0));
        data.setTransverseAlignment(BluestarLayoutData.FRONT);
        data.setPortraitAlignment(BluestarLayoutData.CENTER);
        panel.add(new JLabel("下载链接"),data);
        data=new BluestarLayoutData(3,11,2,0,1,1,new Insets(0,0,0,5));
        data.setTransverseAlignment(BluestarLayoutData.BACK);
        data.setPortraitAlignment(BluestarLayoutData.CENTER);
        allDownloadButton.add(downloadButton);
        panel.add(downloadButton,data);
        Insets insets=new Insets(0,blank,0,blank);
        data=new BluestarLayoutData(1,11,0,0,1,1,insets);
        data.setTransverseAlignment(BluestarLayoutData.FILL);
        data.setPortraitAlignment(BluestarLayoutData.CENTER);
        panel.add(url,data);
        //第二行为保存路径选择框
        data=new BluestarLayoutData(3,11,0,1,1,1,new Insets(0,5,0,0));
        data.setTransverseAlignment(BluestarLayoutData.FRONT);
        data.setPortraitAlignment(BluestarLayoutData.CENTER);
        panel.add(new JLabel("保存路径"),data);
        data=new BluestarLayoutData(3,11,2,1,1,1,new Insets(0,0,0,5));
        data.setTransverseAlignment(BluestarLayoutData.BACK);
        data.setPortraitAlignment(BluestarLayoutData.CENTER);
        JButton chooseButton=new JButton("选择");
        panel.add(chooseButton,data);
        data=new BluestarLayoutData(1,11,0,1,1,1,insets);
        data.setTransverseAlignment(BluestarLayoutData.FILL);
        data.setPortraitAlignment(BluestarLayoutData.CENTER);
        JTextField path=new JTextField();
        path.setText(new File(Main.dataDir(),"download").getAbsolutePath());
        path.setEditable(false);
        panel.add(path,data);
        listener=e->
        {
            JFileChooser chooser=new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.showDialog(new JLabel(),"选择");
            File file=chooser.getSelectedFile();
            if (file!=null)
            {
                path.setText(file.getAbsolutePath());
            }
        };
        chooseButton.addActionListener(listener);

        //第三行为下载线程数选择框
        data=new BluestarLayoutData(3,11,0,2,1,1,new Insets(0,5,0,0));
        data.setTransverseAlignment(BluestarLayoutData.FRONT);
        data.setPortraitAlignment(BluestarLayoutData.CENTER);
        panel.add(new JLabel("线程数"),data);
        data=new BluestarLayoutData(1,11,0,2,1,1,insets);
        data.setTransverseAlignment(BluestarLayoutData.FRONT);
        data.setPortraitAlignment(BluestarLayoutData.CENTER);
        JComboBox<Integer> threadCount=new JComboBox<>(new Integer[]{1,2,4,8,16,32,64});
        panel.add(threadCount,data);
        //线程数右侧为提示标记
        //鼠标悬停时显示提示信息
        var temp=new Insets(0,insets.left+threadCount.getPreferredSize().width+5,0,0);
        data=new BluestarLayoutData(1,11,0,2,1,1,temp);
        data.setTransverseAlignment(BluestarLayoutData.FRONT);
        data.setPortraitAlignment(BluestarLayoutData.CENTER);
        //提示图标,是个图片
        //设置为字符 U+E784
        JLabel tip=new JLabel(" \uE783");//
        tip.setFont(UI.iconFont());
        String tipText="""
                <html>
                建议线程数量:16<br>
                线程数越多,会占用更多的系统资源,一般情况下速度也会越快,但是下载失败的几率也会越大<br>
                如果服务器对多线程下载支持不好,可能会导致反倒没有单线程快如果无法进行多线程下载,会自动降级为单线程下载""";
        tip.setToolTipText(tipText);
        panel.add(tip,data);

        download=e->
        {
            allDownloadButton.forEach(x->x.setEnabled(false));
            String urlText=url.getText();
            String pathText=path.getText();
            var o=threadCount.getSelectedItem();
            if (!(o instanceof Integer threadCountValue))
            {
                JOptionPane.showMessageDialog(panel,"线程数选择错误","错误",JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (urlText.isEmpty())
            {
                JOptionPane.showMessageDialog(panel,"下载链接不能为空","错误",JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (pathText.isEmpty())
            {
                JOptionPane.showMessageDialog(panel,"保存路径不能为空","错误",JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (threadCountValue<=0)
            {
                JOptionPane.showMessageDialog(panel,"线程数不能小于1","错误",JOptionPane.ERROR_MESSAGE);
                return;
            }
            Runnable task=()->
            {
                try
                {
                    showDownload(Downloader.downloadToDir(urlText,pathText,threadCountValue));
                }
                catch (Throwable throwable)
                {
                    UI.putError("下载失败",throwable);
                }
                finally
                {
                    allDownloadButton.forEach(x->x.setEnabled(true));
                }
            };
            try
            {

                ThreadManager.addTask(task,"startDownload-"+urlText);
            }
            catch (Throwable throwable)
            {
                UI.putError("下载失败",throwable);
                downloadButton.setEnabled(true);
            }
        };
        downloadButton.addActionListener(download);

        //快速下载列表
        data=new BluestarLayoutData(1,11,0,3,1,5,insets);
        data.setTransverseAlignment(BluestarLayoutData.FILL);
        data.setPortraitAlignment(BluestarLayoutData.FILL);
        //滚动列表
        var scroll=new JScrollPane();
        //scroll.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        panel.add(scroll,data);
        //列表
        //列表每一行最左边为logo,中间为名称,最右边为下载按钮
        var list=new JPanel();
        scroll.setViewportView(list);
        //在提示的右侧为版本输入框
        data=new BluestarLayoutData(3,11,1,2,1,1,new Insets(0,0,0,0));
        data.setTransverseAlignment(BluestarLayoutData.FRONT);
        data.setPortraitAlignment(BluestarLayoutData.CENTER);
        panel.add(new JLabel("版本"),data);
        data=new BluestarLayoutData(3,11,1,2,1,1,new Insets(0,25,0,15));
        data.setTransverseAlignment(BluestarLayoutData.FILL);
        data.setPortraitAlignment(BluestarLayoutData.CENTER);
        JTextField version=new JTextField();
        panel.add(version,data);

        tip=new JLabel(" \uE783");//
        tip.setFont(UI.iconFont());
        tipText="""
                <html>
                我的世界版本,会在下方的列表中显示不同核心对应版本的下载按钮<br>
                如果某个核心没有对应版本,则不会显示<br>
                注: 部分官网可能因为网络从而无法下载,如有需要请尝试科学上网""";
        tip.setToolTipText(tipText);
        data=new BluestarLayoutData(3,11,1,2,1,1);
        data.setTransverseAlignment(BluestarLayoutData.BACK);
        data.setPortraitAlignment(BluestarLayoutData.CENTER);
        panel.add(tip,data);

        listener=e->
        {
            if (ThreadManager.hasTask("drawServerCores"))
            {
                return;
            }
            ThreadManager.addTask(new ServerCoresDrawer(list,()->download.actionPerformed(null),version.getText()),
                                  "drawServerCores");
        };
        version.addActionListener(listener);
        version.setText("1.19.3");
        listener.actionPerformed(null);

        //java下载
        //第一行为版本选择框,系统选择框,打包选择框,下载按钮
        data=new BluestarLayoutData(5,11,0,8,1,1);
        data.setTransverseAlignment(BluestarLayoutData.FILL);
        data.setPortraitAlignment(BluestarLayoutData.FILL);
        panel.add(new JLabel("Java"),data);
        data=new BluestarLayoutData(5,11,1,8,1,1);
        data.setTransverseAlignment(BluestarLayoutData.FILL);
        data.setPortraitAlignment(BluestarLayoutData.FILL);
        JComboBox<Integer> javaVersion=new JComboBox<>(new Integer[]{1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19});
        panel.add(javaVersion,data);
        data=new BluestarLayoutData(5,11,2,8,1,1);
        data.setTransverseAlignment(BluestarLayoutData.FILL);
        data.setPortraitAlignment(BluestarLayoutData.FILL);
        JComboBox<String> javaSystem=new JComboBox<>(new String[]{"Windows","Linux","MacOS","ArmLinux","ArmMacOS"});
        panel.add(javaSystem,data);
        data=new BluestarLayoutData(5,11,3,8,1,1);
        data.setTransverseAlignment(BluestarLayoutData.FILL);
        data.setPortraitAlignment(BluestarLayoutData.FILL);
        JComboBox<String> javaPack=new JComboBox<>(new String[]{});
        panel.add(javaPack,data);
        data=new BluestarLayoutData(5,11,4,8,1,1);
        data.setTransverseAlignment(BluestarLayoutData.FILL);
        data.setPortraitAlignment(BluestarLayoutData.FILL);
        JButton javaDownload=new JButton("下载");
        panel.add(javaDownload,data);
        allDownloadButton.add(javaDownload);

        //监听系统选择框和版本选择框
        ActionListener javaListener=e->updateJavaPack(javaSystem,javaPack,javaVersion,javaDownload);
        javaSystem.setSelectedIndex(0);
        javaVersion.setSelectedIndex(0);
        javaSystem.addActionListener(javaListener);
        javaVersion.addActionListener(javaListener);
        javaListener.actionPerformed(null);
        return panel;
    }

    private static void showDownload(Downloader downloader)
    {
        //绘制下载界面
        JFrame frame=new JFrame("下载");
        //设置窗口大小
        frame.setSize(500,300);
        //设置窗口位置
        frame.setLocationRelativeTo(null);
        //窗口最小大小
        frame.setMinimumSize(new Dimension(300,300));
        //设置窗口关闭时的操作
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        //设置窗口的布局
        frame.setLayout(new BluestarLayout());
        //设置窗口的内容面板
        frame.setContentPane(createDownloadPanel(downloader,frame));
        //设置窗口可见
        frame.setVisible(true);
        UI.downloadIcon(frame::isVisible,frame::setIconImage);
    }

    private static void updateJavaPack(JComboBox<String> sys,JComboBox<String> pack,JComboBox<Integer> ver,
                                       JButton downloadButton)
    {
        pack.removeAllItems();
        switch (sys.getItemAt(sys.getSelectedIndex()))
        {
            case "Windows" ->
            {
                pack.addItem("zip");
                pack.addItem("msi");
                pack.addItem("exe");
            }
            case "Linux" ->
            {
                pack.addItem("tar.gz");
                pack.addItem("rpm");
                pack.addItem("deb");
            }
            case "ArmLinux" ->
            {
                pack.addItem("tar.gz");
                pack.addItem("rpm");
            }
            case "MacOS","ArmMacOS" ->
            {
                pack.addItem("tar.gz");
                pack.addItem("dmg");
            }
        }
        if (pack.getItemCount()>0)
        {
            pack.setSelectedIndex(0);
        }
        ActionListener listener=e->
        {
            int version=ver.getItemAt(ver.getSelectedIndex());
            String system=sys.getItemAt(sys.getSelectedIndex());
            String packType=pack.getItemAt(pack.getSelectedIndex());
            String systemName=switch (system)
                    {
                        case "Linux" -> "linux-x64";
                        case "MacOS" -> "macos-x64";
                        case "ArmLinux" -> "linux-aarch64";
                        case "ArmMacOS" -> "macos-aarch64";
                        default -> "windows_x64";
                    };
            //if (version>11)
            {
                url.setText("https://download.oracle.com/java/"+
                            version+
                            "/latest/jdk-"+
                            version+
                            "_"+
                            systemName+
                            "_bin."+
                            packType);
                download.actionPerformed(null);
            }
        };
        downloadButton.addActionListener(listener);
    }

    private static JPanel createDownloadPanel(Downloader downloader,JFrame frame)
    {
        ActionListener listener;
        JPanel panel=new JPanel();
        panel.setLayout(new BluestarLayout());
        //第一行为下载进度条,下载进度条有若干个小进度条组成
        int threadCount=(downloader instanceof MultiThreadDownload.MultiThreadDownloader x)?x.totalThread():1;
        JProgressBar[] progressBars=new JProgressBar[threadCount];
        for (int i=0;i<threadCount;i++)
        {
            progressBars[i]=new JProgressBar();
            progressBars[i].setStringPainted(true);
            progressBars[i].setString("0%");
        }
        //设置下载进度条的布局,横向排列,每个小进度条占据1/线程数的宽度
        BluestarLayoutData data;
        for (int i=0;i<threadCount;i++)
        {
            data=new BluestarLayoutData(threadCount,11,i,0,1,1,new Insets(0,0,0,0));
            data.setTransverseAlignment(BluestarLayoutData.FILL);
            data.setPortraitAlignment(BluestarLayoutData.CENTER);
            panel.add(progressBars[i],data);
        }
        //第二行为下载速度
        data=new BluestarLayoutData(1,11,0,1,1,1,new Insets(0,5,0,5));
        data.setTransverseAlignment(BluestarLayoutData.FILL);
        data.setPortraitAlignment(BluestarLayoutData.CENTER);
        JLabel speed=new JLabel();
        panel.add(speed,data);
        //第三行为暂停/继续按钮和取消按钮
        data=new BluestarLayoutData(2,11,0,2,1,1,new Insets(0,5,0,5));
        data.setTransverseAlignment(BluestarLayoutData.FILL);
        data.setPortraitAlignment(BluestarLayoutData.CENTER);
        JButton pauseButton=new JButton("暂停");
        JButton cancelButton=new JButton("取消");
        panel.add(pauseButton,data);
        data=new BluestarLayoutData(2,11,1,2,1,1,new Insets(0,5,0,5));
        data.setTransverseAlignment(BluestarLayoutData.FILL);
        data.setPortraitAlignment(BluestarLayoutData.CENTER);
        panel.add(cancelButton,data);

        //第四行为打开文件夹按钮
        data=new BluestarLayoutData(1,11,0,4,1,1,new Insets(0,5,0,5));
        data.setTransverseAlignment(BluestarLayoutData.FILL);
        data.setPortraitAlignment(BluestarLayoutData.CENTER);
        JButton openButton=new JButton("打开文件夹");
        panel.add(openButton,data);

        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        frame.addWindowListener(new java.awt.event.WindowAdapter()
        {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent)
            {
                int result=JOptionPane.showConfirmDialog(frame,
                                                         "下载未完成,确定要退出吗?",
                                                         "提示",
                                                         JOptionPane.YES_NO_OPTION);
                if (result==JOptionPane.YES_OPTION)
                {
                    downloader.cancel();
                }
            }
        });

        //取消按钮的监听器
        listener=e->
        {
            int result=JOptionPane.showConfirmDialog(frame,"下载未完成,确定要退出吗?","提示",JOptionPane.YES_NO_OPTION);
            if (result==JOptionPane.YES_OPTION)
            {
                downloader.cancel();
            }
        };
        cancelButton.addActionListener(listener);

        //暂停/继续按钮的监听器
        listener=e->
        {
            if (downloader.pause())
            {
                downloader.pause(false);
                pauseButton.setText("暂停");
            }
            else
            {
                downloader.pause(true);
                pauseButton.setText("继续");
            }
        };
        pauseButton.addActionListener(listener);

        //打开文件夹按钮的监听器
        listener=e->
        {
            try
            {
                //打开文件夹并选中文件
                Desktop.getDesktop().open(new File(downloader.file()).getParentFile());
            }
            catch (Throwable e1)
            {
                UI.putError("打开文件夹失败",e1);
            }
        };
        openButton.addActionListener(listener);

        //下载器的监听器
        Runnable thread=()->
        {
            try
            {
                //更新下载进度
                while (downloader.running()||downloader.pause())
                {
                    if (downloader instanceof MultiThreadDownload.MultiThreadDownloader downloader_)
                    {
                        for (int i=0;i<threadCount;i++)
                        {
                            progressBars[i].setMaximum(Integer.MAX_VALUE);
                            progressBars[i].setValue((int) (downloader_.progress(i)*Integer.MAX_VALUE));
                            progressBars[i].setString(String.format("%.2f%%",downloader_.progressPercent(i)));
                        }
                    }
                    else
                    {
                        progressBars[0].setMaximum(Integer.MAX_VALUE);
                        progressBars[0].setValue((int) (downloader.progress()*Integer.MAX_VALUE));
                        progressBars[0].setString(String.format("%.2f%%",downloader.progressPercent()));
                    }
                    if (downloader.running())
                    {
                        speed.setText("已下载:"+
                                      downloader.currentSize()+
                                      "/"+
                                      downloader.totalSize()+
                                      " 速度:"+
                                      downloader.speed()+
                                      " 预计剩余时间:"+
                                      downloader.remainingTime());
                        frame.setTitle(String.format("下载-%.2f%%-%s",downloader.progressPercent(),downloader.speed()));
                    }
                    else
                    {
                        speed.setText("已下载:"+downloader.currentSize()+"/"+downloader.totalSize()+" 已暂停");
                        frame.setTitle(String.format("下载-%.2f%%-%s",downloader.progressPercent(),"已暂停"));
                    }
                    Thread.sleep(100);
                }
            }
            catch (Throwable e)
            {
                frame.dispose();
                UI.putError("下载失败:"+downloader.file(),e);
                return;
            }
            frame.dispose();
            if (downloader.hasError())
            {
                UI.putError("下载失败:"+downloader.file(),downloader.errorCause());
                return;
            }
            if (downloader.status()==Downloader.Status.Cancel)
            {
                UI.putInfo("下载已取消:"+downloader.file());
                return;
            }
            String message=String.format("下载完成:%s\n文件大小:%s\n下载耗时:%s\n平均下载速度:%s",
                                         downloader.file(),
                                         downloader.totalSize(),
                                         downloader.data().runTime(),
                                         downloader.data().averageSpeed());
            JOptionPane pane=new JOptionPane(message,JOptionPane.INFORMATION_MESSAGE,JOptionPane.YES_NO_OPTION);
            pane.setOptions(new String[]{"打开文件夹","关闭"});
            JDialog dialog=pane.createDialog("下载完成");
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
            if ("打开文件夹".equals(pane.getValue()))
            {
                //打开文件夹并选中文件
                try
                {
                    Desktop.getDesktop().open(new File(downloader.file()).getParentFile());
                }
                catch (Throwable e)
                {
                    UI.putError("打开文件夹失败",e);
                }
            }
        };
        ThreadManager.addTask(thread,"download-"+downloader.file());
        return panel;
    }

    private static class ServerCoresDrawer implements Runnable
    {
        private final JPanel panel;
        private final Run run;
        private final String ver;

        private ServerCoresDrawer(JPanel panel,Run run,String ver)
        {
            this.panel=panel;
            this.run=run;
            this.ver=ver;
        }

        @Override
        public void run()
        {
            removeAll(panel,allDownloadButton);
            //设置间隔
            panel.setLayout(new BluestarLayout());
            Insets insets=new Insets(0,blank,0,blank);
            BluestarLayoutData data;
            data=new BluestarLayoutData();
            data.setTransverseAlignment(BluestarLayoutData.CENTER);
            data.setPortraitAlignment(BluestarLayoutData.CENTER);
            panel.add(new JLabel("正在获取服务器核心列表...\n请稍等("+ver+")"),data);
            panel.repaint();
            panel.setEnabled(true);
            panel.updateUI();
            var cores=NetManager.getServerCores();
            var list=new ArrayList<ServerCoreGetter>();
            for (var core: cores)
            {
                System.out.println("检查版本:"+core.name());
                if (core.hasVersion(ver))
                {
                    list.add(core);
                }
            }
            var itemCount=list.size();
            removeAll(panel,allDownloadButton);
            System.out.println("获取到"+itemCount+"个核心");
            System.out.println("开始绘制");
            while (allDownloadButton.size()>1)
            {
                allDownloadButton.remove(allDownloadButton.size()-1);
            }
            panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
            for (ServerCoreGetter core: list)
            {
                //每行最左侧为图标,中间为名称,最右侧为下载按钮
                var icon=core.icon().getScaledInstance(50,50,Image.SCALE_SMOOTH);
                var name=core.name();
                var button=new JButton("下载");
                allDownloadButton.add(button);
                button.addActionListener(e->
                                         {
                                             allDownloadButton.forEach(x->x.setEnabled(false));
                                             url.setText(core.getDownloadURL(ver).toString());
                                             run.run();
                                         });
                var panel_=new JPanel();
                //横向布局
                panel_.setLayout(new TempLayout(200,50));
                panel_.add(new JLabel(new ImageIcon(icon)),TempLayout.Align.LEFT);
                panel_.add(new JLabel(name),TempLayout.Align.CENTER);
                panel_.add(button,TempLayout.Align.RIGHT);
                panel.add(panel_);
            }
            panel.repaint();
            panel.setEnabled(true);
            panel.updateUI();
        }

        private void removeAll(Container container,List<JButton> list)
        {
            for (Component component: container.getComponents())
            {
                list.remove(component);
                container.remove(component);
            }
        }
    }
}
