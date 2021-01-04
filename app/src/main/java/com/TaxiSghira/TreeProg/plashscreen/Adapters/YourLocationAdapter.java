package com.TaxiSghira.TreeProg.plashscreen.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.TaxiSghira.TreeProg.plashscreen.Module.YourLocations;
import com.TaxiSghira.TreeProg.plashscreen.R;

import java.util.ArrayList;
import java.util.List;

public class YourLocationAdapter extends RecyclerView.Adapter<YourLocationAdapter.LocationHolder> {

    private List<YourLocations> YourLocationsList = new ArrayList<>();

    @NonNull
    @Override
    public LocationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LocationHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.locationlayout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull LocationHolder holder, int position) {
        holder.textView.setText(YourLocationsList.get(position).getLocation_String());
    }

    @Override
    public int getItemCount() {
        return YourLocationsList.size();
    }

    public void setList(List<YourLocations> YourLocationsList) {
        this.YourLocationsList = YourLocationsList;
        notifyDataSetChanged();
    }

    public class LocationHolder extends RecyclerView.ViewHolder {

        TextView textView;
        public LocationHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.locationNameTxt);
        }
    }
}