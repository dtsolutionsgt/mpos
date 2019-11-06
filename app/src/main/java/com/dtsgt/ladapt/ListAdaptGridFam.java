package com.dtsgt.ladapt;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dtsgt.base.clsClasses.clsMenu;
import com.dtsgt.mpos.R;

import java.io.File;
import java.util.ArrayList;

public class ListAdaptGridFam extends BaseAdapter {
	private ArrayList<clsMenu> items;

	private int selectedIndex;
    private String imgpath;
	private LayoutInflater l_Inflater;

	public ListAdaptGridFam(Context context, ArrayList<clsMenu> results,String imgfold) {
		items = results;
		l_Inflater = LayoutInflater.from(context);
		selectedIndex = -1;
        imgpath=imgfold;
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
        File file;

		if (convertView == null) {
			convertView = l_Inflater.inflate(R.layout.activity_list_view_gridventa, null);
			holder = new ViewHolder();
			
			holder.imgEst = (ImageView) convertView.findViewById(R.id.imgNext);
			holder.lblName = (TextView) convertView.findViewById(R.id.lblTrat);
		
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
			
		holder.lblName.setText(items.get(position).Name);
        holder.imgEst.setImageResource(R.drawable.blank256);

        try {
            String prodimg = imgpath+"familia/"+items.get(position).Cod+".png";
            file = new File(prodimg);
            if (file.exists()) {
                Bitmap bmImg = BitmapFactory.decodeFile(prodimg);
                holder.imgEst.setImageBitmap(bmImg);
            } else {
                prodimg = imgpath+"familia/"+items.get(position).Cod+".jpg";
                 file = new File(prodimg);
                if (file.exists()) {
                    Bitmap bmImg = BitmapFactory.decodeFile(prodimg);
                    holder.imgEst.setImageBitmap(bmImg);
                }
            }
        } catch (Exception e) {
            holder.imgEst.setImageResource(R.drawable.blank256);
        }

		if(selectedIndex!= -1 && position == selectedIndex) {
			convertView.setBackgroundColor(Color.rgb(26,138,198));
        } else {
        	convertView.setBackgroundColor(Color.TRANSPARENT);
        }
		
		
		return convertView;
	}

	static class ViewHolder {
		ImageView imgEst;
		TextView  lblName;
	}
	
}
