package me.nullaqua.ui;

import me.lanzhi.api.awt.BluestarLayout;
import me.lanzhi.api.awt.BluestarLayoutData;
import me.nullaqua.UI;
import me.nullaqua.Utils;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;

public class ConfigPaneDrawer
{
    private static final int blank=new JLabel("一一一一一一").getPreferredSize().width;
    private static JTextField javaPathField=new JTextField();
    private static JTextField serverJarPathField=new JTextField();
    private static JTextField xmxField=new JTextField();
    private static JTextField xmsField=new JTextField();
    private static JList<String> argsField=new JList<>();
    private static JList<String> javaAgentField=new JList<>();

    public static void draw()
    {
        ActionListener listener;
        /*第一行选择java路径
        第二行选择jar路径
        第三行为最大和最小内存
        第四行为启动参数
        第五行为JavaAgent*/
        JPanel panel=new JPanel();
        panel.setLayout(new BluestarLayout());
        /* 最左侧为"java路径",中间为文本框,右侧为"选择"按钮
         * "java路径"和"选择"始终占据固定的宽度,文本框占据剩余的宽度
         */
        var data=new BluestarLayoutData(3,11,0,0,1,1,new Insets(0,5,0,0));
        data.setTransverseAlignment(BluestarLayoutData.FRONT);
        data.setPortraitAlignment(BluestarLayoutData.CENTER);
        panel.add(new JLabel("java路径"),data);
        data=new BluestarLayoutData(3,11,2,0,1,1,new Insets(0,0,0,5));
        data.setTransverseAlignment(BluestarLayoutData.BACK);
        data.setPortraitAlignment(BluestarLayoutData.CENTER);
        JButton javaPathChooser=new JButton("选择");
        panel.add(javaPathChooser,data);
        Insets insets=new Insets(0,blank,0,blank);
        data=new BluestarLayoutData(1,11,0,0,1,1,insets);
        data.setTransverseAlignment(BluestarLayoutData.FILL);
        data.setPortraitAlignment(BluestarLayoutData.CENTER);
        panel.add(javaPathField,data);
        listener=e->
        {
            JFileChooser chooser=new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            chooser.removeChoosableFileFilter(chooser.getAcceptAllFileFilter());
            //只能选择java.exe
            chooser.setFileFilter(new javax.swing.filechooser.FileFilter()
            {
                @Override
                public boolean accept(File f)
                {
                    return f.isDirectory()||f.getName().endsWith(".exe");
                }

                @Override
                public String getDescription()
                {
                    return "java.exe";
                }
            });
            chooser.setMultiSelectionEnabled(false);
            chooser.setDialogTitle("选择java路径");
            var home=Utils.getCurrentJavaHome();
            if (home!=null)
            {
                chooser.setCurrentDirectory(new File(home));
            }
            chooser.showOpenDialog(panel);
            var file=chooser.getSelectedFile();
            if (file!=null)
            {
                javaPathField.setText(file.getAbsolutePath());
            }
        };
        javaPathChooser.addActionListener(listener);
        /* 第二行为"jar路径",中间为文本框,右侧为"选择"按钮
         * "jar路径"和"选择"始终占据固定的宽度,文本框占据剩余的宽度
         */
        data=new BluestarLayoutData(3,11,0,1,1,1,new Insets(0,5,0,0));
        data.setTransverseAlignment(BluestarLayoutData.FRONT);
        data.setPortraitAlignment(BluestarLayoutData.CENTER);
        panel.add(new JLabel("jar路径"),data);
        data=new BluestarLayoutData(3,11,2,1,1,1,new Insets(0,0,0,5));
        data.setTransverseAlignment(BluestarLayoutData.BACK);
        data.setPortraitAlignment(BluestarLayoutData.CENTER);
        JButton jarPathChooser=new JButton("选择");
        panel.add(jarPathChooser,data);
        listener=e->
        {
            JFileChooser chooser=new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            chooser.removeChoosableFileFilter(chooser.getAcceptAllFileFilter());
            //只能选择jar
            chooser.setFileFilter(new javax.swing.filechooser.FileFilter()
            {
                @Override
                public boolean accept(File f)
                {
                    return f.isDirectory()||f.getName().endsWith(".jar");
                }

                @Override
                public String getDescription()
                {
                    return "jar";
                }
            });
            chooser.setMultiSelectionEnabled(false);
            chooser.setDialogTitle("选择jar路径");
            chooser.setCurrentDirectory(Utils.getDir());
            chooser.showOpenDialog(panel);
            var file=chooser.getSelectedFile();
            if (file!=null)
            {
                serverJarPathField.setText(file.getAbsolutePath());
            }
        };
        jarPathChooser.addActionListener(listener);
        data=new BluestarLayoutData(1,11,0,1,1,1,insets);
        data.setTransverseAlignment(BluestarLayoutData.FILL);
        data.setPortraitAlignment(BluestarLayoutData.CENTER);
        panel.add(serverJarPathField,data);
        /* 第三行为"最大内存",中间为文本框,右侧为"MB"/"GB"选择列表
         */
        data=new BluestarLayoutData(1,11,0,2,1,1,new Insets(0,5,0,0));
        data.setTransverseAlignment(BluestarLayoutData.FRONT);
        data.setPortraitAlignment(BluestarLayoutData.CENTER);
        panel.add(new JLabel("最大内存"),data);
        data=new BluestarLayoutData(1,11,0,2,1,1,new Insets(0,0,0,5));
        data.setTransverseAlignment(BluestarLayoutData.BACK);
        data.setPortraitAlignment(BluestarLayoutData.CENTER);
        //最大内存的单位("GB"/"MB"/"KB")
        var maxMemoryUnit=new JComboBox<String>(new String[]{"GB","MB","KB"});
        panel.add(maxMemoryUnit,data);
        data=new BluestarLayoutData(1,11,0,2,1,1,insets);
        data.setTransverseAlignment(BluestarLayoutData.FILL);
        data.setPortraitAlignment(BluestarLayoutData.CENTER);
        panel.add(xmxField,data);
        /* 第四行为"最小内存",中间为文本框,右侧为"MB"/"GB"选择列表
         */
        data=new BluestarLayoutData(1,11,0,3,1,1,new Insets(0,5,0,0));
        data.setTransverseAlignment(BluestarLayoutData.FRONT);
        data.setPortraitAlignment(BluestarLayoutData.CENTER);
        panel.add(new JLabel("最小内存"),data);
        data=new BluestarLayoutData(1,11,0,3,1,1,new Insets(0,0,0,5));
        data.setTransverseAlignment(BluestarLayoutData.BACK);
        data.setPortraitAlignment(BluestarLayoutData.CENTER);
        //最小内存的单位("GB"/"MB"/"KB")
        var minMemoryUnit=new JComboBox<>(new String[]{"GB","MB","KB"});
        panel.add(minMemoryUnit,data);
        data=new BluestarLayoutData(1,11,0,3,1,1,insets);
        data.setTransverseAlignment(BluestarLayoutData.FILL);
        data.setPortraitAlignment(BluestarLayoutData.CENTER);
        panel.add(xmsField,data);

        /* 第五行为"启动参数",中间为文本框,右侧为"添加"按钮和"删除"按钮纵向排列
         */
        data=new BluestarLayoutData(1,11,0,4,1,3,new Insets(0,5,0,0));
        data.setTransverseAlignment(BluestarLayoutData.FRONT);
        data.setPortraitAlignment(BluestarLayoutData.CENTER);
        panel.add(new JLabel("启动参数"),data);
        data=new BluestarLayoutData(1,22,0,9,1,1,new Insets(0,0,0,5));
        data.setTransverseAlignment(BluestarLayoutData.BACK);
        data.setPortraitAlignment(BluestarLayoutData.CENTER);
        var add=new JButton("添加");
        var delete=new JButton("删除");
        panel.add(add,data);
        data=new BluestarLayoutData(1,22,0,12,1,1,new Insets(0,0,0,5));
        data.setTransverseAlignment(BluestarLayoutData.BACK);
        data.setPortraitAlignment(BluestarLayoutData.CENTER);
        panel.add(delete,data);
        data=new BluestarLayoutData(1,11,0,4,1,3,new Insets(10,blank,10,blank));
        data.setTransverseAlignment(BluestarLayoutData.FILL);
        data.setPortraitAlignment(BluestarLayoutData.FILL);
        //滚动列表
        panel.add(new JScrollPane(argsField),data);
        //添加按钮的监听器
        listener=e->
        {
            //添加参数
            //弹出输入框
            var input=JOptionPane.showInputDialog(panel,"请输入参数");
            if (input!=null&&!input.isEmpty()&&!input.isBlank())
            {
                if (!input.startsWith("-"))
                {
                    input="-"+input;
                }
                //添加参数
                Utils.getUnsaved().javaArgs.add(input);
                //刷新列表
                argsField.setListData(Utils.getUnsaved().javaArgs.toArray(new String[0]));
            }
        };
        add.addActionListener(listener);
        //删除按钮的监听器
        listener=e->
        {
            //删除参数
            //获取选中的参数
            var selected=argsField.getSelectedValuesList();
            if (selected.size()>0)
            {
                //删除参数
                Utils.getUnsaved().javaArgs.removeAll(selected);
                //刷新列表
                argsField.setListData(Utils.getUnsaved().javaArgs.toArray(new String[0]));
            }
        };
        delete.addActionListener(listener);

        /* 第六行为"javaAgent",中间为文本框,右侧为"添加"按钮和"删除"按钮纵向排列
         */
        data=new BluestarLayoutData(1,11,0,7,1,3,new Insets(0,5,0,0));
        data.setTransverseAlignment(BluestarLayoutData.FRONT);
        data.setPortraitAlignment(BluestarLayoutData.CENTER);
        panel.add(new JLabel("javaAgent"),data);
        data=new BluestarLayoutData(1,22,0,15,1,1,new Insets(0,0,0,5));
        data.setTransverseAlignment(BluestarLayoutData.BACK);
        data.setPortraitAlignment(BluestarLayoutData.CENTER);
        add=new JButton("添加");
        delete=new JButton("删除");
        panel.add(add,data);
        data=new BluestarLayoutData(1,22,0,18,1,1,new Insets(0,0,0,5));
        data.setTransverseAlignment(BluestarLayoutData.BACK);
        data.setPortraitAlignment(BluestarLayoutData.CENTER);
        panel.add(delete,data);
        data=new BluestarLayoutData(1,11,0,7,1,3,new Insets(10,blank,10,blank));
        data.setTransverseAlignment(BluestarLayoutData.FILL);
        data.setPortraitAlignment(BluestarLayoutData.FILL);
        panel.add(new JScrollPane(javaAgentField),data);
        //添加按钮的监听器
        listener=e->
        {
            //选择jar文件
            var chooser=new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            chooser.removeChoosableFileFilter(chooser.getFileFilter());
            chooser.setFileFilter(new FileNameExtensionFilter("jar文件","jar"));
            chooser.setMultiSelectionEnabled(true);
            chooser.setDialogTitle("选择jar文件");
            chooser.setCurrentDirectory(Utils.getDir());
            if (chooser.showOpenDialog(panel)==JFileChooser.APPROVE_OPTION)
            {
                //添加参数
                for (var file: chooser.getSelectedFiles())
                {
                    Utils.getUnsaved().javaAgent.add(file.getAbsolutePath());
                }
                //刷新列表
                javaAgentField.setListData(Utils.getUnsaved().javaAgent.toArray(new String[0]));
            }
        };
        add.addActionListener(listener);
        //删除按钮的监听器
        listener=e->
        {
            //删除参数
            //获取选中的参数
            var selected=javaAgentField.getSelectedValuesList();
            if (selected.size()>0)
            {
                //删除参数
                Utils.getUnsaved().javaAgent.removeAll(selected);
                //刷新列表
                javaAgentField.setListData(Utils.getUnsaved().javaAgent.toArray(new String[0]));
            }
        };
        delete.addActionListener(listener);

        //最后一行为为保存按钮
        data=new BluestarLayoutData(1,11,0,10,1,1);
        data.setTransverseAlignment(BluestarLayoutData.CENTER);
        data.setPortraitAlignment(BluestarLayoutData.CENTER);
        JButton save=new JButton("保存");
        panel.add(save,data);
        //保存按钮的监听器
        listener=e->
        {
            //将每一项放入unsaved中
            Utils.getUnsaved().java_exe=javaPathField.getText();
            Utils.getUnsaved().serverJar=serverJarPathField.getText();
            Utils.getUnsaved().javaArgs.clear();
            for (int i=0;i<argsField.getModel().getSize();i++)
            {
                Utils.getUnsaved().javaArgs.add(argsField.getModel().getElementAt(i));
            }
            Utils.getUnsaved().javaAgent.clear();
            for (int i=0;i<javaAgentField.getModel().getSize();i++)
            {
                Utils.getUnsaved().javaAgent.add(javaAgentField.getModel().getElementAt(i));
            }
            //xmx
            Utils.getUnsaved().xmx=xmxField.getText()+maxMemoryUnit.getSelectedItem();
            //xms
            Utils.getUnsaved().xms=xmsField.getText()+minMemoryUnit.getSelectedItem();
            //保存
            Utils.save();
        };
        save.addActionListener(listener);
        //将当前配置写入UI
        javaPathField.setText(Utils.getUnsaved().java_exe);
        serverJarPathField.setText(Utils.getUnsaved().serverJar);
        argsField.setListData(Utils.getUnsaved().javaArgs.toArray(new String[0]));
        javaAgentField.setListData(Utils.getUnsaved().javaAgent.toArray(new String[0]));
        if (Utils.getUnsaved().xmx!=null&&Utils.getUnsaved().xmx.length()>=2)
        {
            xmxField.setText(Utils.getUnsaved().xmx.substring(0,Utils.getUnsaved().xmx.length()-2));
            maxMemoryUnit.setSelectedItem(Utils.getUnsaved().xmx.substring(Utils.getUnsaved().xmx.length()-2));
        }
        if (Utils.getUnsaved().xms!=null&&Utils.getUnsaved().xms.length()>=2)
        {
            xmsField.setText(Utils.getUnsaved().xms.substring(0,Utils.getUnsaved().xms.length()-2));
            minMemoryUnit.setSelectedItem(Utils.getUnsaved().xms.substring(Utils.getUnsaved().xms.length()-2));
        }
        UI.tabbedPane.addTab("运行设置",panel);
    }
}
