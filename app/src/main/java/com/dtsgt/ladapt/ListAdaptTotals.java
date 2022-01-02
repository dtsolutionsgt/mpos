package com.dtsgt.ladapt;

import java.util.ArrayList;

import com.dtsgt.base.clsClasses.clsCDB;
import com.dtsgt.mpos.R;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListAdaptTotals extends BaseAdapter {

	private ArrayList<clsCDB> items;

	private int selectedIndex;
	private boolean horiz;

	private LayoutInflater l_Inflater;

	public ListAdaptTotals(Context context, ArrayList<clsCDB> results,boolean horizdir) {
		items = results;
		l_Inflater = LayoutInflater.from(context);
		selectedIndex = -1;
        horiz=horizdir;
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

		    if (horiz) {
                convertView = l_Inflater.inflate(R.layout.activity_list_view_totals, null);
            } else {
                convertView = l_Inflater.inflate(R.layout.activity_list_view_totals_ver, null);
            }

			holder = new ViewHolder();

			holder.lblCod  = (TextView) convertView.findViewById(R.id.lblETipo);
			holder.lblDesc = (TextView) convertView.findViewById(R.id.lblPNum);
			holder.imgBand = (ImageView) convertView.findViewById(R.id.imgNext);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.lblCod.setText(items.get(position).Cod);
		holder.lblDesc.setText(items.get(position).Desc);

		holder.imgBand.setVisibility(View.INVISIBLE);
		if (items.get(position).Bandera==1) holder.imgBand.setVisibility(View.VISIBLE);

		if (selectedIndex!= -1 && position == selectedIndex) {
			convertView.setBackgroundColor(Color.parseColor("#1B76B9"));
            holder.lblCod.setTextColor(Color.WHITE);
            holder.lblDesc.setTextColor(Color.WHITE);
		} else {
			convertView.setBackgroundColor(Color.TRANSPARENT);
            holder.lblCod.setTextColor(Color.BLACK);
            holder.lblDesc.setTextColor(Color.BLACK);
		}

		return convertView;
	}


	static class ViewHolder {
		TextView  lblCod,lblDesc;
		ImageView  imgBand;
	}

}