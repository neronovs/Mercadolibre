package ru.narod.nod.catalogue.ViewControllers;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.mercadolibre.android.sdk.ApiRequestListener;
import com.mercadolibre.android.sdk.ApiResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.narod.nod.catalogue.Item;
import ru.narod.nod.catalogue.RestManager.JSonParser;
import ru.narod.nod.catalogue.Model.Model;
import ru.narod.nod.catalogue.R;
import ru.narod.nod.catalogue.RestManager.RestfulManager;

public class SearchResultController extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = "myLogs." + getClass().getSimpleName();
    private JSonParser jSonParser;
    private ArrayList<Item> items;
    private int offset;
    private boolean updateList = true;
    private Menu menu_searchresultActivity;

    //A callback for addition filling of the result activity with another 50 items
    Callback apiRequestListener_searchResultController = new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
            Log.i(TAG, " onCreate(): ApiRequestListener: gotten an answer from +50_items");

            String resp = null;
            try {
                resp = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Item[] itemArrayRequest = jSonParser.getArrayOfItems(resp);
            items.clear();
            Collections.addAll(items, itemArrayRequest);

            fill(itemArrayRequest);
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            Log.i(TAG, " the response is onFailure() method, error is: " + t.toString());
            Toast.makeText(getApplicationContext(),
                    "A connection cannot be established. Try again later...", Toast.LENGTH_SHORT).show();
        }
    };

    ApiRequestListener arl_old = new ApiRequestListener() {
        @Override
        public void onRequestProcessed(int requestCode, ApiResponse payload) {
            Log.i(TAG, " onCreate(): ApiRequestListener: gotten an answer from +50_items");
            Item[] itemArrayRequest = jSonParser.getArrayOfItems(payload.getContent());
            items.clear();
            Collections.addAll(items, itemArrayRequest);

            fill(itemArrayRequest);
        }

        @Override
        public void onRequestStarted(int requestCode) {

        }
    };

    NestedScrollView.OnScrollChangeListener onScrollChangeListener = new NestedScrollView.OnScrollChangeListener() {
        @Override
        public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//            Log.i(TAG, " OnScrollChangeListener() is started");
            double percentage = (double) scrollY / (double) v.computeVerticalScrollRange();
            if (percentage > 0.8 && updateList) {
                updateList = false;
                Log.i(TAG, " OnScrollChangeListener() percentage of viewed items is " + percentage);

                Log.i(TAG, " OnScrollChangeListener() offset is " + offset);
                String urlPlus = Model.getInstance().getSearchMainString() + //addition for search url
                        Model.getInstance().getEtSearchCriteria() + //a string from the EditText field with a criteria
                        "&offset=" + offset +
                        "#json"; //last string symbols
                RestfulManager rfm = new RestfulManager();

                rfm.startRestAPI(urlPlus, apiRequestListener_searchResultController, null, null);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_result_view);
        Log.i(TAG, " onCreate() is started");

        //Setting title of the view
        setTitle("Search result");

        Intent intent = getIntent();
        String jsonString = intent.getStringExtra("jsonString");
        jSonParser = new JSonParser();
        Item[] itemArray = jSonParser.getArrayOfItems(jsonString);
        items = new ArrayList<>();
        Collections.addAll(items, itemArray);

        offset = 0;

        NestedScrollView search_result_scroll_view = (NestedScrollView) findViewById(R.id.search_result_scroll_view);
        search_result_scroll_view.setOnScrollChangeListener(onScrollChangeListener);

        fill(itemArray);

        Log.i(TAG, " onCreate() is finished");
    }

    //Filling the result activity with simple views
    private void fill(Item[] itemArray) {

        if (itemArray.length > 0) {
            //Creating and putting simple views in the search result activity
            int numberOfItemsInArray = itemArray.length;
            int i = 0;
            while (i < numberOfItemsInArray) {
                SimpleItemController simpleItemController = new SimpleItemController();

                simpleItemController.setItem(items.get(i));
                simpleItemController.getTmpArr()[0] = items.get(i).getTitle();
                simpleItemController.getTmpArr()[1] = "$" + String.valueOf(items.get(i).getPrice());
                simpleItemController.getTmpArr()[2] = items.get(i).getThumbnailUrl();

                FragmentTransaction fTrans = getFragmentManager().beginTransaction();
                fTrans.add(R.id.actSearchLinLayoutSearchResult, simpleItemController);
                fTrans.commitAllowingStateLoss();

                i++;
            }
            Log.i(TAG, " fill(): updateList taking \"true\"");
            updateList = true;
            Log.i(TAG, " fill(): getting another 50 items to the search_result_view");
            offset += 50;
        } else {
            //Making a banner "no results"
            SimpleItemController simpleItemController = new SimpleItemController();

            simpleItemController.setItem(new Item());
            simpleItemController.getTmpArr()[0] = "";
            simpleItemController.getTmpArr()[1] = "";
            simpleItemController.getTmpArr()[2] = "no result";
            simpleItemController.setTv_no_result_visibility(true);

            FragmentTransaction fTrans = getFragmentManager().beginTransaction();
            fTrans.add(R.id.actSearchLinLayoutSearchResult, simpleItemController);
            fTrans.commit();

        }
    }

    @Override
    public void onClick(View v) {
        //Checking if the Internet is available
        if (Model.isOnline(this)) {
        }
    }

    //region Menu making
    //Preparing the menu (action bar)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu() was started in SearchresultController");
        getMenuInflater().inflate(R.menu.menu_result, menu);
        menu_searchresultActivity = menu;

        return true;
    }

    //Treating of the menu (action bar) selection
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected() was started in SearchresultController");
        switch (item.getItemId()) {
            //Going back to the filter
            case R.id.menu_back:
                onBackPressed();
                break;
            //Going to the favorite
            case R.id.menu_favorites:
                startActivity(new Intent(this, LastVisitedItemsController.class));
                break;
            //Going to the main activity (home)
            case R.id.menu_home:
                Intent intent = new Intent(SearchResultController.this, MainViewController.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    //endregion

}
