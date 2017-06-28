package ru.narod.nod.catalogue;

import android.content.Context;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import static org.mockito.Mockito.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mercadolibre.android.sdk.ApiRequestListener;
import com.mercadolibre.android.sdk.ApiResponse;
import com.mercadolibre.android.sdk.Meli;

import net.hockeyapp.android.metrics.MetricsManager;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.narod.nod.catalogue.Model.Model;
import ru.narod.nod.catalogue.ViewControllers.MainViewController;
import ru.narod.nod.catalogue.ViewControllers.SearchResultController;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class MainViewControllerTest extends ActivityInstrumentationTestCase2<MainViewController> {

    @Mock
    Context mMockContext;

    private MainViewController mainViewController;
    private Model model;
    private EditText etSearchCriteria;
    private Button btnSearch, btnLastVisited, btnClearEtSearchCriteria;
    private Menu menu_mainActivity;
    Callback apiRequestListener = new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
            String resp = null;
            try {
                resp = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }

            btnSearch.setClickable(true);
            btnLastVisited.setClickable(true);
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            try {
                testStopProgressBar();
            } catch (Exception e) {
                e.printStackTrace();
            }
            btnSearch.setClickable(true);
            btnLastVisited.setClickable(true);
        }
    };

    public MainViewControllerTest() {
        super(MainViewController.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        mainViewController = getActivity();
        model = Model.getInstance();
        etSearchCriteria = (EditText) mainViewController.findViewById(R.id.etSearchCriteria);
        btnSearch = (Button) mainViewController.findViewById(R.id.btnSearch);
        btnLastVisited = (Button) mainViewController.findViewById(R.id.btnLastVisited);
        btnClearEtSearchCriteria = (Button) mainViewController.findViewById(R.id.btnClearEtSearchCriteria);

        try {
            runTestOnUiThread(new Runnable() {
                @Override
                public void run() {
                    etSearchCriteria.setText("");
                }
            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    @Test
    public void testOnCreate() throws Exception {

        //assertEquals("etSearchCriteria isn't empty", "", etSearchCriteria.getText().toString());
//        assertNotNull(etSearchCriteria);
//        Model.getInstance().setBase_url(getResources().getString(R.string.meli_redirect_uri));

//        etSearchCriteria = (EditText) findViewById(R.id.etSearchCriteria);
//        if (!Model.getInstance().getEtSearchCriteria().equals("")) {
//            etSearchCriteria.setText(Model.getInstance().getEtSearchCriteria());
//        }
//
//        btnSearch = (Button) findViewById(R.id.btnSearch);
//        btnSearch.setOnClickListener(this);
//        btnLastVisited = (Button) findViewById(R.id.btnLastVisited);
//        btnLastVisited.setOnClickListener(this);
//        btnClearEtSearchCriteria = (Button) findViewById(R.id.btnClearEtSearchCriteria);
//        btnClearEtSearchCriteria.setOnClickListener(this);
////        jSonParser = new JSonParser();
//
//        model = Model.getInstance();
//        model.setSearchMainString(getResources().getString(R.string.search_main_string));
//        //model.setApiRequestListener(apiRequestListener);
//
////        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
////            getActionBar().setIcon(getDrawable(R.mipmap.house));
////        }
//
////        Meli.startLogin(this, 0);
//        Meli.getCurrentIdentity(getApplicationContext());
//
//        //region HockeyApp inits
//        checkForCrashes();
//        checkForUpdates();
//        MetricsManager.register(this, getApplication());
//        //endregion
//
//


    }

    public void testStopProgressBar() throws Exception {

    }

    @Test
    public void testOnActivityResult() throws Exception {

    }

    @Test
    public void testStartParser() throws Exception {

    }

    @Test
    public void testStartHistory() throws Exception {

    }

    @Test
    public void testOnPrepareOptionsMenu() throws Exception {

    }

    @Test
    public void testOnCreateOptionsMenu() throws Exception {

    }

    @Test
    public void testOnOptionsItemSelected() throws Exception {

    }

    @Test
    public void testOnClick() throws Exception {

    }


    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }
}