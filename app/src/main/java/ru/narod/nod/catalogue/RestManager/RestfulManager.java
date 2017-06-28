package ru.narod.nod.catalogue.RestManager;

import android.util.Log;

import com.mercadolibre.android.sdk.ApiRequestListener;
import com.mercadolibre.android.sdk.Meli;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import ru.narod.nod.catalogue.Model.Model;


public class RestfulManager {
    private final String TAG = "myLogs." + getClass().getSimpleName();

    public void loadDescriptionByRestAPI(String descriptionsUrl, ApiRequestListener apiRequestListener) {
        //get an array with descriptionsUrl
        Meli.asyncGet(descriptionsUrl, apiRequestListener);
    }

    public void loadImagesByRestAPI(String imagesUrl, ApiRequestListener apiRequestListener) {
        //get an array with images
        Meli.asyncGet(imagesUrl, apiRequestListener);
    }


    public void startRestAPI(String url, Callback apiRequestListener, String image_url, String description_url) {
        //Meli.asyncGet(url, apiRequestListener);

//        Gson gson = new GsonBuilder().create();
        String url_base = Model.getInstance().getBase_url();
//        String url_additional = Model.getInstance().getSearchMainString();
        String url_criteria = Model.getInstance().getEtSearchCriteria();

        //Change timeout to 15 secs
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(15, TimeUnit.SECONDS)
                .connectTimeout(15, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
//                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .baseUrl(url_base)
                .build();

//        MercadolibreClientMain mercadolibreClientInterface = retrofit.create(MercadolibreClientMain.class);

        Call<ResponseBody> call = null;

//        call = retrofit.create(MercadolibreClientMain.class)
//                .mercadoSearch(url_criteria.substring(1));

        if (image_url != null) {
            call = retrofit.create(MercadolibreClientItemsAndImages.class)
                    .mercadoSearch(image_url); //image_url
        } else if (description_url != null) {
            call = retrofit.create(MercadolibreClientDescription.class)
                    .mercadoSearch(description_url);
            Log.i(TAG, "");
        } else {
//            if (url.substring(0, 19).equals("sites/MLU/search?q=")) {
                call = retrofit.create(MercadolibreClientMain.class)
                        .mercadoSearch(url_criteria);
//            } else {
//                call = retrofit.create(MercadolibreClientMain.class)
//                        .mercadoSearch(url);
//            }
        }


        if (call != null) {
            call.enqueue(apiRequestListener);
        }
    }
}


interface MercadolibreClientMain {
//    @FormUrlEncoded
    @GET("/sites/MLU/search/") //sites/MLU/search?q
    Call<ResponseBody> mercadoSearch(@Query("q") String criteria);
}

interface MercadolibreClientItemsAndImages {
    @GET("/items/{id}/") //sites/MLU/search?q
    Call<ResponseBody> mercadoSearch(@Path("id") String id);
}

interface MercadolibreClientDescription {
    @GET("/items/{id}/descriptions/") //sites/MLU/search?q
    Call<ResponseBody> mercadoSearch(@Path("id") String id);
}