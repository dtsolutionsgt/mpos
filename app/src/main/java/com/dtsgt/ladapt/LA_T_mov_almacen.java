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

public class LA_T_mov_almacen  extends BaseAdapter {

    private MiscUtils mu;
    private DateUtils du;
    private AppMethods app;

    private ArrayList<clsClasses.clsT_mov_almacen> items= new ArrayList<clsClasses.clsT_mov_almacen>();
    private int selectedIndex;
    private LayoutInflater l_Inflater;

    public LA_T_mov_almacen(Context context, PBase owner, ArrayList<clsClasses.clsT_mov_almacen> results) {
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

            convertView = l_Inflater.inflate(R.layout.lv_t_mov_almacen, null);
            holder = new ViewHolder();

            holder.lbl1 = (TextView) convertView.findViewById(R.id.lblV1);
            holder.lbl2 = (TextView) convertView.findViewById(R.id.lblV2);
            holder.lbl3 = (TextView) convertView.findViewById(R.id.lblV3);
            holder.lbl4 = (TextView) convertView.findViewById(R.id.lblV4);
            holder.lbl5 = (TextView) convertView.findViewById(R.id.lblV5);
            holder.lbl6 = (TextView) convertView.findViewById(R.id.lblV6);
            holder.lbl7 = (TextView) convertView.findViewById(R.id.lblV7);
            holder.lbl8 = (TextView) convertView.findViewById(R.id.lblV8);
            holder.lbl9 = (TextView) convertView.findViewById(R.id.lblV9);
            holder.lbl10 = (TextView) convertView.findViewById(R.id.lblV10);
            holder.lbl11 = (TextView) convertView.findViewById(R.id.lblV11);
            holder.lbl12 = (TextView) convertView.findViewById(R.id.lblV12);
            holder.lbl13 = (TextView) convertView.findViewById(R.id.lblV13);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.lbl1.setText(""+items.get(position).corel);
        holder.lbl2.setText(""+items.get(position).almacen_origen);
        holder.lbl3.setText(""+items.get(position).almacen_destino);
        holder.lbl4.setText(""+items.get(position).referencia);
        holder.lbl5.setText(""+items.get(position).total);
        holder.lbl6.setText(""+items.get(position).idtrasalmacen);
        holder.lbl7.setText(""+items.get(position).estado);
        holder.lbl8.setText(""+items.get(position).fechaini);
        holder.lbl9.setText(""+items.get(position).fechafin);
        holder.lbl10.setText(""+items.get(position).usrini);
        holder.lbl11.setText(""+items.get(position).usrfin);
        holder.lbl12.setText(""+items.get(position).idalmtrans);
        holder.lbl13.setText(""+items.get(position).completo);

        if(selectedIndex!= -1 && position == selectedIndex) {
            convertView.setBackgroundColor(Color.rgb(26,138,198));
        } else {
            convertView.setBackgroundColor(Color.TRANSPARENT);
        }

        return convertView;
    }

    static class ViewHolder {
        TextView lbl1,lbl2,lbl3,lbl4,lbl5,lbl6,lbl7,lbl8,lbl9,lbl10,lbl11,lbl12,lbl13;
    }

}

