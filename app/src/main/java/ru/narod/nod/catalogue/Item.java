package ru.narod.nod.catalogue;

import com.mercadolibre.android.sdk.ApiRequestListener;
import com.mercadolibre.android.sdk.ApiResponse;
import com.mercadolibre.android.sdk.Meli;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * The item class consists information about an item, e.d.:
 * 1. product number (id)
 * 2. title
 * 3. descriptionsUrl
 * 4. price
 * 5. thumbnailUrl
 * 6. imageUrl
 */

public class Item implements Serializable {
    private String id;
    private String title;
    private String descriptionsUrl;
    private double price;
    private String thumbnailUrl;
    private String imageUrl;
    private ArrayList<String> imagesUrl; //arrayList to keep all urls of images

    public Item() {
        imagesUrl = new ArrayList<>();
    }

    public Item(String id,
                String title,
                String descriptionsUrl,
                double price,
                String thumbnailUrl,
                String imageUrl) {
        this.id = id;
        this.title = title;
        this.descriptionsUrl = descriptionsUrl;
        this.price = price;
        this.thumbnailUrl = thumbnailUrl;
        this.imageUrl = imageUrl;
    }


    //region Getters

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescriptionsUrl() {
        return descriptionsUrl;
    }

    public double getPrice() {
        return price;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public ArrayList<String> getImagesUrl() {
        return imagesUrl;
    }

    //endregion

    //region Setters
    public void setImageUrl(String urlToGetImage) {
        this.imageUrl = urlToGetImage;
    }

    public void setThumbnailUrl(String urlToGetImage) {
        this.thumbnailUrl = urlToGetImage;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setDescriptionsUrl(String descriptionsUrl) {
        this.descriptionsUrl = descriptionsUrl;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setImagesUrl(String imagesUrl) {
        this.imagesUrl.add(imagesUrl);
    }
    //endregion



}