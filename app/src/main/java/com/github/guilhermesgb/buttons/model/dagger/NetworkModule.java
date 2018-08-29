package com.github.guilhermesgb.buttons.model.dagger;


import android.support.annotation.Nullable;

import com.github.guilhermesgb.buttons.model.Button;
import com.github.guilhermesgb.buttons.model.network.ApiEndpoints;
import com.github.guilhermesgb.buttons.model.network.ApiResource;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.reactivex.Single;

import static com.github.guilhermesgb.buttons.model.Button.ButtonType.TO_BOTTOM;
import static com.github.guilhermesgb.buttons.model.Button.ButtonType.TO_LEFT;
import static com.github.guilhermesgb.buttons.model.Button.ButtonType.TO_RIGHT;
import static com.github.guilhermesgb.buttons.model.utils.StringUtils.isEmpty;

@Module
public class NetworkModule {

    private final String apiBaseUrl;

    public NetworkModule(String apiBaseUrl) {
        this.apiBaseUrl = apiBaseUrl;
    }

    @Provides @Singleton
    ApiEndpoints provideApiEndpoints(@Nullable @ForNetwork String apiBaseUrl) {
        //When apiBaseUrl is null, it means we should use BuildConfig.API_BASE_URL instead.
        if (isEmpty(apiBaseUrl)) {
            return () -> {
                List<Button> buttonsComingFromTheRemoteServer = new LinkedList<>();
                buttonsComingFromTheRemoteServer.add(new Button("0", "Apple", TO_BOTTOM));
                buttonsComingFromTheRemoteServer.add(new Button("1", "yahoo", TO_LEFT));
                buttonsComingFromTheRemoteServer.add(new Button("2", "Google", TO_RIGHT));
                buttonsComingFromTheRemoteServer.add(new Button("3", "Apple", TO_BOTTOM));
                buttonsComingFromTheRemoteServer.add(new Button("4", "yahoo", TO_LEFT));
                buttonsComingFromTheRemoteServer.add(new Button("5", "Google", TO_RIGHT));
                buttonsComingFromTheRemoteServer.add(new Button("6", "Apple", TO_BOTTOM));
                buttonsComingFromTheRemoteServer.add(new Button("7", "yahoo", TO_LEFT));
                buttonsComingFromTheRemoteServer.add(new Button("8", "Google", TO_RIGHT));
                buttonsComingFromTheRemoteServer.add(new Button("9", "Apple", TO_BOTTOM));
                buttonsComingFromTheRemoteServer.add(new Button("10", "yahoo", TO_LEFT));
                buttonsComingFromTheRemoteServer.add(new Button("11", "Google", TO_RIGHT));
                return Single.just(buttonsComingFromTheRemoteServer);
            };
        }
        //FIXME remove the conditional above when a real API is available.
        return ApiResource.createInstance(apiBaseUrl);
    }

    @Provides @Singleton @ForNetwork @Nullable
    String provideApiBaseUrl() {
        return apiBaseUrl;
    }

}
