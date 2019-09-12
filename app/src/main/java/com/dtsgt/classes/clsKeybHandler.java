package com.dtsgt.classes;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;


import java.text.DecimalFormat;

public class clsKeybHandler {

    public TextView label;
    public boolean isValid,isEnter,idle;
    public double value;
    public String val;

    private Context cont;
    private TextView declabel;

    public clsKeybHandler(Context context, TextView outputlabel, TextView decpointlabel){
        cont=context;
        label=outputlabel;
        declabel=decpointlabel;

        clear(true);
    }

    public void setLabel(TextView actlabel,Boolean decpoint) {
        label=actlabel;
        clear(decpoint);
    }

    public void clear() {
        isValid=false;
        isEnter=false;
        idle=true;
        value=-1;val="";
        label.setText("");
      }

    public void clear(Boolean decpoint) {
        isValid=false;
        isEnter=false;
        idle=true;
        value=-1;val="";
        label.setText("");
        if (decpoint) {
            declabel.setVisibility(View.VISIBLE);
        } else {
            declabel.setVisibility(View.INVISIBLE);
        }
        //label.setBackgroundColor(Color.TRANSPARENT);
    }

    public void handleKey(String keychar) {
        int ll;

        if (!idle) return;

        isValid=false;isEnter=false;

        try {
            if (keychar.equalsIgnoreCase("E")) {
                isEnter=true;
            } else if (keychar.equalsIgnoreCase("C")) {
                val="";
            } else if (keychar.equalsIgnoreCase("B")) {
                ll=val.length();
                if (ll>0) val=val.substring(0,ll-1);
            } else if (keychar.equalsIgnoreCase(".")) {
                val+=".";
            } else if (keychar.equalsIgnoreCase("0")) {
                val+="0";
            } else if (keychar.equalsIgnoreCase("1")) {
                val+="1";
            } else if (keychar.equalsIgnoreCase("2")) {
                val+="2";
            } else if (keychar.equalsIgnoreCase("3")) {
                val+="3";
            } else if (keychar.equalsIgnoreCase("4")) {
                val+="4";
            } else if (keychar.equalsIgnoreCase("5")) {
                val+="5";
            } else if (keychar.equalsIgnoreCase("6")) {
                val+="6";
            } else if (keychar.equalsIgnoreCase("7")) {
                val+="7";
            } else if (keychar.equalsIgnoreCase("8")) {
                val+="8";
            } else if (keychar.equalsIgnoreCase("9")) {
                val+="9";
            }

            value=Double.parseDouble(val);
        } catch (Exception e) {
            value=-1;
        }

        isValid=value>=0;
        label.setText(val);

        //label.setBackgroundColor(Color.TRANSPARENT);
    }

    public void enable() {
        idle=true;
    }

    public void disable() {
        idle=false;
    }

    public void focus() {
        //label.setBackgroundColor(Color.parseColor("#02D42E"));
    }

    public void setValue(double val) {
        DecimalFormat ffrmdec2 = new DecimalFormat("#,##0.##");
        value=val;
        if (value>0) label.setText(ffrmdec2.format(val));else label.setText("");
    }

    public String getStringValue() {
        return val;
    }

}
