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
    private boolean idlargo;

    public LA_D_pedido(Context context, PBase owner, ArrayList<clsClasses.clsD_pedido> results,boolean corellargo) {
        items = results;
        l_Inflater = LayoutInflater.from(context);
        selectedIndex = -1;
        idlargo =corellargo;

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
        int corel,color,resid,warn;
        long tdif,tlim;
        String st,stl;

        if (convertView == null) {

            convertView = l_Inflater.inflate(R.layout.lv_d_pedido, null);
            holder = new ViewHolder();

            holder.lbl1 = (TextView) convertView.findViewById(R.id.lblV1);
            holder.lbl2 = (TextView) convertView.findViewById(R.id.lblV2);
            holder.lbl3 = (TextView) convertView.findViewById(R.id.lblV3);
            holder.lbl4 = (TextView) convertView.findViewById(R.id.lblV4);
            holder.lbl5 = (TextView) convertView.findViewById(R.id.lblV);
            holder.img1 = (ImageView) convertView.findViewById(R.id.imageView72);
            holder.img2 = (ImageView) convertView.findViewById(R.id.imageView81);
            holder.rel1 = (RelativeLayout) convertView.findViewById(R.id.relfill);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (idlargo) {
            corel=(int) items.get(position).empresa;
        } else {
            corel=(int) items.get(position).empresa % 1000;
        }

        tdif=items.get(position).tdif;tlim=items.get(position).lim;

        if (corel>0) holder.lbl1.setText(""+corel);else holder.lbl1.setText("");
        if (tdif>=0) {
            st="TPPO : "+tdif+" m";stl="Meta : "+tlim+" m";
        } else {
            st="";stl="";
        }
        holder.lbl2.setText(st);
        holder.lbl5.setText(stl);
        if (items.get(position).fecha_salida_suc==0) {
            holder.lbl3.setText("Inicio : "+du.shora(items.get(position).fecha_pedido));
        } else {
            holder.lbl3.setText("Salio : "+du.shora(items.get(position).fecha_salida_suc));
        }
        holder.lbl4.setText(items.get(position).nombre);

        color=Color.parseColor("#FFFFFF");resid=R.drawable.ped_1;
        if (items.get(position).codigo_usuario_creo>0) {
            color=Color.parseColor("#E2D176");resid=R.drawable.ped_2;
        }
        if (items.get(position).codigo_usuario_proceso>0)  {
            color=Color.parseColor("#8DDF8F");resid=R.drawable.ped_3;
        }
        if (items.get(position).fecha_salida_suc>0)  {
            color=Color.parseColor("#57CAD7");resid=R.drawable.ped_4;
        }
        if (items.get(position).fecha_entrega>0)  {
            color=Color.parseColor("#A9A5A9");resid=R.drawable.ped_6;
        }
        if (items.get(position).anulado==1)  {
            color=Color.parseColor("#ED9475");resid=R.drawable.ped_5;
        }

        if (tdif>tlim) warn=1;else warn=0;
        if (tlim==0) warn=0;
        //if (items.get(position).fecha_salida_suc>0) warn=0;
        if (warn>0) holder.img2.setVisibility(View.VISIBLE);else holder.img2.setVisibility(View.INVISIBLE);

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
        TextView lbl1,lbl2,lbl3,lbl4,lbl5;
        ImageView img1,img2;
        RelativeLayout rel1;
    }

}

