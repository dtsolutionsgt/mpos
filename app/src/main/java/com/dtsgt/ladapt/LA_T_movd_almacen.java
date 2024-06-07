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

public class LA_T_movd_almacen  extends BaseAdapter {

    private MiscUtils mu;
    private DateUtils du;
    private AppMethods app;

    private ArrayList<clsClasses.clsT_movd_almacen> items= new ArrayList<clsClasses.clsT_movd_almacen>();
    private int selectedIndex;
    private LayoutInflater l_Inflater;

    public LA_T_movd_almacen(Context context, PBase owner, ArrayList<clsClasses.clsT_movd_almacen> results) {
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
        int idres;

        if (convertView == null) {

            convertView = l_Inflater.inflate(R.layout.lv_t_movd_almacen, null);
            holder = new ViewHolder();

            holder.lbl1 = (TextView) convertView.findViewById(R.id.lblV1);
            holder.lbl3 = (TextView) convertView.findViewById(R.id.lblV3);
            holder.lbl4 = (TextView) convertView.findViewById(R.id.lblV4);
            holder.lbl5 = (TextView) convertView.findViewById(R.id.lblV5);
            holder.lbl7 = (TextView) convertView.findViewById(R.id.lblV7);
            holder.img1 =  convertView.findViewById(R.id.imageView163);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.lbl1.setText(""+items.get(position).sprod);
        holder.lbl3.setText(""+items.get(position).cant+" "+items.get(position).um);
        holder.lbl5.setText("Verificado: "+items.get(position).cantact);
        holder.lbl7.setText("Precio: "+mu.frmval(items.get(position).precio));

        idres=R.drawable.blank48;
        if (items.get(position).flag==-1) idres=R.drawable.color_red_grad;
        if (items.get(position).flag== 1) idres=R.drawable.color_green_grad;
        holder.img1.setImageResource(idres);

        if (selectedIndex!= -1 && position == selectedIndex) {
            convertView.setBackgroundColor(Color.rgb(26,138,198));
        } else {
            convertView.setBackgroundColor(Color.TRANSPARENT);
        }

        return convertView;
    }

    static class ViewHolder {
        TextView lbl1,lbl3,lbl4,lbl5,lbl7;
        ImageView img1;
    }

}

