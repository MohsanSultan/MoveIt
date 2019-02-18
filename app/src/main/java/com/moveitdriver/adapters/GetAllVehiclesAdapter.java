package com.moveitdriver.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.moveitdriver.R;
import com.moveitdriver.models.getAllVehicleResponse.Datum;

import java.util.List;

public class GetAllVehiclesAdapter extends RecyclerView.Adapter<GetAllVehiclesAdapter.MyViewHolder> {

    Context context;
    List<Datum> arrayList;

    public GetAllVehiclesAdapter(Context context, List<Datum> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_item_view_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.vehicleNameTextView.setText(arrayList.get(position).getCarModel().getTitle());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, ""+arrayList.get(position).getCarColor(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView vehicleNameTextView;

        public MyViewHolder(View view) {
            super(view);
            vehicleNameTextView = view.findViewById(R.id.vehicle_name_text_view);
        }
    }
}