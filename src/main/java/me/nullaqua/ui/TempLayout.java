package me.nullaqua.ui;

import me.lanzhi.api.awt.BluestarLayout;
import me.lanzhi.api.awt.BluestarLayoutData;

import java.awt.*;

public class TempLayout extends BluestarLayout
{
    private final int width;
    private final int height;

    public TempLayout(int width,int height)
    {
        this.width=width;
        this.height=height;
    }

    @Override
    public void addLayoutComponent(Component comp,Object constraints)
    {
        if (constraints instanceof Align)
        {
            switch ((Align) constraints)
            {
                case LEFT ->
                {
                    BluestarLayoutData data=new BluestarLayoutData();
                    data.setPortraitAlignment(BluestarLayoutData.FILL);
                    data.setTransverseAlignment(BluestarLayoutData.FRONT);
                    super.addLayoutComponent(comp,data);
                }
                case RIGHT ->
                {
                    BluestarLayoutData data=new BluestarLayoutData();
                    data.setPortraitAlignment(BluestarLayoutData.FILL);
                    data.setTransverseAlignment(BluestarLayoutData.BACK);
                    super.addLayoutComponent(comp,data);
                }
                case CENTER ->
                {
                    BluestarLayoutData data=new BluestarLayoutData();
                    data.setPortraitAlignment(BluestarLayoutData.FILL);
                    data.setTransverseAlignment(BluestarLayoutData.CENTER);
                    super.addLayoutComponent(comp,data);
                }
            }
        }
    }

    @Override
    public Dimension maximumLayoutSize(Container target)
    {
        return new Dimension(Integer.MAX_VALUE,height);
    }

    @Override
    public Dimension preferredLayoutSize(Container parent)
    {
        return new Dimension(0,height);
    }

    @Override
    public Dimension minimumLayoutSize(Container parent)
    {
        return new Dimension(width,height);
    }

    @Override
    public void layoutContainer(Container parent)
    {
        super.layoutContainer(parent);
    }

    public static enum Align
    {
        LEFT,
        RIGHT,
        CENTER;
    }
}
