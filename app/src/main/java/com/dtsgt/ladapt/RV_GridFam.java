package com.dtsgt.ladapt;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dtsgt.base.clsClasses;
import com.dtsgt.base.clsClasses.clsMenu;


import java.util.List;

public class RV_GridFam extends RecyclerView.Adapter<RV_GridFam.MyViewHolder> {


    public List<clsMenu> items;
    public static int selectedItemPosition=-1;

    private static RV_GridFamList.OnItemClickListener listener;


    public RV_GridFam(List<clsMenu> itemList) {
        this.items = itemList;
    }

    public RV_GridFam(List<clsMenu> itemList, RV_GridFamList.OnItemClickListener listener) {
        this.items = itemList;
        this.listener = listener;
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;

        public MyViewHolder(View view) {
            super(view);
            //textView = view.findViewById(R.id.textViewItem);
        }
    }

    @NonNull
    @Override
    public RV_GridFam.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RV_GridFam.MyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }


    public void setSelectedIndex(int selpos) {
        int previousSelectedPosition = selectedItemPosition;
        selectedItemPosition = selpos;

        notifyItemChanged(previousSelectedPosition);
        notifyItemChanged(selpos);
    }

}
