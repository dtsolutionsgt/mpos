package com.dtsgt.ladapt;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dtsgt.base.clsClasses.clsMenu;
import com.dtsgt.mpos.R;

import java.util.ArrayList;

public class ListAdaptMasVendidos extends BaseAdapter {
	private ArrayList<clsMenu> items;

	private int selectedIndex;
    private LayoutInflater l_Inflater;

	public ListAdaptMasVendidos(Context context, ArrayList<clsMenu> results) {
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

		if (convertView == null) {
			convertView = l_Inflater.inflate(R.layout.activity_list_view_mas_vendidos, null);
			holder = new ViewHolder();
			
			holder.lblName = (TextView) convertView.findViewById(R.id.lblTrat);
		
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
			
		holder.lblName.setText(items.get(position).Name);

		if (selectedIndex!= -1 && position == selectedIndex) {
			convertView.setBackgroundResource(items.get(position).idressel);
		} else {
			convertView.setBackgroundResource(items.get(position).idres);
		}

		return convertView;
	}

	static class ViewHolder {
		TextView  lblName;
	}
	
}
