package com.moveitdriver.retrofit;

import android.content.Context;

import com.moveitdriver.R;
import com.moveitdriver.models.OTPResponse.OTPResponse;
import com.moveitdriver.models.addVehicleDetailResponse.AddVehicleModelResponse;
import com.moveitdriver.models.carMakesResponse.MakesResponse;
import com.moveitdriver.models.carModelsResponse.ModelResponse;
import com.moveitdriver.models.forgotPasswordResponse.ForgotPassword;
import com.moveitdriver.models.getAllVehicleResponse.GetAllVehicleModelResponse;
import com.moveitdriver.models.loginResponse.LoginResponse;
import com.moveitdriver.models.registerationResponse.RegisterResponse;
import com.moveitdriver.models.updateUserDetailResponse.UpdateUserModelResponse;
import com.moveitdriver.utils.Constants;

import java.io.IOException;
import java.net.NoRouteToHostException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
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
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

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
                                      @Field("device_type") String device_type, @Field("role") String role,
                                      @Field("latitude") String latitude, @Field("longitude") String longitude);

        @POST("registerUser")
        @Multipart
        Call<RegisterResponse> userRegister(@Part("firstname") RequestBody firstName, @Part("lastname") RequestBody lastName,
                                            @Part("email") RequestBody email, @Part("password") RequestBody password, @Part("country") RequestBody country,
                                            @Part("country_code") RequestBody countryCode, @Part("contact") RequestBody contactNumber,
                                            @Part("device_type") RequestBody deviceType, @Part("reg_type") RequestBody regType,
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

        @GET("makes")
        Call<MakesResponse> getMakes();

        @GET("get/model/{id}")
        Call<ModelResponse> getModels(@Path("id") String id);

        @POST("vehicle/add")
        @Multipart
        Call<AddVehicleModelResponse> addVehicleDetail(@Part("user_id") RequestBody userId,
                                                       @Part("make") RequestBody make,
                                                       @Part("model") RequestBody model,
                                                       @Part("carYear") RequestBody year,
                                                       @Part("carColor") RequestBody color,
                                                       @Part MultipartBody.Part fImage,
                                                       @Part MultipartBody.Part bImage2);

        @POST("vehicle/edit")
        @Multipart
        Call<AddVehicleModelResponse> editInsuranceVehicleDetail(@Part("id") RequestBody vehicleId,
                                                                 @Part("user_id") RequestBody userId,
                                                                 @Part("vehicleInsuranceCompanyName") RequestBody companyName,
                                                                 @Part("vehicleInsuranceType") RequestBody type,
                                                                 @Part("vehicleInspectionReport") RequestBody report,
                                                                 @Part("vehicleInsuranceCertificateExpires") RequestBody certificateExpires,
                                                                 @Part("vehicleInsuranceEffectiveDate") RequestBody effectiveDate,
                                                                 @Part MultipartBody.Part vehicleInsuranceCertificateImage);

        @POST("vehicle/edit")
        @Multipart
        Call<AddVehicleModelResponse> editRegistrationVehicleDetail(@Part("id") RequestBody vehicleId,
                                                                    @Part("user_id") RequestBody userId,
                                                                    @Part("registrationNumber") RequestBody registrationNumber,
                                                                    @Part("registrationDate") RequestBody type,
                                                                    @Part("registrationExpiry") RequestBody report);

        @GET("allvehicle/{id}")
        Call<GetAllVehicleModelResponse> getAllVehicles(@Path("id") String userId);

        @POST("user/update")
        @Multipart
        Call<UpdateUserModelResponse> updateUserDriverLicence(@Part("id") RequestBody userId,
                                                              @Part("driverLicenceNumber") RequestBody licenceNumber,
                                                              @Part("driverLicenceExpires") RequestBody licenceExpires,
                                                              @Part("driverLicenceState") RequestBody licenceState,
                                                              @Part("validVehicleTypeLiscence") RequestBody licenceType,
                                                              @Part("validLiscenseExpires") RequestBody validLicenceExpires,
                                                              @Part MultipartBody.Part licenceFrontPic,
                                                              @Part MultipartBody.Part licenceBackPic);

        @POST("user/update")
        @Multipart
        Call<UpdateUserModelResponse> updateUserProfile(@Part("id") RequestBody userId,
                                                        @Part("firstname") RequestBody firstName,
                                                        @Part("lastname") RequestBody lastName,
                                                        @Part MultipartBody.Part driverProfileImage);
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