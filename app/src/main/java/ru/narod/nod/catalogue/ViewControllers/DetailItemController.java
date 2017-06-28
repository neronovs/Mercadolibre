package ru.narod.nod.catalogue.ViewControllers;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mercadolibre.android.sdk.ApiRequestListener;
import com.mercadolibre.android.sdk.ApiResponse;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.narod.nod.catalogue.Item;
import ru.narod.nod.catalogue.R;
import ru.narod.nod.catalogue.RestManager.JSonParser;
import ru.narod.nod.catalogue.RestManager.RestfulManager;
import ru.narod.nod.catalogue.SwipeDetector;
import ru.narod.nod.catalogue.dataBase.DBHelperVisitedItems;
import ru.narod.nod.catalogue.dataBase.DataKeepContract;

/**
 * Product number, title, description, price and image
 */

public class DetailItemController extends AppCompatActivity {

    private final static int MAXIMUM_LAST_VISITED_NUMBER = 5;
    private ImageView emptyImage;
    private Item item;
    private TextView detail_tvDescription;
    private LinearLayout detail_ll_images;
    private final String TAG = "myLogs." + getClass().getSimpleName();
    private ArrayList<ImageView> images;
    private int currentPicture = 0;

    private String parsedNumber = "";
    //    private ArrayList<Bitmap> parsedImages = new ArrayList<>();
    private Menu menu_detailActivity;

    //Gestures
    private GestureDetector gestureDetector;
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

    //region Callbacks
    //Getting a description of an item
    Callback apiRequestListenerDescription = new Callback<ResponseBody>() {
        @Override
        public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
            try {
                Log.i(TAG, " DetailItemController: apiRequestListenerImages is started");

                String resp = null;
                try {
                    if (response.body() != null)
                        resp = response.body().string();
                    else
                        resp = "";
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Log.i(TAG, " DetailItemController: apiRequestListenerDescription is started");
                if (resp != null) {
                    JSonParser jSonParser = new JSonParser();
                    String parsedDescription = jSonParser.parseDescription(resp, getApplicationContext());
                    detail_tvDescription.setText(parsedDescription);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
            try {
                Log.i(TAG, " the apiRequestListenerDescription response is onFailure() method, error is: " + t.toString());
//            Toast.makeText(getApplicationContext(),
//                    "A connection cannot be established. Try again later...", Toast.LENGTH_SHORT).show();
                detail_tvDescription.setText(R.string.no_description);
//            onBackPressed();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    //Getting images of an item
    Callback apiRequestListenerImages = new Callback<ResponseBody>() {
        @Override
        public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
            try {
                Log.i(TAG, " DetailItemController: apiRequestListenerImages is started");

                String resp = null;
                try {
                    if (response.body() != null)
                        resp = response.body().string();
                    else
                        resp = "";
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (resp != null) {
                    findViewById(R.id.detail_tempImageNoPhoto).setBackground(null);
                    detail_ll_images.removeAllViews(); //delete the loading spinner pic from layout

                    int widthOfScreen = detail_ll_images.getMeasuredWidth();
                    JSonParser jSonParser = new JSonParser();
                    images = jSonParser.parseImages(resp, item, getApplicationContext(), widthOfScreen);
                    //setting the first image if there is one or more pictures in the array
                    if (detail_ll_images.getChildCount() == 0 && images.size() > 0) {
                        detail_ll_images.addView(images.get(0));
                        Log.i(TAG, " DetailItemController: apiRequestListenerImages, image was added to view: " + images.get(0));
                        //                        detail_ll_images.getChildAt(0).setScaleY((float) 1.4);
                        //                        detail_ll_images.getChildAt(0).setScaleX((float) 1.4);
                    } else { //else setting the "no_image" pic
                        ImageView imageView = new ImageView(getApplicationContext());
                        imageView.setImageResource(R.mipmap.no_image);
                        detail_ll_images.addView(imageView);
                    }
                    findViewById(R.id.detail_tempImageWhiteBlank).setBackground(null);

                    detail_ll_images.setClickable(true);
                    saveDetailedViewToDB();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
            try {
                Log.i(TAG, " the apiRequestListenerImages response is onFailure() method, error is: " + t.toString());
                //            Toast.makeText(getApplicationContext(),
                //                    "A connection cannot be established. Try again later...", Toast.LENGTH_SHORT).show();
                findViewById(R.id.simple_progress_bar).setVisibility(View.GONE);
                //            onBackPressed();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    //endregion

    @Override
    protected void onDestroy() {
        super.onDestroy();
        apiRequestListenerDescription = null;
        apiRequestListenerImages = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_item_view);

        Intent intent = getIntent();
        item = (Item) intent.getSerializableExtra("Item");

        emptyImage = new ImageView(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            emptyImage.setImageDrawable(getDrawable(R.mipmap.no_image));
        }

        images = new ArrayList();

        TextView detail_tvNumber = (TextView) findViewById(R.id.detail_tvNumber);
        TextView detail_tvTitle = (TextView) findViewById(R.id.detail_tvTitle);
        detail_tvDescription = (TextView) findViewById(R.id.detail_tvDescription);
//        TextView detail_tvPrice = (TextView) findViewById(R.id.detail_tvPrice);

        detail_ll_images = (LinearLayout) findViewById(R.id.detail_ll_images);
        //Added spinner during a picture is loading
        /*ImageView imageView = new ImageView(this);
        Glide.with(this)
                .asGif()
                .load(R.mipmap.loading_spinner3)
                .into(imageView);
        detail_ll_images.addView(imageView);*/

        gestureDetector = initGestureDetector();
        detail_ll_images.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                //changePicture();
                return gestureDetector.onTouchEvent(event);
            }
        });

        //Getting a number (id) of an item
        parsedNumber = item.getId();
        detail_tvNumber.setText(parsedNumber);

        //Getting a title of an item
        String parsedTitle = item.getTitle();
        detail_tvTitle.setText(parsedTitle);

        //Getting a price of an item
        double parsedPrice = item.getPrice();
//        detail_tvPrice.setText(String.valueOf(parsedPrice));
        this.setTitle("$" + String.valueOf(parsedPrice));

        ApiRequestListener apiRequestListenerDescription_old = new ApiRequestListener() {
            @Override
            public void onRequestProcessed(int requestCode, ApiResponse payload) {
                Log.i(TAG, " DetailItemController: apiRequestListenerDescription is started");
                if (payload != null) {
                    JSonParser jSonParser = new JSonParser();
                    String parsedDescription = jSonParser.parseDescription(payload.getContent(), getApplicationContext());
                }
            }

            @Override
            public void onRequestStarted(int requestCode) {

            }
        };

        RestfulManager restfulMeliManager = new RestfulManager();
//        restfulMeliManager.loadDescriptionByRestAPI(item.getDescriptionsUrl(), apiRequestListenerDescription);
        //Getting description
        restfulMeliManager.startRestAPI(null, apiRequestListenerDescription, null, item.getDescriptionsUrl());

        ApiRequestListener apiRequestListenerImages_old = new ApiRequestListener() {
            @Override
            public void onRequestProcessed(int requestCode, ApiResponse payload) {
                Log.i(TAG, " DetailItemController: apiRequestListenerImages is started");
                if (payload != null) {
                    //parseImages(payload.getContent());
                    detail_ll_images.setClickable(true);
                    saveDetailedViewToDB();
                }
            }

            @Override
            public void onRequestStarted(int requestCode) {

            }
        };

//        restfulMeliManager.loadImagesByRestAPI(item.getImageUrl(), apiRequestListenerImages);
        //Getting image
        restfulMeliManager.startRestAPI(item.getImageUrl(), apiRequestListenerImages, item.getImageUrl(), null);
    }

    //Writing id of visited detailed item to a DB
    private void saveDetailedViewToDB() {
        DBHelperVisitedItems dbHelperLastVisited = new DBHelperVisitedItems(this);

        //Creating an object for data
        ContentValues cv = new ContentValues();

        //Connecting to a db
        SQLiteDatabase db = dbHelperLastVisited.getWritableDatabase();

        ArrayList<String> arrayListOfNumbers = new ArrayList<>();
        arrayListOfNumbers = dbHelperLastVisited.getDB(db);

        //Erasing DB to create a new one
        try {
            dbHelperLastVisited.clearDB(db);
        } catch (Exception e) {
            Log.d(TAG, "--- Cannot delete the table: ---");
        }

        Log.d(TAG, "--- Insert in a table: ---");

        //read the db
        //dbHelperLastVisited.readDB(db);

        //Checking if the item was opened before. If it was then delete it from db and add last
//        for (int i = 0; i < arrayListOfNumbers.size(); i++) {
//            if (arrayListOfNumbers.get(i).equals(parsedNumber))
//                arrayListOfNumbers.remove(i);
//        }
        int index = arrayListOfNumbers.indexOf(String.valueOf(parsedNumber));
        if (index >= 0)
            arrayListOfNumbers.remove(index);

        //Preparing an arrayList to write to DB
        arrayListOfNumbers.add(parsedNumber);
        while (arrayListOfNumbers.size() > MAXIMUM_LAST_VISITED_NUMBER) {
            arrayListOfNumbers.remove(0);
        }

        //cv.put(DataKeepContract.TableFields.COLUMN_NUMBER, parsedNumber);
        for (String el : arrayListOfNumbers) {
            cv.put(DataKeepContract.TableFields.COLUMN_NUMBER, el);
            //Inserting data to the table and getting its ID
            long rowID = db.insert(DataKeepContract.TableFields.TABLE_NAME, null, cv);
            Log.d(TAG, "row inserted, ID = " + rowID);
        }

        //read the db
        dbHelperLastVisited.readDB(db);

        dbHelperLastVisited.close();
        Log.d(TAG, "dbHelperLastVisited is closed");
    }

    //Method for gestures when we want to swipe images of an item
    private GestureDetector initGestureDetector() {
        return new GestureDetector(new GestureDetector.SimpleOnGestureListener() {

            private SwipeDetector detector = new SwipeDetector();

            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                                   float velocityY) {
                try {
                    if (detector.isSwipeDown(e1, e2, velocityY)) {
//                        return false;
                    } else if (detector.isSwipeUp(e1, e2, velocityY)) {
//                        showToast("Up Swipe");
                    } else if (detector.isSwipeLeft(e1, e2, velocityX)) {
//                        showToast("Left Swipe");
                        changePicture("left");
                    } else if (detector.isSwipeRight(e1, e2, velocityX)) {
                        changePicture("right");
//                        showToast("Right Swipe");
                    }
                } catch (Exception e) {
                } //for now, ignore
                return false;
            }

//            private void showToast(String phrase) {
//                Toast.makeText(getApplicationContext(), phrase, Toast.LENGTH_SHORT).show();
//            }
        });
    }

    /*private void parseDescription(String jsonString) {
        String parsedDescription = "";
        try {
            JSONArray tmpJA = new JSONArray(jsonString);
            JSONObject tmpJO = tmpJA.getJSONObject(0);
            parsedDescription = tmpJO.getString("plain_text");
            Log.i(TAG, " DetailItemController: parseDescription() description is: " + parsedDescription);
            if (parsedDescription.equals(""))
                parsedDescription = getResources().getString(R.string.no_description); //"No description"
            detail_tvDescription.setText(parsedDescription);
        } catch (JSONException e) {
            Log.i(TAG, " DetailItemController: parseDescription() an Error in json: " + e);
            detail_tvDescription.setText(getResources().getString(R.string.no_description)); //"No description"
        }
    }*/

    /*private void parseImages(String jsonString) {
        findViewById(R.id.detail_tempImageNoPhoto).setBackground(null);
        try {
            JSONObject tmpJO = new JSONObject(jsonString);
            JSONArray tmpJA = tmpJO.getJSONArray("pictures");
            Log.i(TAG, " DetailItemController: parseImages() images are: " + tmpJA);
            if (tmpJA.length() > 0) {
                detail_ll_images.removeAllViews(); //delete the loading spinner pic from layout
                for (int i = 0; i < tmpJA.length(); i++) {
                    String concreteImageUrl = tmpJA.getJSONObject(i).get("url").toString();
                    item.setImagesUrl(concreteImageUrl);


                    Log.i(TAG, " +++ DetailItemController: parseImages() Glide is started");
                    RequestOptions options = new RequestOptions()
                            .centerCrop()
//                            .fitCenter()
                            .override(500, 500)
//                            .placeholder(R.mipmap.loading_spinner)
                            .error(R.mipmap.no_image)
                            .priority(Priority.HIGH);

                    ImageView imageView = new ImageView(this);
                    Glide.with(this)
//                            .asBitmap()
                            .load(concreteImageUrl)
                            .apply(options)
                            .into(imageView)
                    ;

                    imageView.buildDrawingCache();
                    //add a bitmap to a parsedImages arrayList
//                    parsedImages.add(imageView.getDrawingCache());
                    images.add(new ImageView(this));
                    images.set(images.size() - 1, imageView);

                    if (detail_ll_images.getChildCount() == 0 && images.size() > 0) {
                        detail_ll_images.addView(images.get(0));
                        Log.i(TAG, " DetailItemController: parseImages() image was added to view: " + images.get(0));
//                        detail_ll_images.getChildAt(0).setScaleY((float) 1.4);
//                        detail_ll_images.getChildAt(0).setScaleX((float) 1.4);
                    }

                    Log.i(TAG, " DetailItemController: parseImages() is finished with tmpJA.length() > 0");
                }
            } else {
                ImageView imageView = new ImageView(this);
                imageView.setImageResource(R.mipmap.no_image);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }*/

    //Changing of detailed image when swiping the picture
    public void changePicture(String direction) {
        detail_ll_images.removeAllViews();
        if (images.size() > 0) {
            if (direction.equals("right")) {
                if (currentPicture + 1 < images.size()) {
                    currentPicture++;
                } else {
                    currentPicture = 0;
                }
            } else if (direction.equals("left")) {
                if (currentPicture - 1 >= 0) {
                    currentPicture--;
                } else {
                    currentPicture = images.size() - 1;
                }
            }
            detail_ll_images.addView(images.get(currentPicture));
//            detail_ll_images.getChildAt(0).setScaleY((float) 1.4);
//            detail_ll_images.getChildAt(0).setScaleX((float) 1.4);
        } else {
            detail_ll_images.addView(emptyImage);
        }
    }

    //region Menu making
    //Preparing the menu (action bar)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu() was started in SearchresultController");
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        menu_detailActivity = menu;

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
                Intent intent = new Intent(DetailItemController.this, MainViewController.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    //endregion

}