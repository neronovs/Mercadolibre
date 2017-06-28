package ru.narod.nod.catalogue.ViewControllers;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.mercadolibre.android.sdk.ApiRequestListener;
import com.mercadolibre.android.sdk.ApiResponse;
import com.mercadolibre.android.sdk.Meli;

import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.Tracking;
import net.hockeyapp.android.UpdateManager;
import net.hockeyapp.android.metrics.MetricsManager;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.narod.nod.catalogue.Model.Model;
import ru.narod.nod.catalogue.R;
import ru.narod.nod.catalogue.RestManager.RestfulManager;

public class MainViewController extends AppCompatActivity implements View.OnClickListener {

    private String url = "";
    private final String TAG = "myLogs." + getClass().getSimpleName();
    private Model model;
    private EditText etSearchCriteria;
    private Button btnSearch, btnLastVisited, btnClearEtSearchCriteria;
    private Menu menu_mainActivity;

    Callback apiRequestListener = new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
            Log.i(TAG, " apiRequestListener: onRequestProcessed() is called");
            Log.i(TAG, " apiRequestListener: onRequestProcessed(), the response is: " + response);

            String resp = null;
            try {
                resp = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }

            btnSearch.setClickable(true);
            btnLastVisited.setClickable(true);

            //Stop showing the progress bar when the start search process is stopped
            stopProgressBar();

            if (resp == null) {
                Toast.makeText(getApplicationContext(),
                        "A connection cannot be established. Try again later...", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(MainViewController.this, SearchResultController.class);
                intent.putExtra("jsonString", resp);

                startActivity(intent);
            }

            Log.i(TAG, " the response is json: " + resp);
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            Log.i(TAG, " the response is onFailure() method, error is: " + t.toString());
            //Stop showing the progress bar when the start search process is stopped
            stopProgressBar();
            btnSearch.setClickable(true);
            btnLastVisited.setClickable(true);

            Toast.makeText(getApplicationContext(),
                    "A connection cannot be established. Try again later...", Toast.LENGTH_SHORT).show();
        }
    };

    public MainViewController() {}

    private void stopProgressBar() {
        //Stop showing the progress bar when the start search process is stopped
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);
    }

    public ApiRequestListener apiRequestListener_old = new ApiRequestListener() {
        @Override
        public void onRequestProcessed(int requestCode, ApiResponse payload) {
            Log.i(TAG, " apiRequestListener: onRequestProcessed() is called");
            Log.i(TAG, " apiRequestListener: onRequestProcessed(), the response is: " + payload.getContent());

            btnSearch.setClickable(true);
            btnLastVisited.setClickable(true);

            //Stop showing the progress bar when the start search process is stopped
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);
            progressBar.setVisibility(View.INVISIBLE);

            if (payload.getContent() == null) {
                Toast.makeText(getApplicationContext(),
                        "A connection cannot be established. Try again later...", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(MainViewController.this, SearchResultController.class);
                intent.putExtra("jsonString", payload.getContent());

                startActivity(intent);
            }
        }

        @Override
        public void onRequestStarted(int requestCode) {
            Log.i(TAG, " apiRequestListener: onRequestStarted() is called");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_view);

//        Meli.initializeSDK(getApplicationContext());

        Model.getInstance().setBase_url(getResources().getString(R.string.meli_redirect_uri));

        etSearchCriteria = (EditText) findViewById(R.id.etSearchCriteria);
        if (!Model.getInstance().getEtSearchCriteria().equals("")) {
            etSearchCriteria.setText(Model.getInstance().getEtSearchCriteria());
        }

        btnSearch = (Button) findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(this);
        btnLastVisited = (Button) findViewById(R.id.btnLastVisited);
        btnLastVisited.setOnClickListener(this);
        btnClearEtSearchCriteria = (Button) findViewById(R.id.btnClearEtSearchCriteria);
        btnClearEtSearchCriteria.setOnClickListener(this);
//        jSonParser = new JSonParser();

        model = Model.getInstance();
        model.setSearchMainString(getResources().getString(R.string.search_main_string));
        //model.setApiRequestListener(apiRequestListener);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getActionBar().setIcon(getDrawable(R.mipmap.house));
//        }

//        Meli.startLogin(this, 0);
//        Meli.getCurrentIdentity(getApplicationContext());

        //region HockeyApp inits
        checkForCrashes();
        checkForUpdates();
        MetricsManager.register(this, getApplication());
        //endregion
    }

    //region HockeyApp methods and related ones are here
    private void checkForCrashes() {
        CrashManager.register(this);
    }

    private void checkForUpdates() {
        // Remove this for store builds!
        UpdateManager.register(this);
    }

    private void unregisterManagers() {
        UpdateManager.unregister();
    }
    @Override
    public void onPause() {
        Tracking.stopUsage(this);
        super.onPause();
        unregisterManagers();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterManagers();
    }
    @Override
    protected void onResume() {
        super.onResume();
        Tracking.startUsage(this);
    }
    //endregion

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, " onActivityResult: the result is received!");
    }


    public void startParser() {

        if (etSearchCriteria.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "Enter item you want to find", Toast.LENGTH_SHORT)
                    .show();
        } else {
            Model.getInstance().setKey_url(getResources().getString(R.string.search_main_string));
            //Hiding a keyboard
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);

            //Showing the progress bar during the start search process
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);
            progressBar.setVisibility(View.VISIBLE);

            btnSearch.setClickable(false);
            btnLastVisited.setClickable(false);
            if (Model.isOnline(getApplicationContext())) {
                Log.i(TAG, " startParser() is started!");
                Model.getInstance().setEtSearchCriteria(etSearchCriteria.getText().toString());
                url =
                        //"https://api.mercadolibre.com" +
                        model.getSearchMainString() + //addition for search url
                                etSearchCriteria.getText() + //a string from the EditText field with a criteria
                                "#json"; //last string symbols
                RestfulManager restfulMeliManager = new RestfulManager();
                restfulMeliManager.startRestAPI(url, apiRequestListener, null, null);
//                String json = Meli.get("https://api.mercadolibre.com/sites/MLU/search?q=Mercedes#json").toString();
//                Log.i(TAG, " Meli get() is: " + json);

            } else {
                Log.i(TAG, " startParser(), the Internet is lost!");
            }
        }
    }

    public void startHistory() {

        btnLastVisited.setClickable(false);
        btnSearch.setClickable(false);

        if (Model.isOnline(getApplicationContext())) {
            Log.i(TAG, " MainViewController: startHistory() was started");
            Intent intent = new Intent(MainViewController.this, LastVisitedItemsController.class);
            startActivity(intent);
        } else {
            Log.i(TAG, " startHistory(), the Internet is lost!");
        }

        btnLastVisited.setClickable(true);
        btnSearch.setClickable(true);
    }

    /*//The method of checking the Internet connection
    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        boolean isInternet = netInfo != null && netInfo.isConnectedOrConnecting();

        if (!isInternet)
            Toast.makeText(context, "The Internet connection is lost", Toast.LENGTH_SHORT).show();

        return isInternet;
    }*/

    //region Menu creating *************
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.d(TAG, "onPrepareOptionsMenu() was started in MainActivity");

        this.menu_mainActivity = menu;

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu() was started in MainActivity");
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    //Treating of the menu (action bar) selection
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected() was started in MainActivity");
        switch (item.getItemId()) {
            case R.id.menu_filter:
                startParser();
                break;
            case R.id.menu_favorites:
                startHistory();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    //endregion


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSearch:
                startParser();
                break;
            case R.id.btnLastVisited:
                startHistory();
                break;
            case R.id.btnClearEtSearchCriteria:
                etSearchCriteria.setText("");
                break;
        }
    }
}
