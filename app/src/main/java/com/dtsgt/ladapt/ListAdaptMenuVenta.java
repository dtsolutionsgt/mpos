package com.dtsgt.ladapt;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dtsgt.base.clsClasses.clsMenu;
import com.dtsgt.mpos.R;

import java.util.ArrayList;

public class ListAdaptMenuVenta extends BaseAdapter {
	private ArrayList<clsMenu> items;

	private int selectedIndex;

	private LayoutInflater l_Inflater;

	public ListAdaptMenuVenta(Context context, ArrayList<clsMenu> results) {
		items = results;
		l_Inflater = LayoutInflater.from(context);
		selectedIndex = -1;
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
		int iconid;

		if (convertView == null) {
			convertView = l_Inflater.inflate(R.layout.activity_list_view_menuventa, null);
			holder = new ViewHolder();
			
			holder.imgEst = (ImageView) convertView.findViewById(R.id.imgNext);
			holder.lblName = (TextView) convertView.findViewById(R.id.lblTrat);
            holder.lblCant = (TextView) convertView.findViewById(R.id.textView153);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
			
		holder.lblName.setText(items.get(position).Name);

		if (items.get(position).cant>0) {
            holder.lblCant.setVisibility(View.VISIBLE);
            holder.lblCant.setText(""+items.get(position).cant);
        } else {
            holder.lblCant.setVisibility(View.INVISIBLE);
        }
			
		holder.imgEst.setImageResource(R.drawable.blank256);
		
		if (items.get(position).Icon==1) holder.imgEst.setImageResource(R.drawable.pedidos_3);
		if (items.get(position).Icon==101) holder.imgEst.setImageResource(R.drawable.pedidos_3);
		if (items.get(position).Icon==102) holder.imgEst.setImageResource(R.drawable.pedidos_2);
		if (items.get(position).Icon==2) holder.imgEst.setImageResource(R.drawable.recibir_archivos);
		if (items.get(position).Icon==3) holder.imgEst.setImageResource(R.drawable.reimpresion);
		if (items.get(position).Icon==4) holder.imgEst.setImageResource(R.drawable.anulacion);
		if (items.get(position).Icon==5) holder.imgEst.setImageResource(R.drawable.consultas);
		if (items.get(position).Icon==6) holder.imgEst.setImageResource(R.drawable.depositos);
		if (items.get(position).Icon==7) holder.imgEst.setImageResource(R.drawable.inventario);
		if (items.get(position).Icon==8) holder.imgEst.setImageResource(R.drawable.findia);
		if (items.get(position).Icon==9) holder.imgEst.setImageResource(R.drawable.utils);
		if (items.get(position).Icon==10) holder.imgEst.setImageResource(R.drawable.cambio_usuario);
		if (items.get(position).Icon==11) holder.imgEst.setImageResource(R.drawable.mantenimientos48);
		if (items.get(position).Icon==12) holder.imgEst.setImageResource(R.drawable.venta_add);
		if (items.get(position).Icon==13) holder.imgEst.setImageResource(R.drawable.venta_switch);
        if (items.get(position).Icon==14) holder.imgEst.setImageResource(R.drawable.recibir_rapido);
        if (items.get(position).Icon==15) holder.imgEst.setImageResource(R.drawable.fel);
        if (items.get(position).Icon==16) holder.imgEst.setImageResource(R.drawable.dom);

        if (items.get(position).Icon==50) holder.imgEst.setImageResource(R.drawable.btn_search);
        if (items.get(position).Icon==51) holder.imgEst.setImageResource(R.drawable.barcode_btn);
        if (items.get(position).Icon==52) holder.imgEst.setImageResource(R.drawable.vendedor);
        if (items.get(position).Icon==53) holder.imgEst.setImageResource(R.drawable.lock);
        if (items.get(position).Icon==54) holder.imgEst.setImageResource(R.drawable.btn_del_line);
        if (items.get(position).Icon==55) holder.imgEst.setImageResource(R.drawable.btn_del_all);
        if (items.get(position).Icon==56) holder.imgEst.setImageResource(R.drawable.btn_switch);
        if (items.get(position).Icon==57) holder.imgEst.setImageResource(R.drawable.btn_exit);
		if (items.get(position).Icon==58) holder.imgEst.setImageResource(R.drawable.pago2);
		if (items.get(position).Icon==59) holder.imgEst.setImageResource(R.drawable.cierre_dia);
        if (items.get(position).Icon==60) holder.imgEst.setImageResource(R.drawable.reportes);
        if (items.get(position).Icon==61) holder.imgEst.setImageResource(R.drawable.btn_pedido);
        if (items.get(position).Icon==62) holder.imgEst.setImageResource(R.drawable.btn_print2);
        if (items.get(position).Icon==63) holder.imgEst.setImageResource(R.drawable.btn_mesero);


        if (items.get(position).Icon==101) holder.imgEst.setImageResource(R.drawable.logo_baktun);

		if(selectedIndex!= -1 && position == selectedIndex) {
			convertView.setBackgroundColor(Color.rgb(26,138,198));
        } else {
        	convertView.setBackgroundColor(Color.TRANSPARENT);
        }

		return convertView;
	}

	static class ViewHolder {
		ImageView imgEst;
		TextView  lblName,lblCant;
	}
	
}
