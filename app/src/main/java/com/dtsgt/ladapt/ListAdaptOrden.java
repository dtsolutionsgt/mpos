package com.dtsgt.ladapt;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dtsgt.base.clsClasses;
import com.dtsgt.base.clsClasses.clsVenta;
import com.dtsgt.mpos.Orden;
import com.dtsgt.mpos.R;
import com.dtsgt.mpos.Venta;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ListAdaptOrden extends BaseAdapter {

	public String cursym;

	private static ArrayList<clsClasses.clsOrden> items;
	private Context cont;
	private int selectedIndex;
	private LayoutInflater l_Inflater;
	private DecimalFormat frmdec;
	private Orden owner;
	private boolean horiz;

	public ListAdaptOrden(Context context, Orden owner, ArrayList<clsClasses.clsOrden> results,boolean horizdir) {
		items = results;
		l_Inflater = LayoutInflater.from(context);
		cont=context;
		selectedIndex = -1;
		frmdec = new DecimalFormat("#,##0.00");
		this.owner=owner;
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
		double val;
		int tcolor;
	
		if (convertView == null) {

		    if (horiz) {
                convertView = l_Inflater.inflate(R.layout.activity_list_view_orden, null);
            } else {
                convertView = l_Inflater.inflate(R.layout.activity_list_view_orden_ver, null);
            }

			holder = new ViewHolder();
			
			holder.lblCod  = (TextView) convertView.findViewById(R.id.lblETipo);
			holder.lblNombre = (TextView) convertView.findViewById(R.id.lblCFact);
			holder.lblCant = (TextView) convertView.findViewById(R.id.lblCant);
			holder.lblPrec = (TextView) convertView.findViewById(R.id.lblPNum);
			holder.lblDesc = (TextView) convertView.findViewById(R.id.lblFecha);
			holder.lblTot = (TextView) convertView.findViewById(R.id.lblTot);
			holder.lblPeso = (TextView) convertView.findViewById(R.id.lblPeso);
            holder.imgFlag = (ImageView) convertView.findViewById(R.id.imageView103);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
							
		holder.lblCod.setText(""+items.get(position).cuenta);
		holder.lblNombre.setText(items.get(position).Nombre);
		if (items.get(position).indent) holder.lblNombre.setPadding(50,0,0,0);else holder.lblNombre.setPadding(0,0,0,0);
		//holder.lblCant.setText(items.get(position).icant+" x "+cursym+items.get(position).sdesc);
		if (items.get(position).modif) holder.lblCant.setVisibility(View.VISIBLE);else holder.lblCant.setVisibility(View.INVISIBLE);
		holder.lblPrec.setText("");
        val=items.get(position).Total;
		holder.lblDesc.setText(cursym+" "+frmdec.format(val));
		holder.lblTot.setText("");
		holder.lblPeso.setText(" ");
		holder.lblDesc.setOnClickListener(new CustomOnClickListener( position,1));
		holder.lblTot.setOnClickListener(new CustomOnClickListener( position,2));

		if (items.get(position).estado==1) {
		    holder.imgFlag.setVisibility(View.VISIBLE);
        } else {
		    holder.imgFlag.setVisibility(View.INVISIBLE);
        }

		if(selectedIndex!= -1 && position == selectedIndex) {
			convertView.setBackgroundColor(Color.rgb(26,138,198));
            holder.lblCod.setTextColor(Color.WHITE);
            holder.lblNombre.setTextColor(Color.WHITE);
            holder.lblCant.setTextColor(Color.WHITE);
            holder.lblPrec.setTextColor(Color.WHITE);
            holder.lblDesc.setTextColor(Color.WHITE);
            holder.lblTot.setTextColor(Color.WHITE);
            holder.lblPeso.setTextColor(Color.WHITE);
        } else {
        	convertView.setBackgroundColor(Color.TRANSPARENT);
			tcolor=Color.parseColor("#202020");  //Color.parseColor("#1B76B9")
            holder.lblCod.setTextColor(tcolor);
            holder.lblNombre.setTextColor(tcolor);
            holder.lblCant.setTextColor(tcolor);
            holder.lblPrec.setTextColor(tcolor);
            holder.lblDesc.setTextColor(tcolor);
            holder.lblTot.setTextColor(tcolor);
            holder.lblPeso.setTextColor(tcolor);
		}
		
		return convertView;
	}
	
	static class ViewHolder {
		TextView  lblCod, lblNombre,lblCant,lblPrec,lblDesc,lblTot,lblPeso;
		ImageView imgFlag;
	}

	public class CustomOnClickListener implements OnClickListener {
		private int position,handle;

		public CustomOnClickListener(int pos, int handle) {
			position = pos;
			this.handle=handle;
		}

		@Override
		public void onClick(View v) {
			owner.subItemClick(position,handle);
		}
	}

}