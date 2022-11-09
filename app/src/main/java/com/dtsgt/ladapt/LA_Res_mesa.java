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

import com.dtsgt.base.MiscUtils;
import com.dtsgt.base.DateUtils;
import com.dtsgt.base.AppMethods;
import com.dtsgt.base.clsClasses;
import com.dtsgt.mpos.PBase;
import com.dtsgt.mpos.R;

public class LA_Res_mesa  extends BaseAdapter {

    private MiscUtils mu;
    private DateUtils du;
    private AppMethods app;

    private ArrayList<clsClasses.clsRes_mesa> items= new ArrayList<clsClasses.clsRes_mesa>();
    private int selectedIndex;
    private LayoutInflater l_Inflater;
    private boolean fullsize;

    public LA_Res_mesa(Context context, PBase owner, ArrayList<clsClasses.clsRes_mesa> results,boolean horiz) {
        items = results;
        l_Inflater = LayoutInflater.from(context);
        selectedIndex = -1;
        fullsize=horiz;

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
        int est,pend;

        try {

            if (convertView == null) {

                if (fullsize) {
                    convertView = l_Inflater.inflate(R.layout.lv_res_mesa, null);
                } else {
                    convertView = l_Inflater.inflate(R.layout.lv_res_mesa_half, null);
                }

                holder = new ViewHolder();

                holder.lbl1 = convertView.findViewById(R.id.lblV1);
                holder.lbl5 = convertView.findViewById(R.id.lblV5);
                holder.lbl6 = convertView.findViewById(R.id.textView270);
                holder.lbl7 = convertView.findViewById(R.id.textView272);
                holder.img1 =  convertView.findViewById(R.id.imageView97);
                holder.img2 =  convertView.findViewById(R.id.imageView130);
                holder.rel1 =  convertView.findViewById(R.id.relcolor);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            est=items.get(position).estado;
            pend=items.get(position).pendiente;

            holder.lbl1.setText(""+items.get(position).nombre);
            holder.lbl6.setText(""+items.get(position).alias);
            holder.lbl7.setText(items.get(position).numorden);
            holder.img1.setImageResource(R.drawable.blank48);
            holder.img1.setVisibility(View.INVISIBLE);

            if (est==0) {
                holder.lbl5.setText("");
                holder.rel1.setBackgroundColor(Color.parseColor("#F0F0F0"));
                //holder.rel1.setBackgroundColor(Color.parseColor("#CCE6F3"));
            } else {
                holder.lbl5.setText(""+du.shora(items.get(position).fecha));
                holder.rel1.setBackgroundColor(Color.parseColor("#60C4F2"));
                //holder.rel1.setBackgroundColor(Color.WHITE);

                switch (est) {
                    case 1:
                        holder.img1.setImageResource(R.drawable.blank48);
                        if (pend>0) {
                            holder.img1.setImageResource(R.drawable.icon_cooking2);
                        }
                        break;
                    case 2:
                        holder.img1.setImageResource(R.drawable.preimpresion);
                        break;
                    case 3:
                        holder.img1.setImageResource(R.drawable.pago_pendiente);
                        break;
                }
            }

            if (items.get(position).est_envio==1) {
                holder.img2.setVisibility(View.INVISIBLE);
            } else {
                holder.img2.setVisibility(View.VISIBLE);
            }

            if(selectedIndex!= -1 && position == selectedIndex) {
                convertView.setBackgroundColor(Color.rgb(26,138,198));
            } else {
                convertView.setBackgroundColor(Color.TRANSPARENT);
            }
            return convertView;

        } catch (Exception e) {
            String ss=e.getMessage();
            //mu.msgbox(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
            return null;
        }

    }

    static class ViewHolder {
        TextView lbl1,lbl5,lbl6,lbl7;
        ImageView img1,img2;
        RelativeLayout rel1;
    }

}

