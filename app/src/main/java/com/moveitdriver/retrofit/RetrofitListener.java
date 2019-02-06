package com.moveitdriver.retrofit;

import retrofit2.Call;
import retrofit2.Response;

public interface RetrofitListener {
    void onSuccess(Call call, Response response, String method);
    void onFailure(String errorMessage);
}
