package ru.narod.nod.catalogue.ViewControllers;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import ru.narod.nod.catalogue.dataBase.DBHelperVisitedItems;
import ru.narod.nod.catalogue.dataBase.DataKeepContract;


public class LastVisitedItemsController extends AppCompatActivity {

    private final String TAG = "myLogs." + getClass().getSimpleName();
    private JSonParser jSonParser;
    private ArrayList<String> itemsUrls;
    private NestedScrollView last_visited_items_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.last_visited_items_view);
        Log.i(TAG, " onCreate() is started");

        //Setting title as "Last visited"
        setTitle("Last visited");

        //Showing the progress bar during the start search process
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar_last_visited);
        progressBar.setVisibility(View.VISIBLE);

        getLastVisitedFromDB();

        Log.i(TAG, " onCreate() is finished");
    }

    private void fill(Item[] itemArray) {

        int i = 0;
        int numberOfItemsInArray = itemArray.length;

        while (i < numberOfItemsInArray) {
            SimpleItemController simpleItemController = new SimpleItemController();

            FragmentTransaction fTrans = getFragmentManager().beginTransaction();
            fTrans.add(R.id.actSearchLinLayoutSearchResult, simpleItemController);
            fTrans.commit();

            i++;
        }
    }


    //region Loading of the favorites
    public void getLastVisitedFromDB() {

        //Initialisation of DBHelperFavoritePurchases to load and save favorites to a database
        DBHelperVisitedItems dbHelperLastVisitedItems = new DBHelperVisitedItems(this);

        Log.d(TAG, "--- Rows in favorites: ---");

        //Connecting to a db
        SQLiteDatabase db = dbHelperLastVisitedItems.getWritableDatabase();

        //Making request for all data from a table ang getting the Cursor
        Cursor c = db.query(DataKeepContract.TableFields.TABLE_NAME,
                null, null, null, null, null, null);

        //read the db to LOG
        dbHelperLastVisitedItems.readDB(db);

        //Putting a cursor position to the first row of the massive
        //If it is no rows then it's returning false
        if (c.moveToFirst()) {

            //Determine column numbers by name in massive
            //final int _ID = c.getColumnIndex(DataKeepContract.TableFields._ID);
            final int COLUMN_NUMBER = c.getColumnIndex(DataKeepContract.TableFields.COLUMN_NUMBER);

            itemsUrls = new ArrayList<>();

            do {
                //Getting values by column numbers
                //Getting and Converting image's URLs to load pictures

                final Item item = new Item();
                item.setId(c.getString(COLUMN_NUMBER));

                //Getting a description of an item
                Callback apiRequestListener_lastVisitedItemsController = new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

//                ApiRequestListener apiRequestListenerDescription = new ApiRequestListener() {
//                    @Override
//                    public void onRequestProcessed(int requestCode, ApiResponse payload) {
                        Log.i(TAG, " DetailItemController: apiRequestListenerDescription is started");

                        String resp = null;
                        try {
                            resp = response.body().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if (resp != null)
                            try {
                                JSONObject jo = new JSONObject(resp);
//                                item.setId(jo.getString("id"));
                                item.setTitle(jo.getString("title"));
                                item.setDescriptionsUrl(jo.getString("id"));
                                item.setPrice(jo.getDouble("price"));
                                item.setThumbnailUrl(jo.getString("thumbnail"));
                                item.setImageUrl(jo.getString("id"));

                                JSONArray tmpJA = null;
                                try {
                                    tmpJA = jo.getJSONArray("pictures");
                                    Log.i(TAG, " DetailItemController: parseImages() images are: " + tmpJA);
                                    if (tmpJA.length() > 0) {
                                        for (int i = 0; i < tmpJA.length(); i++) {
                                            String concreteImageUrl = tmpJA.getJSONObject(i).get("url").toString();
                                            item.setImagesUrl(concreteImageUrl);
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                Model.getInstance().putLastVisitedItem(item);

                                SimpleItemController simpleItemController = new SimpleItemController();

                                simpleItemController.setItem(item);
                                simpleItemController.getTmpArr()[0] = item.getTitle();
                                simpleItemController.getTmpArr()[1] = "$" + String.valueOf(item.getPrice());
                                simpleItemController.getTmpArr()[2] = item.getThumbnailUrl();

                                FragmentTransaction fTrans = getFragmentManager().beginTransaction();
                                fTrans.add(R.id.actLastVisitedItemsLinLayout, simpleItemController);
                                fTrans.commit();

                                //Model.getInstance().putLastVisitedItem(item);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        //Stop showing the progress bar when the start search process is stopped
                        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar_last_visited);
                        progressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.i(TAG, " the response is onFailure() method, error is: " + t.toString());
                        Toast.makeText(getApplicationContext(),
                                "A connection cannot be established. Try again later...", Toast.LENGTH_SHORT).show();
                    }
                };


                String url =
                        getResources().getString(R.string.items_main_string) + //a string from the EditText field with a criteria
                                item.getId() +
                                "#json"; //last string symbols
                RestfulManager restfulMeliManager = new RestfulManager();
                restfulMeliManager.startRestAPI(url, apiRequestListener_lastVisitedItemsController, item.getId(), null);


                //Going to the next row
                //If it's the last row then getting false and finish a cycle
            } while (c.moveToNext());
        } else
            Log.d(TAG, "0 rows");
        c.close();
        dbHelperLastVisitedItems.close();
    }
    //endregion

    //region Menu making
    //Preparing the menu (action bar)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_favorite, menu);
        return true;
    }

    //Treating of the menu (action bar) selection
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
                Intent intent = new Intent(LastVisitedItemsController.this, MainViewController.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    //endregion

}
