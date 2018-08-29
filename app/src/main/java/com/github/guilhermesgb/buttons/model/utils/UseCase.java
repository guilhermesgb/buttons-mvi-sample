package com.github.guilhermesgb.buttons.model.utils;

import com.github.guilhermesgb.buttons.model.database.DatabaseResource;
import com.github.guilhermesgb.buttons.model.network.ApiEndpoints;

public abstract class UseCase {

    private final ApiEndpoints apiEndpoints;
    private final DatabaseResource databaseResource;

    public UseCase(ApiEndpoints apiEndpoints, DatabaseResource databaseResource) {
        this.apiEndpoints = apiEndpoints;
        this.databaseResource = databaseResource;
    }

    protected ApiEndpoints getApi() {
        return apiEndpoints;
    }

    public DatabaseResource getDatabase() {
        return databaseResource;
    }

}
