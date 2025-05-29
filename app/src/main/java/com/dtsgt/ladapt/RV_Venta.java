package com.dtsgt.ladapt;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dtsgt.base.clsClasses;
import com.dtsgt.base.clsClasses.clsVenta;
import com.dtsgt.mpos.R;
import com.dtsgt.mpos.Venta;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;

public class RV_Venta extends RecyclerView.Adapter<RV_Venta.MyViewHolder> {

    public List<clsVenta> items;

    public String cursym;
    private DecimalFormat frmdec;

    public static int selectedItemPosition=-1;
     private File file;

    public RV_Venta(List<clsVenta> itemList) {
        this.items = itemList;
        frmdec = new DecimalFormat("#,##0.00");
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public View rootLayout;
        public TextView lblCod;
        public TextView lblNombre;
        public TextView lblCant;
        public TextView lblPrec;
        public TextView lblDesc;
        public TextView lblTot;
        public TextView lblPeso;

        double valtot;

        public MyViewHolder(View view) {
            super(view);
            rootLayout = itemView;

            lblCod  = view.findViewById(R.id.lblETipo);
            lblNombre = view.findViewById(R.id.lblCFact);
            lblCant = view.findViewById(R.id.lblCant);
            lblPrec = view.findViewById(R.id.lblPNum);
            lblDesc = view.findViewById(R.id.lblFecha);
            lblTot = view.findViewById(R.id.lblTot);
            lblPeso = view.findViewById(R.id.lblPeso);

        }

        public void bind(clsVenta item, int position) {
            lblCod.setText(item.Cod);
            lblNombre.setText(item.Nombre);

            lblCant.setText(item.val);
            lblPrec.setText("");
            lblDesc.setText(cursym+" "+item.sdesc);
            valtot=item.Total;
            lblTot.setText(cursym+" "+frmdec.format(valtot));
            lblPeso.setText("Descuento ("+frmdecno(item.Desc)+" %)  :           "+cursym+item.valp);

            if (item.valp.equalsIgnoreCase(".")) {
               lblPeso.setVisibility(View.GONE);
            } else {
                lblPeso.setVisibility(View.VISIBLE);
            }

            if (selectedItemPosition == position) {
                rootLayout.setBackgroundColor(Color.rgb(26,138,198));
                lblCod.setTextColor(Color.WHITE);
                lblNombre.setTextColor(Color.WHITE);
                lblCant.setTextColor(Color.WHITE);
                lblPrec.setTextColor(Color.WHITE);
                lblDesc.setTextColor(Color.WHITE);
                lblTot.setTextColor(Color.WHITE);
                lblPeso.setTextColor(Color.WHITE);
            } else {
                rootLayout.setBackgroundColor(Color.TRANSPARENT);
                lblCod.setTextColor(Color.parseColor("#1B76B9"));
                lblNombre.setTextColor(Color.parseColor("#1B76B9"));
                lblCant.setTextColor(Color.parseColor("#1B76B9"));
                lblPrec.setTextColor(Color.parseColor("#1B76B9"));
                lblDesc.setTextColor(Color.parseColor("#1B76B9"));
                lblTot.setTextColor(Color.parseColor("#1B76B9"));
                lblPeso.setTextColor(Color.parseColor("#1B76B9"));
            }

            rootLayout.setOnClickListener(v -> {
                int previousPosition = selectedItemPosition;
                selectedItemPosition =  getAbsoluteAdapterPosition();

                notifyItemChanged(previousPosition);
                notifyItemChanged(selectedItemPosition);
            });

            rootLayout.setOnLongClickListener(v -> {
                int previousPosition = selectedItemPosition;
                selectedItemPosition =  getAbsoluteAdapterPosition();

                notifyItemChanged(previousPosition);
                notifyItemChanged(selectedItemPosition);

                return false;
            });
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_list_view_venta, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RV_Venta.MyViewHolder holder, int position) {
        holder.bind(items.get(position), position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setSelectedIndex(int selpos) {
        int previousSelectedPosition = selectedItemPosition;
        selectedItemPosition = selpos;

        notifyItemChanged(previousSelectedPosition);
        notifyItemChanged(selpos);
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    private String frmdecno(double val) {
        return frmdec.format(val);
    }
}
