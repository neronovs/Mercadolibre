package ru.narod.nod.catalogue.ViewControllers;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import ru.narod.nod.catalogue.Item;
import ru.narod.nod.catalogue.R;

/**
 * title, price and a thumbnail
 */

public class SimpleItemController extends Fragment {

    private TextView simple_tvTitle;
    private TextView simple_tvPrice;
    private ImageView simple_imageThumbnail;
    private String simple_imageThumbnailUrl = "";
    private String[] tmpArr = new String[3];
    private boolean startGettingNewFifteenElements = false;
    private GridLayout simple_item_view_grid_layout;
    private final String TAG = "myLogs." + getClass().getSimpleName();
    private Item item;
    private View v;

    private boolean tv_no_result_visibility = false;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.simple_item_view, null);

        simple_tvTitle = (TextView) v.findViewById(R.id.simple_tvTitle);
        simple_tvTitle.setText(tmpArr[0]);
        simple_tvPrice = (TextView) v.findViewById(R.id.simple_tvPrice);
        simple_tvPrice.setText(tmpArr[1]);

        simple_imageThumbnail = (ImageView) v.findViewById(R.id.simple_imageThumbnail);
        simple_imageThumbnailUrl = tmpArr[2];

        TextView tv_no_result = (TextView) v.findViewById(R.id.simple_no_result);
        if (tv_no_result_visibility)
            tv_no_result.setVisibility(View.VISIBLE);

        //Getting thumbnail for the form
//        DownloadImageTask downloadImageTask = new DownloadImageTask(simple_imageThumbnail);
//        downloadImageTask.execute(simple_imageThumbnailUrl);
        try {
//            Picasso.with(getActivity())
//                    .load(simple_imageThumbnailUrl)
//                    .placeholder(getResources().getDrawable(R.mipmap.loading_spinner))
//                    .error(getResources().getDrawable(R.mipmap.no_image))
//                    .into(simple_imageThumbnail);

            RequestOptions options = new RequestOptions()
                    .placeholder(R.mipmap.no_photo)
                    .error(R.mipmap.no_image)
                    .priority(Priority.HIGH);
            Glide.with(getActivity())
                    .load(simple_imageThumbnailUrl.equals("no result") ?
                            R.mipmap.empty_frame : simple_imageThumbnailUrl)
                    .apply(options)
                    //Using this listener to set a spinner invisible
                    .listener(new RequestListener() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                    Target target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Object resource, Object model, Target target,
                                                       DataSource dataSource, boolean isFirstResource) {
                            v.findViewById(R.id.simple_progress_bar).setVisibility(View.INVISIBLE);
                            return false;
                        }
                    })
                    .into(simple_imageThumbnail);

        } catch (Exception e) {
            Log.e(TAG, " SimpleItemController, onCreateView(): exception: " + e);
        }

        simple_item_view_grid_layout = (GridLayout) v.findViewById(R.id.simple_item_view_grid_layout);
        simple_item_view_grid_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, " SimpleItemController: onClick() started");

                switch (v.getId()) {
                    case R.id.simple_item_view_grid_layout:
                        Log.i(TAG, " SimpleItemController: onClick() simple_item_view_grid_layout pressed");
                        openDetailItemView();
                        break;
                }
            }
        });

        return v;
    }

    private void openDetailItemView() {
        Log.i(TAG, " SimpleItemController: openDetailItemView() started");
        Intent intent = new Intent(v.getContext(), DetailItemController.class);
        intent.putExtra("Item", item);
        startActivity(intent);
    }

    ///////////////********************

    //region Getters & Setters
    public TextView getSimple_tvTitle() {
        return simple_tvTitle;
    }

    public void setSimple_tvTitle(TextView simple_tvTitle) {
        this.simple_tvTitle = simple_tvTitle;
    }

    public TextView getSimple_tvPrice() {
        return simple_tvPrice;
    }

    public void setSimple_tvPrice(TextView simple_tvPrice) {
        this.simple_tvPrice = simple_tvPrice;
    }

    public ImageView getSimple_imageThumbnail() {
        return simple_imageThumbnail;
    }

    public void setSimple_imageThumbnail(ImageView simple_imageThumbnail) {
        this.simple_imageThumbnail = simple_imageThumbnail;
    }

    public String getSimple_imageThumbnailUrl() {
        return simple_imageThumbnailUrl;
    }

    public void setSimple_imageThumbnailUrl(String simple_imageThumbnailUrl) {
        this.simple_imageThumbnailUrl = simple_imageThumbnailUrl;
    }

    public void setTmpArr(String[] tmpArr) {
        this.tmpArr = tmpArr;
    }

    public String[] getTmpArr() {
        return tmpArr;
    }


    public boolean isStartGettingNewFifteenElements() {
        return startGettingNewFifteenElements;
    }

    public void setStartGettingNewFifteenElements(boolean startGettingNewFifteenElements) {
        this.startGettingNewFifteenElements = startGettingNewFifteenElements;
    }

    public GridLayout getSimple_item_view_grid_layout() {
        return simple_item_view_grid_layout;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public void setTv_no_result_visibility(boolean tv_no_result_visibility) {
        this.tv_no_result_visibility = tv_no_result_visibility;
    }
//endregion

}
