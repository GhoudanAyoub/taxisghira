package com.TaxiSghira.TreeProg.plashscreen.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.TaxiSghira.TreeProg.plashscreen.Module.Chifor;
import com.TaxiSghira.TreeProg.plashscreen.R;

import java.util.ArrayList;
import java.util.List;

public class FavorAdapter extends RecyclerView.Adapter<FavorAdapter.FavorAdapterHolder> {
    private List<Chifor> favorList = new ArrayList<>();
    private Context context;
    public FavorAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public FavorAdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FavorAdapterHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.favor_driver_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FavorAdapterHolder holder, int position) {
        holder.TaxiNum.setText(favorList.get(position).getTaxi_NUM());
        holder.Ch_Num.setText(favorList.get(position).getPhone());
        holder.Ch_Name.setText(favorList.get(position).getFullname());
        holder.ImageView.setOnClickListener(v ->context.startActivity(new Intent(Intent.ACTION_DIAL).setData(Uri.parse("tel:"+favorList.get(position).getPhone()))));
    }

    @Override
    public int getItemCount() {
        return favorList.size();
    }

    public void setList(List<Chifor> moviesList) {
        this.favorList = moviesList;
        notifyDataSetChanged();
    }

    static class FavorAdapterHolder extends RecyclerView.ViewHolder {
        TextView Ch_Name,TaxiNum,Ch_Num;
        ImageView ImageView;
        FavorAdapterHolder(@NonNull View itemView) {
            super(itemView);
            Ch_Name = itemView.findViewById(R.id.Ch_Name);
            Ch_Num = itemView.findViewById(R.id.Ch_Num);
            TaxiNum = itemView.findViewById(R.id.TaxiNum);
            ImageView = itemView.findViewById(R.id.imageViewcall);
        }
    }
}