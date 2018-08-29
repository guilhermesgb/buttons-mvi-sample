package com.github.guilhermesgb.buttons.model.dagger;


import android.content.Context;

import com.github.guilhermesgb.buttons.model.database.DatabaseResource;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DatabaseModule {

    private final Context applicationContext;

    public DatabaseModule(Context applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Provides @Singleton
    DatabaseResource provideDatabaseResource(@ForDatabase Context context) {
        return DatabaseResource.createInstance(context);
    }

    @Provides @Singleton @ForDatabase
    Context provideApplicationContext() {
        return applicationContext;
    }

}
