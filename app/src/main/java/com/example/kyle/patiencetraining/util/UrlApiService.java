package com.example.kyle.patiencetraining.util;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface UrlApiService {
    String BASE_URL = "http://link-thumbnailer-api.herokuapp.com/thumbnails/";

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    @GET("new")
    Call<UrlInfo> getUrlInfo(@Query("url") String url);
}