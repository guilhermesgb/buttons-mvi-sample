package com.github.guilhermesgb.buttons.model.network;

import com.github.guilhermesgb.buttons.model.Button;

import java.util.List;

import io.reactivex.Single;
import retrofit2.http.GET;

public interface ApiEndpoints {

    @GET("buttons")
    Single<List<Button>> getButtons();

}
