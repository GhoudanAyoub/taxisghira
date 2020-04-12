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

import com.TaxiSghira.TreeProg.plashscreen.Both.Auth;
import com.TaxiSghira.TreeProg.plashscreen.Module.Favor;
import com.TaxiSghira.TreeProg.plashscreen.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class Favor_Adpter extends RecyclerView.Adapter<Favor_Adpter.ImageViewHolder2> {
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Favor");
    Favor favor;
    private Context mContext;
    private List<Favor> mUploads;


    public Favor_Adpter(Context context, List<Favor> uploads) {
        this.mUploads = uploads;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ImageViewHolder2 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.favor_driver_layout, parent, false);
        return new Favor_Adpter.ImageViewHolder2(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder2 holder, int position) {
         favor = mUploads.get(position);

        databaseReference.orderByChild("id").equalTo(Auth.gmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot dataSnapshot1 :dataSnapshot.getChildren()){
                        favor = dataSnapshot1.getValue(Favor.class);
                        holder.TaxiNum.setText(favor.Taxi_num);
                        holder.Ch_Num.setText(favor.Ch_num);
                        holder.Ch_Name.setText(favor.Ch_Name);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        holder.ImageView.setOnClickListener(v -> {
           mContext.startActivity(new Intent(Intent.ACTION_DIAL).setData(Uri.parse("tel:"+favor.Ch_num)));
        });

    }

    @Override
    public int getItemCount() { return mUploads.size();}

    public class ImageViewHolder2 extends RecyclerView.ViewHolder {
        TextView Ch_Name,TaxiNum,Ch_Num;
        ImageView ImageView;
        public ImageViewHolder2(@NonNull View itemView) {
            super(itemView);
            Ch_Name = itemView.findViewById(R.id.Ch_Name);
            Ch_Num = itemView.findViewById(R.id.Ch_Num);
            TaxiNum = itemView.findViewById(R.id.TaxiNum);
            ImageView = itemView.findViewById(R.id.imageViewcall);
        }
    }
}
