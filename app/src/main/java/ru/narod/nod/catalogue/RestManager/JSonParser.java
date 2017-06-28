package ru.narod.nod.catalogue.RestManager;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ru.narod.nod.catalogue.Item;
import ru.narod.nod.catalogue.Model.Model;
import ru.narod.nod.catalogue.R;

import static ru.narod.nod.catalogue.R.id.detail_tvDescription;


public class JSonParser {

    final String TAG = "myLogs." + getClass().getSimpleName();
//    Model model = Model.getInstance();
//    String stringApiRequestListener = "";

    public JSonParser() {
        Log.i(TAG, " JSonParser constructor: is done!");
    }

    public Item[] getArrayOfItems(String jsonString) {
        JSONObject jsonObjectMain = null;

        try {
            jsonObjectMain = new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONArray jsonArrayItems = null;
        try {
            jsonArrayItems = jsonObjectMain.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Item[] resultOfParsing = new Item[jsonArrayItems.length()];

//        Map dict = new HashMap();
//        Iterator iter = jsonObjectMain.keys();
//        Log.d(TAG, "iter: " + iter);
//        int i = 0;
//        while(iter.hasNext()) {
//            String key = (String) iter.next();
//            Log.d(TAG, "key: " + key);
        for (int i = 0; i < jsonArrayItems.length(); i++) {
            try {
                JSONObject jo = jsonArrayItems.getJSONObject(i);
                resultOfParsing[i] = new Item();
                resultOfParsing[i].setId(jo.getString("id"));
                resultOfParsing[i].setTitle(jo.getString("title"));
                resultOfParsing[i].setDescriptionsUrl(jo.getString("id"));
                resultOfParsing[i].setPrice(jo.getDouble("price"));
                resultOfParsing[i].setThumbnailUrl(jo.getString("thumbnail"));
                resultOfParsing[i].setImageUrl(jo.getString("id") /*+ "/pictures"*/);
                JSONArray ja = jo.getJSONArray("pictures");
                for (int j = 0; j < ja.length(); j++) {
                    JSONObject joFromJa = ja.getJSONObject(j);
                    resultOfParsing[i].setImagesUrl(joFromJa.getString("secure_url") /*+ "/pictures"*/);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            /*try {
                resultOfParsing[i].setId(jsonArrayItems.getString("id"));
                resultOfParsing[i].setTitle(jsonArrayItems.getString("title"));
                //resultOfParsing[i].setDescriptionsUrl(jsonObjectItems.getString("descriptions"));
                resultOfParsing[i].setPrice(jsonArrayItems.getDouble("price"));
                resultOfParsing[i].setThumbnailUrl(jsonArrayItems.getString("thumbnail"));
                //resultOfParsing[i].setImageUrl(jsonObjectItems.getString("picctures"));
            } catch (JSONException e) {
                e.printStackTrace();
            }*/

        }

        return resultOfParsing;
    }

    //Taking a description from json
    public String parseDescription(String jsonString, Context context) {
        String parsedDescription = "";
        try {
            JSONArray tmpJA = new JSONArray(jsonString);
            JSONObject tmpJO = tmpJA.getJSONObject(0);
            parsedDescription = tmpJO.getString("plain_text");
            Log.i(TAG, " DetailItemController: parseDescription() description is: " + parsedDescription);
            if (parsedDescription.equals(""))
                parsedDescription = context.getResources().getString(R.string.no_description); //"No description";
        } catch (JSONException e) {
            Log.i(TAG, " DetailItemController: parseDescription() an Error in json: " + e);
            parsedDescription = context.getResources().getString(R.string.no_description); //"No description"
        }
        return parsedDescription;
    }

    //Taking image URLs from json, puts them to an arrayList and returns
    public ArrayList<ImageView> parseImages(String jsonString, Item item, Context context, int widthOfScreen) {
        ArrayList<ImageView> images = new ArrayList<>();
        try {
            JSONObject tmpJO = new JSONObject(jsonString);
            JSONArray tmpJA = tmpJO.getJSONArray("pictures");
            Log.i(TAG, " DetailItemController: parseImages() images are: " + tmpJA);
            if (tmpJA.length() > 0) {
                for (int i = 0; i < tmpJA.length(); i++) {
                    String concreteImageUrl = tmpJA.getJSONObject(i).get("url").toString();
                    item.setImagesUrl(concreteImageUrl);


                    Log.i(TAG, " +++ DetailItemController: parseImages() Glide is started");
                    RequestOptions options = new RequestOptions()
                            .centerCrop()
//                            .fitCenter()
                            .override(widthOfScreen, widthOfScreen)
//                            .placeholder(R.mipmap.loading_spinner)
                            .error(R.mipmap.no_image)
                            .priority(Priority.HIGH);

                    ImageView imageView = new ImageView(context);
                    Glide.with(context)
//                            .asBitmap()
                            .load(concreteImageUrl)
                            .apply(options)
                            .into(imageView)
                    ;

//                    imageView.buildDrawingCache();
                    //add a bitmap to the images arrayList
//                    parsedImages.add(imageView.getDrawingCache());

                    images.add(imageView);
//                    images.add(new ImageView(context));
//                    images.set(images.size() - 1, imageView);

                    Log.i(TAG, " DetailItemController: parseImages() is finished with tmpJA.length() > 0");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return images;
    }
}
