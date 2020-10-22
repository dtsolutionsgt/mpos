package com.dtsgt.ladapt;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dtsgt.base.clsClasses.clsVenta;
import com.dtsgt.mpos.Orden;
import com.dtsgt.mpos.R;
import com.dtsgt.mpos.Venta;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ListAdaptOrden extends BaseAdapter {

	public String cursym;

	private static ArrayList<clsVenta> items;
	private Context cont;
	private int selectedIndex;
	private LayoutInflater l_Inflater;
	private DecimalFormat frmdec;
	private Orden owner;

	public ListAdaptOrden(Context context, Orden owner, ArrayList<clsVenta> results) {
		items = results;
		l_Inflater = LayoutInflater.from(context);
		cont=context;
		selectedIndex = -1;
		frmdec = new DecimalFormat("#,##0.00");
		this.owner=owner;
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
	
		if (convertView == null) {
			
			convertView = l_Inflater.inflate(R.layout.activity_list_view_venta, null);
			holder = new ViewHolder();
			
			holder.lblCod  = (TextView) convertView.findViewById(R.id.lblETipo);
			holder.lblNombre = (TextView) convertView.findViewById(R.id.lblCFact);
			holder.lblCant = (TextView) convertView.findViewById(R.id.lblCant);
			holder.lblPrec = (TextView) convertView.findViewById(R.id.lblPNum);
			holder.lblDesc = (TextView) convertView.findViewById(R.id.lblFecha);
			holder.lblTot = (TextView) convertView.findViewById(R.id.lblTot);
			holder.lblPeso = (TextView) convertView.findViewById(R.id.lblPeso);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
							
		holder.lblCod.setText(items.get(position).Cod);
		holder.lblNombre.setText(items.get(position).Nombre);
		
		//val=items.get(position).Cant;
		//holder.lblCant.setText(frmdec.format(val)+" "+items.get(position).um);
		holder.lblCant.setText(items.get(position).val);
		//val=items.get(position).Prec;
		//holder.lblPrec.setText(frmdec.format(val));
		holder.lblPrec.setText("");
		holder.lblDesc.setText(items.get(position).sdesc);
		val=items.get(position).Total;
		holder.lblTot.setText(cursym+" "+frmdec.format(val));

		holder.lblPeso.setText(items.get(position).valp);
		if (items.get(position).valp.equalsIgnoreCase(".")) holder.lblPeso.setVisibility(View.GONE);
		if (items.get(position).Peso==0) holder.lblPeso.setVisibility(View.GONE);

		holder.lblDesc.setOnClickListener(new CustomOnClickListener( position,1));
		holder.lblTot.setOnClickListener(new CustomOnClickListener( position,2));

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
            holder.lblCod.setTextColor(Color.parseColor("#1B76B9"));
            holder.lblNombre.setTextColor(Color.parseColor("#1B76B9"));
            holder.lblCant.setTextColor(Color.parseColor("#1B76B9"));
            holder.lblPrec.setTextColor(Color.parseColor("#1B76B9"));
            holder.lblDesc.setTextColor(Color.parseColor("#1B76B9"));
            holder.lblTot.setTextColor(Color.parseColor("#1B76B9"));
            holder.lblPeso.setTextColor(Color.parseColor("#1B76B9"));
		}
		
		return convertView;
	}
	
	static class ViewHolder {
		TextView  lblCod, lblNombre,lblCant,lblPrec,lblDesc,lblTot,lblPeso;
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