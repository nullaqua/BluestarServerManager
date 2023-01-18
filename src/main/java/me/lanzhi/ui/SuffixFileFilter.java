package me.lanzhi.ui;

import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SuffixFileFilter extends FileFilter
{
    private final List<String> suffixList;
    private final String name;

    public SuffixFileFilter(String name,String... suffixList)
    {
        this.name=name;
        this.suffixList=new ArrayList<>();
        for (String suffix: suffixList)
        {
            if (suffix.startsWith("."))
            {
                this.suffixList.add(suffix);
            }
            else
            {
                this.suffixList.add("."+suffix);
            }
        }
    }

    @Override
    public boolean accept(File f)
    {
        return f.isDirectory()||suffixList.stream().anyMatch(f.getName()::endsWith);
    }

    @Override
    public String getDescription()
    {
        return name;
    }
}
