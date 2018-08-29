package com.github.guilhermesgb.buttons.model.dagger;


import com.github.guilhermesgb.buttons.model.FetchButtonsUseCase;
import com.github.guilhermesgb.buttons.presenter.ButtonsPresenter;

import dagger.Module;
import dagger.Provides;

@Module
public class PresentersModule {

    @Provides
    ButtonsPresenter provideButtonsPresenter(FetchButtonsUseCase fetchButtonsUseCase) {
        return new ButtonsPresenter(fetchButtonsUseCase);
    }

}
