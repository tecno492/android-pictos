package com.example.pictopocketiv.arasaac;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ArasaacApiService {

    @GET("/api/pictograms/{locale}/bestsearch/{searchText}")
    Call<List<ArasaacModel.Pictogram>> search(@Path("locale") String locale, @Path("searchText") String search);

    @GET("/api/pictograms/{locale}/{idPictogram}")
    Call<ArasaacModel.Pictogram> getPicto(@Path("locale") String locale, @Path("idPictogram") int idPictogram);
}