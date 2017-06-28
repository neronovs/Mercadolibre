package ru.narod.nod.catalogue.dataBase;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.BaseColumns;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * Created by User on 16.05.2017.
 */

public class DataKeepContract {

    //Fields of the table
    public static final class TableFields implements BaseColumns {
        public final static String TABLE_NAME = "favorites_purchases";
        public final static String _ID = "_id";
        public final static String COLUMN_NUMBER = "number";
    }

    //region Converters that could be useful
    //Converting a Bitmap to a ByteArray
    public static byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    //Converting a ByteArray to a Bitmap
    public static Bitmap byteArrayToImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    //Converting arrayList to JSON string
    public static String arrayListToJsonString(ArrayList<String> arrays) {
        final String KEY_VALUE_DELIMITER = ";";
        StringBuilder sb = new StringBuilder();
        int counter = 0;
        for (String array : arrays) {
            if (counter > 0)
                sb.append(KEY_VALUE_DELIMITER);
            sb.append(array);
            counter++;
        }
        return sb.toString();
    }

    //Converting JSON string to arrayList
    public static ArrayList<String> jsonStringToArrayList(String string_val) {
        final String KEY_VALUE_DELIMITER = ";";
        ArrayList<String> arrays = new ArrayList<>();
        if (!string_val.isEmpty()) {
            String[] split = string_val.split(KEY_VALUE_DELIMITER);
            for (int i = 0; i < split.length; i = i + 2) {
                arrays.add(split[i]);
            }
        }
        return arrays;
    }
    //endregion
}
