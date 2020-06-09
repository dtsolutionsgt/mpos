package com.dtsgt.ladapt;


import android.content.Context;
import java.util.ArrayList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dtsgt.base.AppMethods;
import com.dtsgt.base.DateUtils;
import com.dtsgt.base.MiscUtils;
import com.dtsgt.base.clsClasses;
import com.dtsgt.mpos.PBase;
import com.dtsgt.mpos.R;

public class LA_T_movd  extends BaseAdapter {

    private MiscUtils mu;
    private DateUtils du;
    private AppMethods app;

    private ArrayList<clsClasses.clsT_movd> items= new ArrayList<clsClasses.clsT_movd>();
    private int selectedIndex;
    private LayoutInflater l_Inflater;

    public LA_T_movd(Context context, PBase owner, ArrayList<clsClasses.clsT_movd> results) {
        items = results;
        l_Inflater = LayoutInflater.from(context);
        selectedIndex = -1;

        mu=owner.mu;
        du=owner.du;
        app=owner.app;
    }

    public void setSelectedIndex(int ind) {
        selectedIndex = ind;
        notifyDataSetChanged();
    }

    public void refreshItems() {
        notifyDataSetChanged();
    }

    public int getCount() {
        return items.size();
    }

    public Object getItem(int position) {
        return items.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {

            convertView = l_Inflater.inflate(R.layout.lv_t_movd, null);
            holder = new ViewHolder();

            holder.lbl4 = (TextView) convertView.findViewById(R.id.lblV4);
            holder.lbl7 = (TextView) convertView.findViewById(R.id.lblV7);
            holder.lbl8 = (TextView) convertView.findViewById(R.id.lblV8);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.lbl4.setText(mu.frmint(items.get(position).cant));
        holder.lbl7.setText(mu.frmcur(items.get(position).pesom));
        holder.lbl8.setText(""+items.get(position).lote);

        if(selectedIndex!= -1 && position == selectedIndex) {
            convertView.setBackgroundColor(Color.rgb(26,138,198));
        } else {
            convertView.setBackgroundColor(Color.TRANSPARENT);
        }

        return convertView;
    }

    static class ViewHolder {
        TextView lbl4,lbl7,lbl8;
    }

}

