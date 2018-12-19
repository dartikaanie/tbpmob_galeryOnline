package com.example.dara.galery;

import com.example.dara.galery.Model.FotoList;

import java.util.Date;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface TmdbClient {
    //mengambil data yang sdg tayang
    @GET("api/all")
    Call<List<Foto>> getAllGaleries();

    @FormUrlEncoded
    @POST("api/daftar")
    Call<ResponseBody> newFoto(
            @Field("nama") String nama,
            @Field("deskripsi") String deskripsi,
            @Field("lat") Double lat,
            @Field("lng") Double lng,
            @Field("foto") String foto
    );



}
