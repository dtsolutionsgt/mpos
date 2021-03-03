package com.dtsgt.ladapt;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dtsgt.base.clsClasses;
import com.dtsgt.base.clsClasses.clsMenu;
import com.dtsgt.mpos.R;

import java.util.ArrayList;

public class ListAdaptOpcion extends BaseAdapter {
	private ArrayList<clsClasses.clsOpcion> items;

	private int selectedIndex;

	private LayoutInflater l_Inflater;

	public ListAdaptOpcion(Context context, ArrayList<clsClasses.clsOpcion> results) {
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
			convertView = l_Inflater.inflate(R.layout.activity_list_view_opcion, null);
			holder = new ViewHolder();
			
			holder.imgEst = (ImageView) convertView.findViewById(R.id.imgNext);
			holder.lblName = (TextView) convertView.findViewById(R.id.lblTrat);
            holder.lblPrec = (TextView) convertView.findViewById(R.id.textView226);
		
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
			
		holder.lblName.setText(items.get(position).Name);
        holder.lblPrec.setText(items.get(position).sprec);
			
		holder.imgEst.setImageResource(R.drawable.check_round_off);
		if (items.get(position).bandera==1) holder.imgEst.setImageResource(R.drawable.check_round_on);

		if (selectedIndex!=-1 && position == selectedIndex) {
			convertView.setBackgroundColor(Color.rgb(26,138,198));
        } else {
        	convertView.setBackgroundColor(Color.TRANSPARENT);
        }

		return convertView;
	}

	static class ViewHolder {
		ImageView imgEst;
		TextView  lblName,lblPrec;
	}
	
}
