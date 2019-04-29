package com.moveitdriver.activities;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.moveitdriver.R;
import com.moveitdriver.models.addCardDetailResponse.PaymentInfoResponse;
import com.moveitdriver.models.getAllVehicleResponse.GetAllVehicleModelResponse;
import com.moveitdriver.models.getCardDetailResponse.GetCardDetailModelResponse;
import com.moveitdriver.retrofit.RestHandler;
import com.moveitdriver.retrofit.RetrofitListener;
import com.moveitdriver.utils.Constants;
import com.moveitdriver.utils.SharedPrefManager;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class PaymentCardActivity extends AppCompatActivity implements View.OnClickListener, RetrofitListener {

    private EditText cardNumberEditText, cardExpMonthEditText, cardExpYearEditText, cardCVCEditText;
    private Button submitBtn;
    private String cardNumberStr, cardExpMonthStr, cardExpYearStr, cardCVCStr;
    private String tokenId = "";

    private Card card;

    private ProgressDialog pDialog;
    private RestHandler restHandler;
    private GetCardDetailModelResponse getCardDetailModelResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_card);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getCardDetailModelResponse = (GetCardDetailModelResponse) getIntent().getSerializableExtra("cardDetail");

        // Declare RestHandler...
        restHandler = new RestHandler(this, this);

        // Declare Progress Dialog...
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);

        cardNumberEditText = findViewById(R.id.card_number_edit_text_payment_card_activity);
        cardExpMonthEditText = findViewById(R.id.card_exp_month_edit_text_payment_card_activity);
        cardExpYearEditText = findViewById(R.id.card_exp_year_edit_text_payment_card_activity);
        cardCVCEditText = findViewById(R.id.card_cvc_edit_text_payment_card_activity);

        submitBtn = findViewById(R.id.submit_btn_payment_card_activity);
        submitBtn.setOnClickListener(this);

        try {
            if (!getCardDetailModelResponse.getData().get(0).getCardNumber().equals("")) {
                cardNumberEditText.setText(getCardDetailModelResponse.getData().get(0).getCardNumber());
            }
            if (!getCardDetailModelResponse.getData().get(0).getMonth().equals("")) {
                cardExpMonthEditText.setText(getCardDetailModelResponse.getData().get(0).getMonth());
            }
            if (!getCardDetailModelResponse.getData().get(0).getYear().equals("")) {
                cardExpYearEditText.setText(getCardDetailModelResponse.getData().get(0).getYear());
            }
            if (!getCardDetailModelResponse.getData().get(0).getCvc().equals("")) {
                cardCVCEditText.setText(getCardDetailModelResponse.getData().get(0).getCvc()
                );
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit_btn_payment_card_activity:

                try {
                    cardNumberStr = cardNumberEditText.getText().toString();
                    cardExpMonthStr = cardExpMonthEditText.getText().toString();
                    cardExpYearStr = cardExpYearEditText.getText().toString();
                    cardCVCStr = cardCVCEditText.getText().toString();
                } catch (Exception e) {
                    Toast.makeText(PaymentCardActivity.this, "Kindly Fill All Fields ", Toast.LENGTH_LONG).show();
                }

                card = new Card(
                        cardNumberStr,
                        Integer.parseInt(cardExpMonthStr),
                        Integer.parseInt(cardExpYearStr),
                        cardCVCStr
                );

                pDialog.show();

                card.validateNumber();
                card.validateCVC();

                if (!card.validateCard()) {
                    pDialog.dismiss();
                    // Show errors
                    Toast.makeText(PaymentCardActivity.this, "ERROR...", Toast.LENGTH_SHORT).show();
                } else {
                    Stripe stripe = new Stripe(PaymentCardActivity.this, "pk_test_txTONuxV4B9vWfWVTcBmjvpe");

                    stripe.createToken(
                            card,
                            new TokenCallback() {
                                public void onSuccess(Token token) {
                                    // Send token to your server
                                    Log.e("TokenId", token.getId());
                                    Log.e("TokenType", token.getType());

                                    tokenId = token.getId();

                                    if (getIntent().getStringExtra("path").equals("new")) {
                                        addPaymentMethod(tokenId);
                                    } else if (getIntent().getStringExtra("path").equals("old")) {
                                        updatePaymentMethod(tokenId);
                                    }
                                }

                                public void onError(Exception error) {
                                    // Show localized error message
                                    pDialog.dismiss();
                                    Toast.makeText(PaymentCardActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                    );
                }
                break;
        }
    }

    @Override
    public void onSuccess(Call call, Response response, String method) {
        if (response != null && response.code() == 200) {
            pDialog.dismiss();
            if (method.equalsIgnoreCase("addCardDetail")) {
                PaymentInfoResponse paymentInfoResponse = (PaymentInfoResponse) response.body();

                Constants.NEXT_STEP = "Complete";

                String idStr = SharedPrefManager.getInstance(this).getDriverId();
                String fNameStr = SharedPrefManager.getInstance(this).getDriverFirstName();
                String lNameStr = SharedPrefManager.getInstance(this).getDriverLastName();
                String emailStr = SharedPrefManager.getInstance(this).getDriverEmail();
                String picStr = SharedPrefManager.getInstance(this).getDriverPic();
                String vTypeIdStr = SharedPrefManager.getInstance(this).getVehicleId();
                String contactStr = SharedPrefManager.getInstance(this).getDriverContact();

                SharedPrefManager.getInstance(this).driverLogin(idStr, fNameStr, lNameStr, emailStr, picStr, contactStr, vTypeIdStr, "Complete");

                Toast.makeText(this, paymentInfoResponse.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            } else if (method.equalsIgnoreCase("updateCardDetail")) {
                PaymentInfoResponse paymentInfoResponse = (PaymentInfoResponse) response.body();

                Constants.NEXT_STEP = "Complete";

                Toast.makeText(this, paymentInfoResponse.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        } else if (response != null && (response.code() == 403 || response.code() == 500)) {
            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();
            }

            try {
                ResponseBody body = response.errorBody();
                JSONObject jObj = new JSONObject(body.string());
                if (jObj.optString("status").equals("403"))
                    Constants.showAlert(this, jObj.optString("message"));
                else
                    Constants.showAlert(this, jObj.optString("message"));
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (JSONException e1) {
                Constants.showAlert(this, "Oops! API returned invalid response. Try again later.");
                e1.printStackTrace();
            }
        }
    }

    @Override
    public void onFailure(String errorMessage) {
        if (pDialog != null && pDialog.isShowing()) {
            pDialog.dismiss();
        }
        Constants.showAlert(this, errorMessage);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    // ---------------------------------     FUNCTIONS     --------------------------------------- //
    public void addPaymentMethod(String tokenId) {
        restHandler.makeHttpRequest(restHandler.retrofit.create(RestHandler.RestInterface.class).addCardDetail(SharedPrefManager.getInstance(this).getDriverId(),
                cardNumberStr, cardExpMonthStr, cardExpYearStr, cardCVCStr,
                tokenId, true, "complete",
                "Complete"), "addCardDetail");
    }

    public void updatePaymentMethod(String tokenId) {
        restHandler.makeHttpRequest(restHandler.retrofit.create(RestHandler.RestInterface.class).editCardDetail(SharedPrefManager.getInstance(this).getDriverId(),
                getCardDetailModelResponse.getData().get(0).getId(),
                cardNumberStr, cardExpMonthStr, cardExpYearStr, cardCVCStr,
                tokenId, true, "complete",
                "Complete"), "updateCardDetail");
    }
}