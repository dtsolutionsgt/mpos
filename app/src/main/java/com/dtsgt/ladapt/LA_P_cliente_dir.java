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

public class LA_P_cliente_dir  extends BaseAdapter {

    private MiscUtils mu;
    private DateUtils du;
    private AppMethods app;

    private ArrayList<clsClasses.clsP_cliente_dir> items= new ArrayList<clsClasses.clsP_cliente_dir>();
    private int selectedIndex;
    private LayoutInflater l_Inflater;

    public LA_P_cliente_dir(Context context, PBase owner, ArrayList<clsClasses.clsP_cliente_dir> results) {
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

            convertView = l_Inflater.inflate(R.layout.lv_p_cliente_dir, null);
            holder = new ViewHolder();

            holder.lbl1 = (TextView) convertView.findViewById(R.id.lblV1);
            holder.lbl2 = (TextView) convertView.findViewById(R.id.lblV2);
            holder.lbl3 = (TextView) convertView.findViewById(R.id.lblV3);
            holder.lbl4 = (TextView) convertView.findViewById(R.id.lblV4);
            holder.lbl5 = (TextView) convertView.findViewById(R.id.lblV5);
            holder.lbl6 = (TextView) convertView.findViewById(R.id.lblV6);
            holder.lbl7 = (TextView) convertView.findViewById(R.id.lblV7);
            holder.lbl8 = (TextView) convertView.findViewById(R.id.lblV8);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.lbl1.setText(""+items.get(position).codigo_direccion);
        holder.lbl2.setText(""+items.get(position).codigo_cliente);
        holder.lbl3.setText(""+items.get(position).referencia);
        holder.lbl4.setText(""+items.get(position).codigo_departamento);
        holder.lbl5.setText(""+items.get(position).codigo_municipio);
        holder.lbl6.setText(""+items.get(position).direccion);
        holder.lbl7.setText(""+items.get(position).zona_entrega);
        holder.lbl8.setText(""+items.get(position).telefono);

        if(selectedIndex!= -1 && position == selectedIndex) {
            convertView.setBackgroundColor(Color.rgb(26,138,198));
        } else {
            convertView.setBackgroundColor(Color.TRANSPARENT);
        }

        return convertView;
    }

    static class ViewHolder {
        TextView lbl1,lbl2,lbl3,lbl4,lbl5,lbl6,lbl7,lbl8;
    }

}

