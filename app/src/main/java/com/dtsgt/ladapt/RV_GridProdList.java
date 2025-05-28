package com.dtsgt.ladapt;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dtsgt.base.clsClasses.clsMenu;
import com.dtsgt.mpos.R;

import java.util.List;

public class RV_GridProdList extends RecyclerView.Adapter<RV_GridProdList.MyViewHolder> {

    public List<clsMenu> items;

    public static int selectedItemPosition=-1;


    public RV_GridProdList(List<clsMenu> itemList) {
        this.items = itemList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView lbl1;
        public View rootLayout;

        public MyViewHolder(View view) {
            super(view);
            rootLayout = itemView;
            lbl1 = view.findViewById(R.id.lbl1);
        }

        public void bind(clsMenu item, int position) {
            lbl1.setText(""+item.Name);

            if (selectedItemPosition == position) {
                rootLayout.setBackgroundColor(Color.LTGRAY);
            } else {
                rootLayout.setBackgroundColor(Color.WHITE);
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
    public RV_GridProdList.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rv_gridventa_lista, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RV_GridProdList.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
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

}
