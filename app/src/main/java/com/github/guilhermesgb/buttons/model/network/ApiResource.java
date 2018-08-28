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

import static com.github.guilhermesgb.buttons.model.utils.StringUtils.isEmpty;

public class ApiResource {

    //For better code readability semantically-wise.
    public static final String WILL_USE_REAL_API = null;

    private static final OkHttpClient.Builder client = new OkHttpClient.Builder();
    private static final HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
    static {
        if (BuildConfig.DEBUG) {
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            client.addInterceptor(logging);
        }
    }

    private static ApiEndpoints apiService;
    private static String apiBaseUrl;

    private ApiResource() {}

    /**
     * Returns the API endpoints access point object. The API_BASE_URL will depend
     * upon whether this is a debug or release version of the application, pointing
     * either to production or to the test version of the live API server.
     * @return an object providing access to all the live server's endpoints
     */
    private static ApiEndpoints getInstance() {
        if (apiService == null || isEmpty(apiBaseUrl)
                || !BuildConfig.API_BASE_URL.equals(apiBaseUrl)) {
            apiBaseUrl = BuildConfig.API_BASE_URL;
            apiService = createInstance(apiBaseUrl);
        }
        return apiService;
    }

    /**
     * Returns the API endpoints access point object. This version of the method
     * exists for the sole purpose of allowing to override default API_BASE_URL
     * when instantiating the API endpoints access point, used for testing purposes
     * with aid from a mocked server.
     * @param overrideBaseUrl the URL of the mocked server -- beware that if null or
     *                        empty string is passed, the live server access point
     *                        will be created instead.
     * @return an object providing access to all the mocked server's endpoints
     */
    public static ApiEndpoints getInstance(final String overrideBaseUrl) {
        if (isEmpty(overrideBaseUrl)) {
            return getInstance();
        }
        if (apiService == null || isEmpty(apiBaseUrl)
                || !overrideBaseUrl.equals(apiBaseUrl)) {
            apiBaseUrl = overrideBaseUrl;
            apiService = createInstance(apiBaseUrl);
        }
        return apiService;
    }

    /**
     * Creates the actual instance of the API endpoints access point object,
     * configuring it to talk to a live or mocked server located at API_BASE_URL.
     * This is where all JSON deserializers (for each entity type) shall be located.
     * @param apiBaseUrl the URL of the live (test or production) or mocked server.
     * @return an object providing access to all the desired server's endpoints
     */
    private static ApiEndpoints createInstance(final String apiBaseUrl) {
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
