package com.dtsgt.ladapt;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dtsgt.base.clsClasses;
import com.dtsgt.mpos.R;

public class ListAdaptDevBod extends BaseAdapter {

	private static ArrayList<clsClasses.clsExist> items;
	private LayoutInflater l_Inflater;

	private int selectedIndex;
	private boolean upeso;

	public ListAdaptDevBod(Context context, ArrayList<clsClasses.clsExist> results) {
		items = results;
		l_Inflater = LayoutInflater.from(context);
		//upeso=usarpeso;
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
		int ic;
		String lote;

		if (convertView == null) {

			convertView = l_Inflater.inflate(R.layout.activity_list_view_exist, null);
			holder = new ViewHolder();

			holder.lblDesc = (TextView) convertView.findViewById(R.id.lblPNum);
			holder.lblCod = (TextView) convertView.findViewById(R.id.textView18);
			holder.lblLote  = (TextView) convertView.findViewById(R.id.lblETipo);
			holder.lblLotem  = (TextView) convertView.findViewById(R.id.textView19);
			holder.lblValor = (TextView) convertView.findViewById(R.id.lblPValor);
			holder.lblValor2 = (TextView) convertView.findViewById(R.id.lblPValor2);
			holder.lblValorM = (TextView) convertView.findViewById(R.id.lblValorM);
			holder.lblValorT = (TextView) convertView.findViewById(R.id.lblValorT);
			holder.lblPeso = (TextView) convertView.findViewById(R.id.lblepeso);
			holder.lblPesoM = (TextView) convertView.findViewById(R.id.lblepesom);
			holder.lblPesoT = (TextView) convertView.findViewById(R.id.lblepesoT);

			holder.reltitle= (RelativeLayout) convertView.findViewById(R.id.relexitit);
			holder.relbueno= (RelativeLayout) convertView.findViewById(R.id.relexist);
			holder.relmalo = (RelativeLayout) convertView.findViewById(R.id.relexistm);
			holder.reltot = (RelativeLayout) convertView.findViewById(R.id.relexistt);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (!upeso) holder.lblValor.setVisibility(View.INVISIBLE);

		holder.lblLote.setText(items.get(position).Cod);
		holder.lblCod.setText(items.get(position).Desc);

		holder.lblPeso.setText(items.get(position).Valor);
		holder.lblValor2.setText(items.get(position).ValorM);
		/*holder.lblLotem.setText(items.get(position).Valor);
		holder.lblPeso.setText(items.get(position).Valor);
		holder.lblValor.setText(items.get(position).Valor);

		holder.lblPesoM.setText(items.get(position).Valor);
		holder.lblValorM.setText(items.get(position).Valor);

		holder.lblPesoT.setText(items.get(position).Valor);
		holder.lblValorT.setText(items.get(position).Valor);*/

		holder.reltitle.setVisibility(View.GONE);
		//holder.relbueno.setVisibility(View.GONE);
		holder.relmalo.setVisibility(View.GONE);
		holder.reltot.setVisibility(View.GONE);

		ic=items.get(position).items;
		lote=items.get(position).Lote;

		switch (items.get(position).flag) {
			case 0:
				holder.reltitle.setVisibility(View.VISIBLE);
				holder.lblCod.setVisibility(View.VISIBLE);break;
			case 1:
				holder.relbueno.setVisibility(View.VISIBLE);break;
			case 2:
				holder.relmalo.setVisibility(View.VISIBLE);break;
			case 3:
				holder.reltot.setVisibility(View.VISIBLE);break;
		}

		if(selectedIndex!= -1 && position == selectedIndex) {
			convertView.setBackgroundColor(Color.rgb(26,138,198));
		} else {
			convertView.setBackgroundColor(Color.TRANSPARENT);
		}

		return convertView;
	}


	static class ViewHolder {
		TextView  lblLote,lblLotem,lblCod,lblDesc,lblValor,lblValor2,lblValorM,lblValorT,lblPeso,lblPesoM,lblPesoT;
		RelativeLayout reltitle,relbueno,relmalo,reltot;
	}

}