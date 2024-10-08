package com.dtsgt.ladapt;


import android.content.Context;
import java.util.ArrayList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.dtsgt.base.AppMethods;
import com.dtsgt.base.DateUtils;
import com.dtsgt.base.MiscUtils;
import com.dtsgt.base.clsClasses;
import com.dtsgt.mpos.PBase;
import com.dtsgt.mpos.R;

public class LA_D_domicilio_enc  extends BaseAdapter {

    private MiscUtils mu;
    private DateUtils du;
    private AppMethods app;

    private ArrayList<clsClasses.clsD_domicilio_enc> items= new ArrayList<clsClasses.clsD_domicilio_enc>();
    private int selectedIndex;
    private LayoutInflater l_Inflater;

    public LA_D_domicilio_enc(Context context, PBase owner, ArrayList<clsClasses.clsD_domicilio_enc> results) {
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
        int estado;

        if (convertView == null) {

            convertView = l_Inflater.inflate(R.layout.lv_d_domicilio_enc, null);
            holder = new ViewHolder();

            holder.lbl1 =   convertView.findViewById(R.id.lblV1);
            holder.lbl2 =   convertView.findViewById(R.id.lblV2);
            holder.lbl3 =   convertView.findViewById(R.id.lblV3);
            holder.lbl4 =   convertView.findViewById(R.id.lblV4);
            holder.lbl7 =   convertView.findViewById(R.id.lblV7);
            holder.relest = convertView.findViewById(R.id.relest);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.lbl1.setText(""+items.get(position).sorden);
        holder.lbl2.setText(""+items.get(position).smin);
        holder.lbl3.setText(""+items.get(position).shora);
        holder.lbl4.setText(""+items.get(position).sestado);
        holder.lbl7.setText(""+items.get(position).cliente_nombre);

        RelativeLayout.LayoutParams relativeParams = (RelativeLayout.LayoutParams) holder.relest.getLayoutParams();

        if (selectedIndex!= -1 && position == selectedIndex) {
            relativeParams.setMargins(5, 5, 5, 5);
            convertView.setBackgroundColor(Color.rgb(26,138,198));
        } else {
            relativeParams.setMargins(1, 1, 1, 1);
            convertView.setBackgroundColor(Color.TRANSPARENT);
        }

        holder.relest.setLayoutParams(relativeParams);

        estado=items.get(position).estado;
        switch (estado) {
            case 2:
                holder.relest.setBackgroundResource(R.drawable.blank48);break;
            case 3:
                holder.relest.setBackgroundResource(R.drawable.color_ocra_grad);break;
            case 5:
                holder.relest.setBackgroundResource(R.drawable.color_green_grad);break;
            case 6:
                holder.relest.setBackgroundResource(R.drawable.color_gray_grad);break;
        }



        return convertView;
    }

    static class ViewHolder {
        TextView lbl1,lbl2,lbl3,lbl4,lbl7;
        RelativeLayout relest;
    }

}

