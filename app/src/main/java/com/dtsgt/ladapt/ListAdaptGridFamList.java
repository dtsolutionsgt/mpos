package com.dtsgt.ladapt;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dtsgt.base.clsClasses.clsMenu;
import com.dtsgt.mpos.R;

import java.io.File;
import java.util.ArrayList;

public class ListAdaptGridFamList extends BaseAdapter {
	private ArrayList<clsMenu> items;

	private int selectedIndex;
    private String imgpath;
	private LayoutInflater l_Inflater;
    private boolean horiz;

	public ListAdaptGridFamList(Context context, ArrayList<clsMenu> results, String imgfold,boolean horizdir) {
		items = results;
		l_Inflater = LayoutInflater.from(context);
		selectedIndex = -1;
        imgpath=imgfold;
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
		int col;
		String cstr;

		if (convertView == null) {
            if (horiz) {
                convertView = l_Inflater.inflate(R.layout.activity_list_view_gridventa_lista, null);
            } else {
                convertView = l_Inflater.inflate(R.layout.activity_list_view_gridventa_lista_ver, null);
            }

			holder = new ViewHolder();
			holder.lblName = convertView.findViewById(R.id.lblTrat);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
			
		holder.lblName.setText(items.get(position).Name);

        if(selectedIndex!= -1 && position == selectedIndex) {
            holder.lblName.setTextColor(Color.WHITE);
            convertView.setBackgroundColor(Color.rgb(26,138,198));
        } else {
            holder.lblName.setTextColor(Color.parseColor("#1B76B9"));

            convertView.setBackgroundColor(Color.TRANSPARENT);
            try {
                cstr=items.get(position).val;
                col=Color.parseColor(cstr);
                convertView.setBackgroundColor(col);
            } catch (Exception e) {
                convertView.setBackgroundColor(Color.TRANSPARENT);
            }
        }

		return convertView;
	}

	static class ViewHolder {
		TextView  lblName;
	}
	
}
