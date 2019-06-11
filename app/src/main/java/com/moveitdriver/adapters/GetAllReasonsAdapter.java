package com.moveitdriver.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.moveitdriver.R;
import com.moveitdriver.activities.MainActivity;
import com.moveitdriver.models.CancelReasonResponce.Datum;
import com.moveitdriver.models.allRatingResponse.Review;

import java.util.List;

public class GetAllReasonsAdapter extends RecyclerView.Adapter<GetAllReasonsAdapter.MyViewHolder> {

    Context context;
    List<Datum> arrayList;

    public GetAllReasonsAdapter(Context context, List<Datum> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_reason_item_view_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.cancelReasonTextView.setText(arrayList.get(position).getReason());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) context).CancelBooking(arrayList.get(position).getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView cancelReasonTextView;

        public MyViewHolder(View view) {
            super(view);
            cancelReasonTextView = view.findViewById(R.id.cancel_reason_text_view);
        }
    }
}