package me.nullaqua.ui;

import me.nullaqua.UI;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

public class ColorPane extends JTextPane
{
    private static final Color D_Black=Color.getHSBColor(0.000f,0.000f,0.000f);
    private static final Color D_Red=Color.getHSBColor(0.000f,1.000f,0.502f);
    private static final Color D_Blue=Color.getHSBColor(0.667f,1.000f,0.502f);
    private static final Color D_Magenta=Color.getHSBColor(0.833f,1.000f,0.502f);
    private static final Color D_Green=Color.getHSBColor(0.333f,1.000f,0.502f);
    private static final Color D_Yellow=Color.getHSBColor(0.167f,1.000f,0.502f);
    private static final Color D_Cyan=Color.getHSBColor(0.500f,1.000f,0.502f);
    private static final Color D_White=Color.getHSBColor(0.000f,0.000f,0.753f);
    private static final Color B_Black=Color.getHSBColor(0.000f,0.000f,0.502f);
    private static final Color B_Red=Color.getHSBColor(0.000f,1.000f,1.000f);
    private static final Color B_Blue=Color.getHSBColor(0.667f,1.000f,1.000f);
    private static final Color B_Magenta=Color.getHSBColor(0.833f,1.000f,1.000f);
    private static final Color B_Green=Color.getHSBColor(0.333f,1.000f,1.000f);
    private static final Color B_Yellow=Color.getHSBColor(0.167f,1.000f,1.000f);
    private static final Color B_Cyan=Color.getHSBColor(0.500f,1.000f,1.000f);
    private static final Color B_White=Color.getHSBColor(0.000f,0.000f,1.000f);
    private AttributeSet colorCurrent=SimpleAttributeSet.EMPTY;
    private String remaining="";

    public void append(String s)
    {
        int aPos=0;   // current char position in addString
        int aIndex; // index of next Escape sequence
        int mIndex; // index of "m" terminating Escape sequence
        String tmpString;
        boolean stillSearching=true; // true until no more Escape sequences
        String addString=remaining+s;
        remaining="";

        if (addString.length()>0)
        {
            aIndex=addString.indexOf(27); // find first escape
            if (aIndex==-1)
            { // no escape/color change in this string, so just send it with current color
                append(colorCurrent,addString);
                return;
            }
            // otherwise There is an escape character in the string, so we must process it

            if (aIndex>0)
            { // Escape is not first char, so send text up to first escape
                tmpString=addString.substring(0,aIndex);
                append(colorCurrent,tmpString);
                aPos=aIndex;
            }
            // aPos is now at the beginning of the first escape sequence

            while (stillSearching)
            {
                mIndex=addString.indexOf("m",aPos); // find the end of the escape sequence
                if (mIndex<0)
                { // the buffer ends halfway through the ansi string!
                    remaining=addString.substring(aPos);
                    stillSearching=false;
                    continue;
                }
                else
                {
                    tmpString=addString.substring(aPos,mIndex+1);
                    AnsiToAttributeSet(tmpString);
                }
                aPos=mIndex+1;
                // now we have the color, send text that is in that color (up to next escape)

                aIndex=addString.indexOf("\u001B",aPos);

                if (aIndex==-1)
                { // if that was the last sequence of the input, send remaining text
                    tmpString=addString.substring(aPos);
                    append(colorCurrent,tmpString);
                    stillSearching=false;
                    continue; // jump out of loop early, as the whole string has been sent now
                }

                // there is another escape sequence, so send part of the string and prepare for the next
                tmpString=addString.substring(aPos,aIndex);
                aPos=aIndex;
                append(colorCurrent,tmpString);

            } // while there's text in the input buffer
        }
    }

    public void append(AttributeSet c,String s)
    {
        int len=getDocument().getLength();
        try
        {
            getDocument().insertString(len,s,c);
        }
        catch (BadLocationException e)
        {
            UI.putError("Can't update output.",e);
        }
        this.setCaretPosition(getDocument().getLength());
    }

    public void AnsiToAttributeSet(String ansi)
    {
        StyleContext sc=StyleContext.getDefaultStyleContext();
        ansi=ansi.substring(2,ansi.length()-1);
        String[] parts=ansi.split(";");
        for (int i=0;i<parts.length;i++)
        {
            var x=parts[i];
            var id=0;
            try
            {
                id=Integer.parseInt(x);
            }
            catch (Throwable ignored)
            {
            }
            switch (id)
            {
                case 0 ->
                {
                    this.colorCurrent=SimpleAttributeSet.EMPTY;
                }
                case 1 ->
                {
                    //??????
                    this.colorCurrent=sc.addAttribute(this.colorCurrent,StyleConstants.Bold,true);
                }
                case 2/*??????*/,21/*????????????*/,22/*????????????*/ ->
                {
                    this.colorCurrent=sc.addAttribute(this.colorCurrent,StyleConstants.Bold,false);
                }
                case 3 ->
                {
                    //??????
                    this.colorCurrent=sc.addAttribute(this.colorCurrent,StyleConstants.Italic,true);
                }
                case 4 ->
                {
                    //?????????
                    this.colorCurrent=sc.addAttribute(this.colorCurrent,StyleConstants.Underline,true);
                }
                case 5,6 ->
                {
                    //??????
                    //?????????????????????
                }
                case 7,27 ->
                {
                    //?????????????????????
                    Color fg=(Color) this.colorCurrent.getAttribute(StyleConstants.Foreground);
                    Color bg=(Color) this.colorCurrent.getAttribute(StyleConstants.Background);
                    this.colorCurrent=sc.addAttribute(this.colorCurrent,StyleConstants.Foreground,bg);
                    this.colorCurrent=sc.addAttribute(this.colorCurrent,StyleConstants.Background,fg);
                }
                case 8 ->
                {
                    //??????
                    //?????????????????????
                }
                case 9 ->
                {    //?????????
                    this.colorCurrent=sc.addAttribute(this.colorCurrent,StyleConstants.StrikeThrough,true);
                }
                case 10,//??????,???????????????
                        11,//??????,???????????????
                        12,//??????,???????????????
                        13,//??????,???????????????
                        14,//??????,???????????????
                        15,//??????,???????????????
                        16,//??????,???????????????
                        17,//??????,???????????????
                        18,//??????,???????????????
                        19,//??????,???????????????
                        20 ->//"??????"??????,???????????????
                {
                }
                case 23 ->
                {//????????????
                    this.colorCurrent=sc.addAttribute(this.colorCurrent,StyleConstants.Italic,false);

                }
                case 24 ->
                {    //???????????????
                    this.colorCurrent=sc.addAttribute(this.colorCurrent,StyleConstants.Underline,false);

                }
                case 25,26 ->
                {
                    //????????????
                    //?????????????????????
                }
                case 28 ->
                {
                    //????????????
                    //?????????????????????
                }
                case 29 ->
                {
                    //???????????????
                    this.colorCurrent=sc.addAttribute(this.colorCurrent,StyleConstants.StrikeThrough,false);
                }
                case 30 ->
                {
                    //??????
                    this.colorCurrent=sc.addAttribute(this.colorCurrent,StyleConstants.Foreground,D_Black);
                }
                case 31 ->
                {
                    //??????
                    this.colorCurrent=sc.addAttribute(this.colorCurrent,StyleConstants.Foreground,D_Red);
                }
                case 32 ->
                {
                    //??????
                    this.colorCurrent=sc.addAttribute(this.colorCurrent,StyleConstants.Foreground,D_Green);
                }
                case 33 ->
                {
                    //??????
                    this.colorCurrent=sc.addAttribute(this.colorCurrent,StyleConstants.Foreground,D_Yellow);
                }
                case 34 ->
                {
                    //??????
                    this.colorCurrent=sc.addAttribute(this.colorCurrent,StyleConstants.Foreground,D_Blue);
                }
                case 35 ->
                {
                    //??????
                    this.colorCurrent=sc.addAttribute(this.colorCurrent,StyleConstants.Foreground,D_Magenta);
                }
                case 36 ->
                {
                    //??????
                    this.colorCurrent=sc.addAttribute(this.colorCurrent,StyleConstants.Foreground,D_Cyan);
                }
                case 37 ->
                {
                    //??????
                    this.colorCurrent=sc.addAttribute(this.colorCurrent,StyleConstants.Foreground,D_White);
                }
                case 38 ->
                {
                    //RGB??????
                    Color c=parseRGB(parts,i);
                    if (c!=null)
                    {
                        this.colorCurrent=sc.addAttribute(this.colorCurrent,StyleConstants.Foreground,c);
                    }
                }
                case 39 ->
                {
                    //????????????
                    this.colorCurrent=sc.addAttribute(this.colorCurrent,
                                                      StyleConstants.Foreground,
                                                      SimpleAttributeSet.EMPTY.getAttribute(StyleConstants.Foreground));
                }
                case 40 ->
                {
                    //????????????
                    this.colorCurrent=sc.addAttribute(this.colorCurrent,StyleConstants.Background,B_Black);
                }
                case 41 ->
                {
                    //????????????
                    this.colorCurrent=sc.addAttribute(this.colorCurrent,StyleConstants.Background,B_Red);
                }
                case 42 ->
                {
                    //????????????
                    this.colorCurrent=sc.addAttribute(this.colorCurrent,StyleConstants.Background,B_Green);
                }
                case 43 ->
                {
                    //????????????
                    this.colorCurrent=sc.addAttribute(this.colorCurrent,StyleConstants.Background,B_Yellow);
                }
                case 44 ->
                {
                    //????????????
                    this.colorCurrent=sc.addAttribute(this.colorCurrent,StyleConstants.Background,B_Blue);
                }
                case 45 ->
                {
                    //????????????
                    this.colorCurrent=sc.addAttribute(this.colorCurrent,StyleConstants.Background,B_Magenta);
                }
                case 46 ->
                {
                    //????????????
                    this.colorCurrent=sc.addAttribute(this.colorCurrent,StyleConstants.Background,B_Cyan);
                }
                case 47 ->
                {
                    //????????????
                    this.colorCurrent=sc.addAttribute(this.colorCurrent,StyleConstants.Background,B_White);
                }
                case 48 ->
                {
                    //RGB??????
                    Color c2=parseRGB(parts,i);
                    if (c2!=null)
                    {
                        this.colorCurrent=sc.addAttribute(this.colorCurrent,StyleConstants.Background,c2);
                    }
                }
                case 49 ->
                {
                    //????????????
                    this.colorCurrent=sc.addAttribute(this.colorCurrent,
                                                      StyleConstants.Background,
                                                      SimpleAttributeSet.EMPTY.getAttribute(StyleConstants.Background));
                }
                case 51,//Framed
                        52,//Encircled
                        53,//?????????
                        54,//Not framed or encircled
                        55,//???????????????
                        60,//ideogram underline or right side line
                        61,//ideogram double underline or double line on the right side
                        62,//ideogram overline or left side line
                        63,//ideogram double overline or double line on the left side
                        64,//ideogram stress marking
                        65 ->//ideogram attributes off
                {
                    //?????????????????????*n
                }
                case 90 ->
                {
                    //????????????
                    this.colorCurrent=sc.addAttribute(this.colorCurrent,StyleConstants.Foreground,B_Black);
                }
                case 91 ->
                {
                    //????????????
                    this.colorCurrent=sc.addAttribute(this.colorCurrent,StyleConstants.Foreground,B_Red);
                }
                case 92 ->
                {
                    //????????????
                    this.colorCurrent=sc.addAttribute(this.colorCurrent,StyleConstants.Foreground,B_Green);
                }
                case 93 ->
                {
                    //????????????
                    this.colorCurrent=sc.addAttribute(this.colorCurrent,StyleConstants.Foreground,B_Yellow);
                }
                case 94 ->
                {
                    //????????????
                    this.colorCurrent=sc.addAttribute(this.colorCurrent,StyleConstants.Foreground,B_Blue);
                }
                case 95 ->
                {
                    //????????????
                    this.colorCurrent=sc.addAttribute(this.colorCurrent,StyleConstants.Foreground,B_Magenta);
                }
                case 96 ->
                {
                    //????????????
                    this.colorCurrent=sc.addAttribute(this.colorCurrent,StyleConstants.Foreground,B_Cyan);
                }
                case 97 ->
                {
                    //????????????
                    this.colorCurrent=sc.addAttribute(this.colorCurrent,StyleConstants.Foreground,B_White);
                }
                case 100 ->
                {
                    //??????????????????
                    this.colorCurrent=sc.addAttribute(this.colorCurrent,StyleConstants.Background,B_Black);
                }
                case 101 ->
                {
                    //??????????????????
                    this.colorCurrent=sc.addAttribute(this.colorCurrent,StyleConstants.Background,B_Red);
                }
                case 102 ->
                {
                    //??????????????????
                    this.colorCurrent=sc.addAttribute(this.colorCurrent,StyleConstants.Background,B_Green);
                }
                case 103 ->
                {
                    //??????????????????
                    this.colorCurrent=sc.addAttribute(this.colorCurrent,StyleConstants.Background,B_Yellow);
                }
                case 104 ->
                {
                    //??????????????????
                    this.colorCurrent=sc.addAttribute(this.colorCurrent,StyleConstants.Background,B_Blue);
                }
                case 105 ->
                {
                    //??????????????????
                    this.colorCurrent=sc.addAttribute(this.colorCurrent,StyleConstants.Background,B_Magenta);
                }
                case 106 ->
                {
                    //??????????????????
                    this.colorCurrent=sc.addAttribute(this.colorCurrent,StyleConstants.Background,B_Cyan);
                }
                case 107 ->
                {
                    //??????????????????
                    this.colorCurrent=sc.addAttribute(this.colorCurrent,StyleConstants.Background,B_White);
                }
            }
        }
    }

    private Color parseRGB(String[] parts,int i)
    {
        if (parts.length<i+1)
        {
            return null;
        }
        switch (parts[i])
        {
            case "5" ->
            {
                if (parts.length<i+2)
                {
                    return null;
                }
                int color=Integer.parseInt(parts[i+1]);
                return switch (color)
                        {
                            case 0 -> D_Black;
                            case 1 -> D_Red;
                            case 2 -> D_Green;
                            case 3 -> D_Yellow;
                            case 4 -> D_Blue;
                            case 5 -> D_Magenta;
                            case 6 -> D_Cyan;
                            case 7 -> D_White;
                            case 8 -> B_Black;
                            case 9 -> B_Red;
                            case 10 -> B_Green;
                            case 11 -> B_Yellow;
                            case 12 -> B_Blue;
                            case 13 -> B_Magenta;
                            case 14 -> B_Cyan;
                            case 15 -> B_White;
                            default -> null;
                        };
            }
            case "2" ->
            {
                if (parts.length<i+4)
                {
                    return null;
                }
                int r=Integer.parseInt(parts[i+1]);
                int g=Integer.parseInt(parts[i+2]);
                int b=Integer.parseInt(parts[i+3]);
                return new Color(r,g,b);
            }
            default ->
            {
                return null;
            }
        }
    }
}