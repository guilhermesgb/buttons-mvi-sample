package com.github.guilhermesgb.buttons.model.dagger;

import android.content.Context;
import android.support.annotation.Nullable;

import com.github.guilhermesgb.buttons.model.FetchButtonsUseCase;
import com.github.guilhermesgb.buttons.model.database.DatabaseResource;
import com.github.guilhermesgb.buttons.model.network.ApiEndpoints;
import com.github.guilhermesgb.buttons.presenter.ButtonsPresenter;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {
    PresentersModule.class,
    UseCasesModule.class,
    NetworkModule.class,
    DatabaseModule.class
})
public interface Dependencies {

    ButtonsPresenter buttonsPresenter();

    FetchButtonsUseCase fetchButtonsUseCase();

    ApiEndpoints apiEndpoints();

    DatabaseResource databaseResource();

    @Nullable @ForNetwork String apiBaseUrl();

    @ForDatabase Context applicationContext();

}
