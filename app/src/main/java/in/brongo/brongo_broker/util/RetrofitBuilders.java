package in.brongo.brongo_broker.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by mohit on 07-03-2017.
 */

public class RetrofitBuilders {

    private static String SERVER_IP = "http://18.218.3.165:8080/QuickBroker/";
    private static String DEVELOPMENT_URL = "http://18.220.16.149:8080/Brongo/";
    private static String LOCAL_IP = "http://192.168.1.30:8082/brongo/";
    public static final String MAP_ROOT_URL = "https://maps.googleapis.com";
    private static String PRODUCTION_URL = "https://prod.brongo.in/Brongo/";
    private static RetrofitBuilders retrofitBuilders;

    public static String getBaseUrl() {
        return PRODUCTION_URL;
    }

    public static RetrofitBuilders getInstance() {
        if (retrofitBuilders != null) {
            return retrofitBuilders;
        } else {
            retrofitBuilders = new RetrofitBuilders();
            return retrofitBuilders;
        }
    }

    public RetrofitAPIs getAPIService(String baseUrl) {
        Gson gson = new GsonBuilder()
                .setLenient()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();
        OkHttpClient myOkhtppClient = getOkhttpResponse();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(myOkhtppClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        return retrofit.create(RetrofitAPIs.class);
    }

    private OkHttpClient getOkhttpResponse() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        final OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(4, TimeUnit.MINUTES)
                .readTimeout(4, TimeUnit.MINUTES)
                .writeTimeout(4, TimeUnit.MINUTES)
                .addInterceptor(httpLoggingInterceptor)
                .build();
        return okHttpClient;
    }
}
