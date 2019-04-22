package com.moveitdriver.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.moveitdriver.R;
import com.moveitdriver.models.allRatingResponse.Review;
import com.moveitdriver.models.getAllVehicleResponse.Datum;

import java.util.List;

public class GetAllRatingAdapter extends RecyclerView.Adapter<GetAllRatingAdapter.MyViewHolder> {

    Context context;
    List<Review> arrayList;

    public GetAllRatingAdapter(Context context, List<Review> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.rating_list_view_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.riderNameTextView.setText(arrayList.get(position).getRatingFrom().getFirstname()+" "+arrayList.get(position).getRatingFrom().getLastname());
        holder.rideDateTextView.setText(arrayList.get(position).getCreatedAt());
        holder.riderCommentTextView.setText(arrayList.get(position).getComment());
        holder.ratingBar.setRating(arrayList.get(position).getRating());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView riderNameTextView, rideDateTextView, riderCommentTextView;
        public RatingBar ratingBar;

        public MyViewHolder(View view) {
            super(view);
            riderNameTextView = view.findViewById(R.id.rider_name_text_view);
            rideDateTextView = view.findViewById(R.id.ride_date_text_view);
            riderCommentTextView = view.findViewById(R.id.comment_text_view);
            ratingBar = view.findViewById(R.id.rating_bar);
        }
    }
}