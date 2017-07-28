package mindvalley.com.navigation;


import com.github.aurae.retrofit2.LoganSquareConverterFactory;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;

public class ApiClient {

    public static final String BASE_URL = "https://www.google.com";

    private static Retrofit retrofit = null;


    public static Retrofit getClient() {
        if (retrofit == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
// set your desired log level
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.readTimeout(60, TimeUnit.SECONDS);

            httpClient.connectTimeout(60, TimeUnit.SECONDS);
// add your other interceptors …
// add logging as last interceptor
            httpClient.addInterceptor(logging);  // <-- this is the important line!

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(httpClient.build())
                    .addConverterFactory(LoganSquareConverterFactory.create())
                    //    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

}