package info.chitanka.android.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import info.chitanka.android.Constants;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by nmp on 16-3-15.
 */
public class ChitankaApiService {
    private ChitankaApiService() {
    }

    public static ChitankaApi createChitankaApiService() {
        Gson gson = new GsonBuilder().setLenient().create();
        Retrofit.Builder builder = new Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(Constants.CHITANKA_INFO_API);

        OkHttpClient client = new OkHttpClient.Builder().build();
        builder.client(client);

        return builder.build().create(ChitankaApi.class);
    }
}
