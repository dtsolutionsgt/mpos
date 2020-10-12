package com.dtsgt.ladapt;

import android.content.Context;

import java.util.ArrayList;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.dtsgt.base.AppMethods;
import com.dtsgt.base.appGlobals;
import com.dtsgt.base.DateUtils;
import com.dtsgt.base.MiscUtils;
import com.dtsgt.base.clsClasses;
import com.dtsgt.mpos.PBase;
import com.dtsgt.mpos.R;

public class LA_Lista extends BaseAdapter {

    private MiscUtils mu;
    private DateUtils du;
    private AppMethods app;
    private appGlobals appG;

    private ArrayList<clsClasses.clsLista> items = new ArrayList<clsClasses.clsLista>();
    private int selectedIndex,mantid;
    private LayoutInflater l_Inflater;

    public LA_Lista(Context context, PBase owner, ArrayList<clsClasses.clsLista> results,int mant_id) {
        items = results;
        l_Inflater = LayoutInflater.from(context);
        selectedIndex = -1;
        mantid=mant_id;

        mu = owner.mu;
        du = owner.du;
        app = owner.app;
        appG = owner.gl;
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

            convertView = l_Inflater.inflate(R.layout.lv_lista, null);
            holder = new ViewHolder();

            holder.lbl2 = (TextView) convertView.findViewById(R.id.lblV2);
            holder.lbl3 = (TextView) convertView.findViewById(R.id.lblV3);
            holder.lbl4 = (TextView) convertView.findViewById(R.id.lblV4);
            holder.img1 = (ImageView) convertView.findViewById(R.id.imageView48);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.lbl2.setText("" + items.get(position).f1);if (mantid==27) holder.lbl2.setText("");
        holder.lbl3.setText("" + items.get(position).f2);
        if(appG.banco){
            holder.lbl4.setText("" + items.get(position).f3);
        }else {
            holder.lbl4.setVisibility(View.GONE);
        }

        if(items.get(position).f8.isEmpty()) {
            holder.img1.setVisibility(View.INVISIBLE);
        } else {
            holder.img1.setVisibility(View.VISIBLE);
        }

        if (selectedIndex != -1 && position == selectedIndex) {
            convertView.setBackgroundColor(Color.rgb(26, 138, 198));
        } else {
            convertView.setBackgroundColor(Color.TRANSPARENT);
        }

        return convertView;
    }

    static class ViewHolder {
        TextView lbl2, lbl3, lbl4;
        ImageView img1;
    }

}


