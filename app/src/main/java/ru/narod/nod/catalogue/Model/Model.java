package ru.narod.nod.catalogue.Model;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.mercadolibre.android.sdk.ApiRequestListener;

import java.util.ArrayList;

import ru.narod.nod.catalogue.Item;

public class Model {

    private String key_url = "";
    private String base_url = "";
    private static final Model ourInstance = new Model();
    private ArrayList<Item> lastVisited;
    private String searchMainString;
    private ApiRequestListener apiRequestListener;
    private String etSearchCriteria = "";

    public static Model getInstance() {
        return ourInstance;
    }

    private Model() {
        lastVisited = new ArrayList();

    }

    //The key url setter/getter
    public String getKey_url() {
        return key_url;
    }
    public void setKey_url(String key_url) {
        this.key_url = key_url;
    }

    //The base url setter/getter
    public String getBase_url() {
        return base_url;
    }
    public void setBase_url(String base_url) {
        this.base_url = base_url;
    }

    //The lastVisited array getter/setter
    public ArrayList getLastVisited() {
        return lastVisited;
    }
    public ArrayList getLastVisitedItem(int numberOfItem) {
        return lastVisited;
    }
    public void setLastVisited(ArrayList lastVisited) {
        this.lastVisited = lastVisited;
    }
    public void putLastVisitedItem(Item item) {
        this.lastVisited.add(item);
    }

    //The searchMainString string getter/setter
    public String getSearchMainString() {
        return searchMainString;
    }
    public void setSearchMainString(String searchMainString) {
        this.searchMainString = searchMainString;
    }

    //The etSearchCriteria editText getter/setter
    public String getEtSearchCriteria() {
        return etSearchCriteria;
    }
    public void setEtSearchCriteria(String etSearchCriteria) {
        this.etSearchCriteria = etSearchCriteria;
    }

    //The apiRequestListener callback getter/setter
    public ApiRequestListener getApiRequestListener() {
        return apiRequestListener;
    }
    public void setApiRequestListener(ApiRequestListener apiRequestListener) {
        this.apiRequestListener = apiRequestListener;
    }

    //The method of checking the Internet connection
    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        boolean isInternet = netInfo != null && netInfo.isConnectedOrConnecting();

        if (!isInternet)
            Toast.makeText(context, "The Internet connection is lost", Toast.LENGTH_SHORT).show();

        return isInternet;
    }
}
