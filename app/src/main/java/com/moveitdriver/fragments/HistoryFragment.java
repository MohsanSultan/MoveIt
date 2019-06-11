package com.moveitdriver.fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.moveitdriver.R;
import com.moveitdriver.adapters.GetAllBookingHistoryAdapter;
import com.moveitdriver.models.bookingHistoryResponce.BookingHistoryModelResponse;
import com.moveitdriver.models.bookingHistoryResponce.Datum;
import com.moveitdriver.retrofit.RestHandler;
import com.moveitdriver.retrofit.RetrofitListener;
import com.moveitdriver.utils.Constants;
import com.moveitdriver.utils.SharedPrefManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class HistoryFragment extends Fragment implements RetrofitListener {

    private View view;
    private TextView averageRatingTextView, totalRatingTextView;
    private Context context;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private GetAllBookingHistoryAdapter adapter;
    private BookingHistoryModelResponse obj;
    private List<Datum> bHistoryList;

    private ProgressDialog pDialog;
    private RestHandler restHandler;

    public HistoryFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public HistoryFragment(Context context) {
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_history, container, false);

        pDialog = new ProgressDialog(context);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);

        restHandler = new RestHandler(context, HistoryFragment.this);

        recyclerView = view.findViewById(R.id.recycler_view_booking_history_fragment);
        layoutManager = new GridLayoutManager(context, 1);
        recyclerView.setLayoutManager(layoutManager);

        getBookingHistoryData();

        return view;
    }

    // -----------------------------        Functions         ------------------------------------//

    private void getBookingHistoryData() {
        pDialog.show();
        restHandler.makeHttpRequest(restHandler.retrofit.create(RestHandler.RestInterface.class).getBookingHistory(SharedPrefManager.getInstance(context).getDriverId(), "driver", "History"), "BookingHistory");
    }

    // -------------------------       Override Functions         --------------------------------//

    @Override
    public void onSuccess(Call call, Response response, String method) {
        if (response != null && response.code() == 200) {
            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();
            }
            if (method.equalsIgnoreCase("BookingHistory")) {
                obj = (BookingHistoryModelResponse) response.body();

                if (obj.getData().size() != 0) {
                    bHistoryList = obj.getData();

                    adapter = new GetAllBookingHistoryAdapter(context, bHistoryList);
                    recyclerView.setAdapter(adapter);
                } else {

                }
            }
        } else if (response != null && (response.code() == 403 || response.code() == 500)) {
            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();
            }

            try {
                ResponseBody body = response.errorBody();
                JSONObject jObj = new JSONObject(body.string());
                if (jObj.optString("status").equals("403"))
                    Constants.showAlert(context, jObj.optString("message"));
                else
                    Constants.showAlert(context, jObj.optString("message"));
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (JSONException e1) {
                Constants.showAlert(context, "Oops! API returned invalid response. Try again later.");
                e1.printStackTrace();
            }
        }
    }

    @Override
    public void onFailure(String errorMessage) {
        if (pDialog != null && pDialog.isShowing()) {
            pDialog.dismiss();
        }
        Constants.showAlert(context, errorMessage);
    }
}