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
import com.dtsgt.base.appGlobals;
import com.dtsgt.base.DateUtils;
import com.dtsgt.base.MiscUtils;
import com.dtsgt.base.clsClasses;
import com.dtsgt.mpos.PBase;
import com.dtsgt.mpos.R;

public class listAdapt_desc extends BaseAdapter {

    private MiscUtils mu;
    private DateUtils du;
    private AppMethods app;
    private appGlobals appG;

    private long dateini, datefin;

    private ArrayList<clsClasses.clsLista> items = new ArrayList<clsClasses.clsLista>();
    private int selectedIndex;
    private LayoutInflater l_Inflater;

    public listAdapt_desc(Context context, PBase owner, ArrayList<clsClasses.clsLista> results) {
        items = results;
        l_Inflater = LayoutInflater.from(context);
        selectedIndex = -1;

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

            convertView = l_Inflater.inflate(R.layout.list_desc, null);
            holder = new ViewHolder();

            holder.txtCod = (TextView) convertView.findViewById(R.id.txtCod);
            holder.txtName = (TextView) convertView.findViewById(R.id.txtName);
            holder.txtDisc = (TextView) convertView.findViewById(R.id.txtDisc);
            holder.txtStartdate = (TextView) convertView.findViewById(R.id.txtStartdate);
            holder.txtEnddate = (TextView) convertView.findViewById(R.id.txtEnddate);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        dateini = Long.parseLong(items.get(position).f4);
        datefin = Long.parseLong(items.get(position).f5);

        holder.txtCod.setText("" + items.get(position).f1);
        holder.txtName.setText("" + items.get(position).f2);
        holder.txtDisc.setText("" + items.get(position).f3);
        holder.txtStartdate.setText("" + du.sfecha(dateini));
        holder.txtEnddate.setText("" + du.sfecha(datefin));

        if (selectedIndex != -1 && position == selectedIndex) {
            convertView.setBackgroundColor(Color.rgb(26, 138, 198));
        } else {
            convertView.setBackgroundColor(Color.TRANSPARENT);
        }

        return convertView;
    }

    static class ViewHolder {
        TextView txtCod, txtName, txtDisc, txtStartdate, txtEnddate;
    }

}


