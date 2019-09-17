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

public class ListReportVenta extends BaseAdapter {

    private static ArrayList<clsClasses.clsReport> items;
    private LayoutInflater l_Inflater;

    private int selectedIndex;
    private String err;
    private boolean upeso;

    public ListReportVenta(Context context, ArrayList<clsClasses.clsReport> results) {
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
        int ic;
        String lote;

        if (convertView == null) {

            convertView = l_Inflater.inflate(R.layout.activity_list_view_exist, null);
            holder = new ViewHolder();

            holder.lblCorel = (TextView) convertView.findViewById(R.id.textView18);

            holder.lblDesc = (TextView) convertView.findViewById(R.id.lblPNum);
            holder.lblSerie  = (TextView) convertView.findViewById(R.id.lblETipo);
            holder.lblLotem  = (TextView) convertView.findViewById(R.id.textView19);
            holder.lblValor = (TextView) convertView.findViewById(R.id.lblPValor);
            holder.lblValor2 = (TextView) convertView.findViewById(R.id.lblPValor2);
            holder.lblValorM = (TextView) convertView.findViewById(R.id.lblValorM);
            holder.lblValorT = (TextView) convertView.findViewById(R.id.lblValorT);
            holder.lblCorr = (TextView) convertView.findViewById(R.id.lblepeso);
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

        try{

            holder.lblCorel.setText(""+items.get(position).corel);
            holder.lblSerie.setText(""+items.get(position).serie);
            holder.lblCorr.setText(""+items.get(position).correl);
        }catch (Exception e){
            err =""+ e;
        }

        holder.lblDesc.setVisibility(View.GONE);
        holder.lblLotem.setVisibility(View.GONE);
        holder.lblValor.setVisibility(View.GONE);
        holder.lblPesoM.setVisibility(View.GONE);
        holder.lblValorM.setVisibility(View.GONE);
        holder.lblPesoT.setVisibility(View.GONE);
        holder.lblValorT.setVisibility(View.GONE);
        holder.relmalo.setVisibility(View.GONE);
        holder.reltot.setVisibility(View.GONE);
        holder.lblValor2.setVisibility(View.GONE);

        if(selectedIndex!= -1 && position == selectedIndex) {
            convertView.setBackgroundColor(Color.rgb(26,138,198));
        } else {
            convertView.setBackgroundColor(Color.TRANSPARENT);
        }

        return convertView;
    }


    static class ViewHolder {
        TextView  lblSerie,lblLotem,lblCorel,lblDesc,lblValor2,lblValor,lblValorM,lblValorT,lblCorr,lblPesoM,lblPesoT;
        RelativeLayout reltitle,relbueno,relmalo,reltot;
    }

}