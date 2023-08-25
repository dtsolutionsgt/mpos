package com.dtsgt.ladapt;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.dtsgt.base.AppMethods;
import com.dtsgt.base.DateUtils;
import com.dtsgt.base.MiscUtils;
import com.dtsgt.base.clsClasses;
import com.dtsgt.mpos.PBase;
import com.dtsgt.mpos.R;

import java.util.ArrayList;

public class LA_ResCaja extends BaseAdapter {

    private MiscUtils mu;
    private DateUtils du;
    private AppMethods app;

    private ArrayList<clsClasses.clsView> items= new ArrayList<clsClasses.clsView>();
    private int selectedIndex;
    private LayoutInflater l_Inflater;

    public LA_ResCaja(Context context, PBase owner, ArrayList<clsClasses.clsView> results) {
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

            convertView = l_Inflater.inflate(R.layout.lv_res_caja, null);
            holder = new ViewHolder();

            holder.lbl2 = convertView.findViewById(R.id.lblV2);
            holder.lbl4 = convertView.findViewById(R.id.lblV4);
            holder.lbl5 = convertView.findViewById(R.id.lblV5);
            holder.lbl6 = convertView.findViewById(R.id.textView273);
            holder.img1 = convertView.findViewById(R.id.imageView102);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.lbl2.setText(""+items.get(position).f2);
        holder.lbl4.setText(""+items.get(position).f7);
        holder.lbl5.setText(""+items.get(position).pk);
        holder.lbl6.setText(""+items.get(position).f4);

        holder.img1.setImageResource(R.drawable.mesa_ocupada);
        if (items.get(position).f3.equalsIgnoreCase("2")) {
            holder.img1.setImageResource(R.drawable.preimpresion);
        } else if (items.get(position).f3.equalsIgnoreCase("3")) {
            holder.img1.setImageResource(R.drawable.pago_caja);
        } else if (items.get(position).f3.equalsIgnoreCase("4")) {
            holder.img1.setImageResource(R.drawable.mesa_pago_completo);
        }

        if(selectedIndex!= -1 && position == selectedIndex) {
            convertView.setBackgroundColor(Color.rgb(26,138,198));
        } else {
            convertView.setBackgroundColor(Color.TRANSPARENT);
        }

        return convertView;
    }

    static class ViewHolder {
        TextView lbl2,lbl4,lbl5,lbl6;
        ImageView img1;
    }

}

