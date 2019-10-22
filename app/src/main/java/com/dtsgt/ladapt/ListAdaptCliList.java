package com.dtsgt.ladapt;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dtsgt.base.DateUtils;
import com.dtsgt.base.clsClasses.clsCDB;
import com.dtsgt.mpos.R;

import java.util.ArrayList;

public class ListAdaptCliList extends BaseAdapter {

	private static ArrayList<clsCDB> items;

	public DateUtils du;
	private int selectedIndex;
	private String date;

	private LayoutInflater l_Inflater;


	public ListAdaptCliList(Context context, ArrayList<clsCDB> results) {
		items = results;
		l_Inflater = LayoutInflater.from(context);
		selectedIndex = -1;
		du=new DateUtils();
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
		int val,cobro,ppago,iconid;
	
		if (convertView == null) {
			
			convertView = l_Inflater.inflate(R.layout.activity_list_view_clilist, null);
			holder = new ViewHolder();
			
			holder.lblCod  = (TextView) convertView.findViewById(R.id.lblETipo);
			holder.lblDesc = (TextView) convertView.findViewById(R.id.lblPNum);
			holder.imgBand = (ImageView) convertView.findViewById(R.id.imgNext);
			holder.imgCobro = (ImageView) convertView.findViewById(R.id.imageView8);
			holder.imgPPago = (ImageView) convertView.findViewById(R.id.imageView7);
			holder.lblDate  = (TextView) convertView.findViewById(R.id.textView141);
			holder.lblTel = (TextView) convertView.findViewById(R.id.textView140);
			holder.lblNit  = (TextView) convertView.findViewById(R.id.textView138);
			holder.lblEmail  = (TextView) convertView.findViewById(R.id.textView139);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
					
		holder.lblCod.setText("Cod: "+items.get(position).Cod);
		holder.lblNit.setText("NIT: "+items.get(position).nit);
		holder.lblDesc.setText(items.get(position).Desc);
		if(items.get(position).Date<=10000000) holder.lblDate.setText("");

		if(items.get(position).Date>=10000000) {
			date = du.univfechaReport(items.get(position).Date);
			holder.lblDate.setText(date);
		}

		if(items.get(position).Adds.isEmpty()) holder.lblTel.setText("No hay teléfono registrado");
		if(!items.get(position).Adds.isEmpty()) holder.lblTel.setText("Tel: "+ items.get(position).Adds);

		if(items.get(position).email.isEmpty()) holder.lblEmail.setText("");
		if(!items.get(position).email.isEmpty()) holder.lblEmail.setText(items.get(position).email);

		val= items.get(position).Bandera;
		cobro= items.get(position).Cobro;
		ppago= items.get(position).ppend;

		iconid=R.drawable.blank24;
		//if (val>0) iconid=R.drawable.disable24;
		//if (val==0) iconid=R.drawable.icok24;
		holder.imgBand.setImageResource(iconid);

		if (cobro==1) holder.imgCobro.setImageResource(R.drawable.cobro48);else holder.imgCobro.setImageResource(R.drawable.blank24);
		if (ppago==1) holder.imgPPago.setImageResource(R.drawable.pago_pend);else holder.imgPPago.setImageResource(R.drawable.blank24);

		if(selectedIndex!= -1 && position == selectedIndex) {
			convertView.setBackgroundColor(Color.rgb(26,138,198));
        } else {
        	convertView.setBackgroundColor(Color.TRANSPARENT);
        }
		
		return convertView;
	}
	
	
	static class ViewHolder {
		TextView  lblCod,lblDesc,lblDate,lblTel,lblNit,lblEmail;
		ImageView  imgBand,imgCobro,imgPPago;
	}
	
}