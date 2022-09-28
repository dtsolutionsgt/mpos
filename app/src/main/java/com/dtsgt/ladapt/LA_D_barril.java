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

public class LA_D_barril  extends BaseAdapter {

    private MiscUtils mu;
    private DateUtils du;
    private AppMethods app;

    private ArrayList<clsClasses.clsD_barril> items= new ArrayList<clsClasses.clsD_barril>();
    private int selectedIndex;
    private LayoutInflater l_Inflater;

    public LA_D_barril(Context context, PBase owner, ArrayList<clsClasses.clsD_barril> results) {
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

            convertView = l_Inflater.inflate(R.layout.lv_d_barril, null);
            holder = new ViewHolder();

            holder.lbl4 = (TextView) convertView.findViewById(R.id.lblV4);
            holder.lbl5 = (TextView) convertView.findViewById(R.id.lblV5);
            holder.lbl6 = (TextView) convertView.findViewById(R.id.lblV6);
            holder.lbl7 = (TextView) convertView.findViewById(R.id.lblV7);
            holder.lbl8 = (TextView) convertView.findViewById(R.id.lblV8);
            holder.lbl11 = (TextView) convertView.findViewById(R.id.lblV11);
            holder.lbl12 = (TextView) convertView.findViewById(R.id.lblV12);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.lbl4.setText(items.get(position).ntipo);
        holder.lbl5.setText(""+items.get(position).codigo_interno);
        holder.lbl6.setText(items.get(position).nest);
        holder.lbl7.setText(items.get(position).nprod);
        holder.lbl8.setText("Lote: "+items.get(position).lote);
        holder.lbl11.setText("Vence: "+items.get(position).nfecha);
        holder.lbl12.setText(items.get(position).nfabrir);


        items.get(position).activo=-1;

        holder.lbl6.setBackgroundResource(R.drawable.frame_rect_blue);
        switch (items.get(position).activo) {
            case 1:
                holder.lbl6.setBackgroundResource(R.drawable.frame_label_focus);break;
            case 0:
                holder.lbl6.setBackgroundColor(Color.TRANSPARENT);break;
        }

        if(selectedIndex!= -1 && position == selectedIndex) {
            convertView.setBackgroundColor(Color.rgb(26,138,198));
        } else {
            convertView.setBackgroundColor(Color.TRANSPARENT);
        }

        return convertView;
    }

    static class ViewHolder {
        TextView lbl4,lbl5,lbl6,lbl7,lbl8,lbl11,lbl12;
    }

}

