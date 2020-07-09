package com.dtsgt.ladapt;


import android.content.Context;
import java.util.ArrayList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dtsgt.base.AppMethods;
import com.dtsgt.base.DateUtils;
import com.dtsgt.base.MiscUtils;
import com.dtsgt.base.clsClasses;
import com.dtsgt.mpos.PBase;
import com.dtsgt.mpos.R;

public class LA_D_pedido  extends BaseAdapter {

    private MiscUtils mu;
    private DateUtils du;
    private AppMethods app;

    private ArrayList<clsClasses.clsD_pedido> items= new ArrayList<clsClasses.clsD_pedido>();
    private int selectedIndex;
    private LayoutInflater l_Inflater;

    public LA_D_pedido(Context context, PBase owner, ArrayList<clsClasses.clsD_pedido> results) {
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
        int corel,color,resid;

        if (convertView == null) {

            convertView = l_Inflater.inflate(R.layout.lv_d_pedido, null);
            holder = new ViewHolder();

            holder.lbl1 = (TextView) convertView.findViewById(R.id.lblV1);
            holder.lbl2 = (TextView) convertView.findViewById(R.id.lblV2);
            holder.lbl3 = (TextView) convertView.findViewById(R.id.lblV3);
            holder.lbl4 = (TextView) convertView.findViewById(R.id.lblV4);
            holder.img1 = (ImageView) convertView.findViewById(R.id.imageView72);
            holder.rel1 = (RelativeLayout) convertView.findViewById(R.id.relfill);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        corel=items.get(position).fecha_sistema % 1000;

        holder.lbl1.setText(""+corel);
        holder.lbl2.setText(du.sfechash(items.get(position).fecha_recepcion_suc));
        holder.lbl3.setText(du.shora(items.get(position).fecha_recepcion_suc));
        holder.lbl4.setText("");

        color=Color.parseColor("#FFFFFF");resid=R.drawable.ped_1;

        if (items.get(position).codigo_usuario_creo>0) {
            color=Color.parseColor("#E2D176");resid=R.drawable.ped_2;
        }
        if (items.get(position).codigo_usuario_proceso>0)  {
            color=Color.parseColor("#8DDF8F");resid=R.drawable.ped_3;
        }
        
        holder.rel1.setBackgroundColor(color);
        holder.img1.setImageResource(resid);

        if(selectedIndex!= -1 && position == selectedIndex) {
            convertView.setBackgroundColor(Color.rgb(26,138,198));
        } else {
            convertView.setBackgroundColor(Color.TRANSPARENT);
        }

        return convertView;
    }

    static class ViewHolder {
        TextView lbl1,lbl2,lbl3,lbl4;
        ImageView img1;
        RelativeLayout rel1;
    }

}

