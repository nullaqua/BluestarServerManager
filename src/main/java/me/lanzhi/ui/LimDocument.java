package me.lanzhi.ui;

import me.lanzhi.api.util.collection.FastLinkedList;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.html.HTMLDocument;
import java.util.List;


public class LimDocument extends HTMLDocument// implements StyledDocument,
{
    private final int lineMax;
    private final JTextComponent text;
    private final List<Integer> lineLength=new FastLinkedList<>();

    public LimDocument(JTextComponent text,int lineMax)
    {
        this.lineMax=lineMax;
        this.text=text;
    }

    public void insertString(int offset,String s,AttributeSet attributeSet) throws BadLocationException
    {
        super.insertString(offset,s,attributeSet);
        //将文本每行的长度存入列表
        int x=0;
        int last=0;
        while (x<s.length())
        {
            if (s.charAt(x)=='\n')
            {
                lineLength.add(x-last);
                last=x+1;
            }
            x++;
        }
        //如果超过了最大行数
        while (lineLength.size()>lineMax)
        {
            //删除第一行
            super.remove(0,lineLength.get(0)+1);
            lineLength.remove(1);
        }
        //滚动到最后一行
        text.setCaretPosition(getLength());
    }
}