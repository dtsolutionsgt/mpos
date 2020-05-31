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

public class LA_Login extends BaseAdapter {
	private static ArrayList<clsMenu> items;

	private int selectedIndex;

	private LayoutInflater l_Inflater;

	public LA_Login(Context context, ArrayList<clsMenu> results) {
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
			convertView = l_Inflater.inflate(R.layout.activity_la_login, null);
			holder = new ViewHolder();
			
			holder.lblName = (TextView) convertView.findViewById(R.id.lblTrat);
			holder.imgVend = (ImageView) convertView.findViewById(R.id.imageView36);
		
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
			
		holder.lblName.setText(items.get(position).Name);
		String fotoVend = Environment.getExternalStorageDirectory() + "/mPosFotos/Vendedor/" + items.get(position).Cod + ".jpg";

		File file = new File(fotoVend);

		if (file.exists()) {
			Bitmap bmImg = BitmapFactory.decodeFile(fotoVend);
			holder.imgVend.setImageBitmap(bmImg);
		}else{
			fotoVend = Environment.getExternalStorageDirectory() + "/mPosFotos/Vendedor/" + items.get(position).Cod + ".png";

			file = new File(fotoVend);

			if (file.exists()) {
				Bitmap bmImg = BitmapFactory.decodeFile(fotoVend);
				holder.imgVend.setImageBitmap(bmImg);
			}
		}

		if(selectedIndex!= -1 && position == selectedIndex) {
			convertView.setBackgroundColor(Color.rgb(26,138,198));
        } else {
        	convertView.setBackgroundColor(Color.TRANSPARENT);
        }

		return convertView;
	}

	static class ViewHolder {
		TextView  lblName;
		ImageView imgVend;
	}
	
}
