package com.github.guilhermesgb.buttons.model.network;

import com.github.guilhermesgb.buttons.BuildConfig;
import com.github.guilhermesgb.buttons.model.Button;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ApiResource {

    //For better code readability semantically-wise, users of this ApiResource can pass around
    // the constant below as the apiBaseUrl while creating a new ApiEndpoints instance in order
    // to indicate desire of pointing to real API configured on the build.gradle file (the variable
    // API_BASE_URL) instead of overriding with another API url (usually for testing purposes).
    public static final String WILL_USE_REAL_API = null;

    private static final OkHttpClient.Builder client = new OkHttpClient.Builder();
    private static final HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
    static {
        if (BuildConfig.DEBUG) {
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            client.addInterceptor(logging);
        }
    }

    /**
     * Creates the actual instance of the API endpoints access point object,
     * configuring it to talk to a live or mocked server located at API_BASE_URL.
     * This is where all JSON deserializers (for each entity type) shall be located.
     * @param apiBaseUrl the URL of the live (test or production) or mocked server.
     * @return an object providing access to all the desired server's endpoints
     */
    public static ApiEndpoints createInstance(final String apiBaseUrl) {
        GsonBuilder registeredTypeAdapters = new GsonBuilder();
        registeredTypeAdapters.registerTypeAdapter(Button.class,
            (JsonDeserializer<Button>) (json, typeOfT, context)
                -> Button.dejsonizeFrom(json.getAsJsonObject()));

        return new Retrofit.Builder().baseUrl(apiBaseUrl)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(registeredTypeAdapters.create()))
            .client(client.build()).build().create(ApiEndpoints.class);
    }

}
