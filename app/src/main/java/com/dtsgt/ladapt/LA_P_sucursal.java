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
import com.dtsgt.base.DateUtils;
import com.dtsgt.base.MiscUtils;
import com.dtsgt.base.clsClasses;
import com.dtsgt.mpos.PBase;
import com.dtsgt.mpos.R;

public class LA_P_sucursal extends BaseAdapter {

    private MiscUtils mu;
    private DateUtils du;
    private AppMethods app;

    private ArrayList<clsClasses.clsP_sucursal> items = new ArrayList<clsClasses.clsP_sucursal>();
    private int selectedIndex;
    private LayoutInflater l_Inflater;

    public LA_P_sucursal(Context context, PBase owner, ArrayList<clsClasses.clsP_sucursal> results) {
        items = results;
        l_Inflater = LayoutInflater.from(context);
        selectedIndex = -1;

        mu = owner.mu;
        du = owner.du;
        app = owner.app;
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

            convertView = l_Inflater.inflate(R.layout.lv_p_sucursal, null);
            holder = new ViewHolder();

            holder.lbl3 = (TextView) convertView.findViewById(R.id.lblV3);
            holder.img1 = (ImageView) convertView.findViewById(R.id.imageView33);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.lbl3.setText("" + items.get(position).descripcion);

        if (items.get(position).activo==1) {
            holder.img1.setImageResource(R.drawable.check_round_on);
        } else {
            holder.img1.setImageResource(R.drawable.check_round_off);
        }

        if (selectedIndex != -1 && position == selectedIndex) {
            convertView.setBackgroundColor(Color.rgb(26, 138, 198));
        } else {
            convertView.setBackgroundColor(Color.TRANSPARENT);
        }

        return convertView;
    }

    static class ViewHolder {
        TextView lbl3;
        ImageView img1;
    }

}

