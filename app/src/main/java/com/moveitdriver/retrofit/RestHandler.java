package com.moveitdriver.retrofit;

import android.content.Context;

import com.moveitdriver.R;
import com.moveitdriver.models.OTPResponse.OTPResponse;
import com.moveitdriver.models.forgotPasswordResponse.ForgotPassword;
import com.moveitdriver.models.loginResponse.LoginResponse;
import com.moveitdriver.models.registerationResponse.RegisterResponse;
import com.moveitdriver.utils.Constants;

import java.io.IOException;
import java.net.NoRouteToHostException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public class RestHandler {

    public Retrofit retrofit;
    RetrofitListener retroListener;
    String method_name;
    Context mContext;

    public RestHandler(Context con, RetrofitListener retroListener) {

        this.retroListener = retroListener;
        mContext = con;

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS).addInterceptor(interceptor)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.base_url)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public interface RestInterface {

        @FormUrlEncoded
        @POST("login")
        Call<LoginResponse> userLogin(@Field("email") String email, @Field("password") String password,
                                      @Field("device_type") String device_type, @Field("device_token") String device_token,
                                      @Field("latitude") String latitude, @Field("longitude") String longitude,
                                      @Field("role") String role);

        @POST("registerDriver")
        @Multipart
        Call<RegisterResponse> userRegister(@Part("firstname") RequestBody firstName, @Part("lastname") RequestBody lastName,
                                            @Part("email") RequestBody email, @Part("password") RequestBody password,
                                            @Part("contact") RequestBody contactNumber, @Part("country") RequestBody country,
                                            @Part("role") RequestBody role);

        @FormUrlEncoded
        @POST("verifyOTP")
        Call<OTPResponse> verifyOTP(@Field("verify_type") String type, @Field("id") String id, @Field("otp") String otp);

        @FormUrlEncoded
        @POST("resendOTP")
        Call<OTPResponse> resendOTP(@Field("verify_type") String type, @Field("id") String id);

        @FormUrlEncoded
        @POST("forgetPassword")
        Call<ForgotPassword> forgetPassword(@Field("email") String email);
    }

    public void makeHttpRequest(Call call, String method) {
        this.method_name = method;
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                retroListener.onSuccess(call, response, method_name);
            }

            @Override
            public void onFailure(Call call, Throwable t) {

                if (t instanceof NoRouteToHostException) {
                    retroListener.onFailure(mContext.getString(R.string.server_unreachable));
                } else if (t instanceof SocketTimeoutException) {
                    retroListener.onFailure(mContext.getString(R.string.timed_out));
                } else if (t instanceof IOException) {
                    retroListener.onFailure(t.getMessage());
                } else
                    retroListener.onFailure(t.getMessage());
            }
        });
    }
}