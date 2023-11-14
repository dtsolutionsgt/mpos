package com.dtsgt.ladapt;


import android.content.Context;
import android.graphics.Color;
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

public class LA_exist_list extends BaseAdapter {

    private MiscUtils mu;
    private DateUtils du;
    private AppMethods app;

    private ArrayList<clsClasses.clsT_exist_list> items= new ArrayList<clsClasses.clsT_exist_list>();
    private int selectedIndex;
    private LayoutInflater l_Inflater;

    public LA_exist_list(Context context, PBase owner, ArrayList<clsClasses.clsT_exist_list> results) {
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

            convertView = l_Inflater.inflate(R.layout.lv_exist_list, null);
            holder = new ViewHolder();

            holder.lbl2 = (TextView) convertView.findViewById(R.id.lblV2);
            holder.img1 = (ImageView) convertView.findViewById(R.id.imageView54);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.lbl2.setText(""+items.get(position).nombre);

        switch (items.get(position).flag) {
            case 0:
                holder.img1.setVisibility(View.INVISIBLE);
                convertView.setBackgroundResource(R.drawable.frame_label_focus);
                break;
            case 1:
                holder.img1.setVisibility(View.INVISIBLE);
                convertView.setBackgroundColor(Color.TRANSPARENT);
                break;
            case 2:
                holder.img1.setVisibility(View.VISIBLE);
                convertView.setBackgroundColor(Color.TRANSPARENT);
                break;
        }

        return convertView;
    }

    static class ViewHolder {
        TextView lbl2;
        ImageView img1;
    }

}

