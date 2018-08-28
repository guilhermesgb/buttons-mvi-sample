package com.github.guilhermesgb.buttons.model.utils;

import android.content.Context;

import com.github.guilhermesgb.buttons.model.database.DatabaseResource;
import com.github.guilhermesgb.buttons.model.network.ApiEndpoints;
import com.github.guilhermesgb.buttons.model.network.ApiResource;

public abstract class UseCase {

    private final String apiBaseUrl; //for networking purposes (endpoint calls)
    private final Context context;  //for persistence purposes (database operations)

    public UseCase(String apiBaseUrl, Context context) {
        this.apiBaseUrl = apiBaseUrl;
        this.context = context;
    }

    protected ApiEndpoints getApi() {
        return ApiResource.getInstance(apiBaseUrl);
    }

    public DatabaseResource getDatabase() {
        return DatabaseResource.getInstance(context);
    }

}
