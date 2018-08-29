package com.github.guilhermesgb.buttons.model.dagger;


import com.github.guilhermesgb.buttons.model.FetchButtonsUseCase;
import com.github.guilhermesgb.buttons.model.database.DatabaseResource;
import com.github.guilhermesgb.buttons.model.network.ApiEndpoints;

import dagger.Module;
import dagger.Provides;

@Module
public class UseCasesModule {

    @Provides
    FetchButtonsUseCase provideFetchButtonsUseCase(ApiEndpoints apiEndpoints,
                                                   DatabaseResource databaseResource) {
        return new FetchButtonsUseCase(apiEndpoints, databaseResource);
    }

}
