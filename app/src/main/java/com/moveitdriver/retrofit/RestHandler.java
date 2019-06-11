package com.moveitdriver.retrofit;

import android.content.Context;

import com.moveitdriver.R;
import com.moveitdriver.models.CancelReasonResponce.CancelationReasonModelResponse;
import com.moveitdriver.models.OTPResponse.OTPResponse;
import com.moveitdriver.models.UserDetailResponse.UserDetailModelResponse;
import com.moveitdriver.models.addCardDetailResponse.PaymentInfoResponse;
import com.moveitdriver.models.addVehicleDetailResponse.AddVehicleModelResponse;
import com.moveitdriver.models.allEarningsResponse.EarningsModelResponse;
import com.moveitdriver.models.allRatingResponse.DriverRatingResponse;
import com.moveitdriver.models.bookingHistoryResponce.BookingHistoryModelResponse;
import com.moveitdriver.models.carMakesResponse.MakesResponse;
import com.moveitdriver.models.carModelsResponse.ModelResponse;
import com.moveitdriver.models.forgotPasswordResponse.ForgotPassword;
import com.moveitdriver.models.getAllVehicleResponse.GetAllVehicleModelResponse;
import com.moveitdriver.models.getCardDetailResponse.GetCardDetailModelResponse;
import com.moveitdriver.models.getInvoiceResponse.GetInvoiceResponse;
import com.moveitdriver.models.loginResponse.LoginResponse;
import com.moveitdriver.models.registerationResponse.RegisterResponse;
import com.moveitdriver.models.setRatingUserResponse.RatingResponse;
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
                                            @Part("role") RequestBody role, @Part("next_step") RequestBody nextStep);

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
                                                       @Part("next_step") RequestBody nextStep,
                                                       @Part MultipartBody.Part fImage,
                                                       @Part MultipartBody.Part bImage,
                                                       @Part MultipartBody.Part lImage,
                                                       @Part MultipartBody.Part rImage);

        @POST("vehicle/edit")
        @Multipart
        Call<AddVehicleModelResponse> editVehicleDetail(@Part("id") RequestBody vehicleId,
                                                        @Part("user_id") RequestBody userId,
                                                        @Part("make") RequestBody make,
                                                        @Part("model") RequestBody model,
                                                        @Part("carYear") RequestBody year,
                                                        @Part("carColor") RequestBody color,
                                                        @Part("next_step") RequestBody nextStep,
                                                        @Part MultipartBody.Part fImage,
                                                        @Part MultipartBody.Part bImage,
                                                        @Part MultipartBody.Part lImage,
                                                        @Part MultipartBody.Part rImage);

        @POST("vehicle/edit")
        @Multipart
        Call<AddVehicleModelResponse> editInsuranceVehicleDetail(@Part("id") RequestBody vehicleId,
                                                                 @Part("user_id") RequestBody userId,
                                                                 @Part("vehicleInsureName") RequestBody insureName,
                                                                 @Part("vehicleInsuranceCompanyName") RequestBody companyName,
                                                                 @Part("vehicleInsuranceType") RequestBody type,
                                                                 @Part("vehicleInsuranceEffectiveFrom") RequestBody effectiveFromDate,
                                                                 @Part("vehicleInsuranceEffectiveTill") RequestBody effectiveTillDate,
                                                                 @Part("next_step") RequestBody nextStep,
                                                                 @Part MultipartBody.Part vehicleInsuranceCertificateImage);

        @POST("vehicle/edit")
        @Multipart
        Call<AddVehicleModelResponse> editRegistrationVehicleDetail(@Part("id") RequestBody vehicleId,
                                                                    @Part("user_id") RequestBody userId,
                                                                    @Part("registrationNumber") RequestBody registrationNumber,
                                                                    @Part("next_step") RequestBody nextStep,
                                                                    @Part MultipartBody.Part registerImage);

        @GET("allvehicle/{id}")
        Call<GetAllVehicleModelResponse> getAllVehicles(@Path("id") String userId);

        @POST("user/update")
        @Multipart
        Call<UpdateUserModelResponse> updateUserDriverLicence(@Part("id") RequestBody userId,
                                                              @Part("driverLicenceNumber") RequestBody licenceNumber,
                                                              @Part("driverLicenceFrom") RequestBody driverLicenceFromDate,
                                                              @Part("driverLicenceTill") RequestBody driverLicenceTillDate,
                                                              @Part("next_step") RequestBody nextStep,
                                                              @Part MultipartBody.Part licenceFrontPic,
                                                              @Part MultipartBody.Part licenceBackPic);

        @POST("user/update")
        @Multipart
        Call<UpdateUserModelResponse> updateUserProfile(@Part("id") RequestBody userId,
                                                        @Part("firstname") RequestBody firstName,
                                                        @Part("lastname") RequestBody lastName,
                                                        @Part MultipartBody.Part driverProfileImage);

        @GET("user/profile/{id}")
        Call<UserDetailModelResponse> getUserStatus(@Path("id") String userId);

        @FormUrlEncoded
        @POST("user/changepass")
        Call<UpdateUserModelResponse> changePassword(@Field("id") String userId,
                                                     @Field("password") String oldPassword,
                                                     @Field("new_password") String newPassword);

        @POST("paymentInfo")
        @FormUrlEncoded
        Call<PaymentInfoResponse> addCardDetail(@Field("user_id") String id,
                                                @Field("cardNumber") String cardNumber,
                                                @Field("month") String month,
                                                @Field("year") String year,
                                                @Field("cvc") String cvc,
                                                @Field("token") String token,
                                                @Field("default") Boolean value,
                                                @Field("paymentMethod") String paymentMethod,
                                                @Field("next_step") String nextStep);

        @POST("editPaymentInfo")
        @FormUrlEncoded
        Call<PaymentInfoResponse> editCardDetail(@Field("user_id") String id,
                                                 @Field("id") String paymentId,
                                                 @Field("cardNumber") String cardNumber,
                                                 @Field("month") String month,
                                                 @Field("year") String year,
                                                 @Field("cvc") String cvc,
                                                 @Field("token") String token,
                                                 @Field("default") Boolean value,
                                                 @Field("paymentMethod") String paymentMethod,
                                                 @Field("next_step") String nextStep);

        @GET("getPaymentById/{id}")
        Call<GetCardDetailModelResponse> getCardDetail(@Path("id") String userId);

        @FormUrlEncoded
        @POST("rating/ratecount")
        Call<DriverRatingResponse> getRating(@Field("rating_to") String driverId);

        @FormUrlEncoded
        @POST("rating/ratetrip")
        Call<RatingResponse> setRatingToUser(@Field("rating_to") String to, @Field("rating_from") String from, @Field("comment") String comment, @Field("rating") Float rating);

        @FormUrlEncoded
        @POST("generateInvoice")
        Call<GetInvoiceResponse> getInvoice(@Field("driver_id") String driverId, @Field("bookingId") String bookingId, @Field("user_id") String userId, @Field("totalTime") double totalTime, @Field("distance") double distance, @Field("vehicleTypeId") String vehicleTypeId);

        @FormUrlEncoded
        @POST("bookingHistory")
        Call<BookingHistoryModelResponse> getBookingHistory(@Field("user_id") String userId, @Field("role") String userRole, @Field("type") String type);

        @FormUrlEncoded
        @POST("getInvoice")
        Call<GetInvoiceResponse> getInvoiceDetail(@Field("bookingId") String bId);

        @FormUrlEncoded
        @POST("getEarnings")
        Call<EarningsModelResponse> getEarnings(@Field("from") String fromDate, @Field("till") String tillDate, @Field("user_id") String userId);

        @GET("cancelationReason/{role}")
        Call<CancelationReasonModelResponse> getReasons(@Path("role") String role);
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