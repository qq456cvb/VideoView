package com.qq456cvb.videoview.Adapters.Profile;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;

import com.qq456cvb.videoview.R;
import com.qq456cvb.videoview.Tools.AsyncImageLoader;
import com.qq456cvb.videoview.Utils.UserImage;

import java.util.List;

/**
 * Created by qq456cvb on 8/22/15.
 */
public class ProfileImageSetAdapter extends ArrayAdapter<UserImage> {

    private GridView gridView;
    private AsyncImageLoader asyncImageLoader;
    public ProfileImageSetAdapter(Activity activity, List<UserImage> imageGridWithTexts, GridView gridView1) {
        super(activity, 0, imageGridWithTexts);
        this.gridView = gridView1;
        asyncImageLoader = new AsyncImageLoader();
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        Activity activity = (Activity) getContext();

        // Inflate the views from XML
        View rowView;
        LayoutInflater inflater = activity.getLayoutInflater();
        rowView = inflater.inflate(R.layout.profile_image_edit_grid_item, null);
        UserImage image = getItem(position);

        // Load the image and set it on the ImageView
        String imageUrl = image.getURL();

        //checkbox
        final CheckBox checkBox = (CheckBox)rowView.findViewById(R.id.grid_item_edit_checkbox);
        checkBox.setText("");

        //image
        ImageView imageView = (ImageView)rowView.findViewById(R.id.grid_item_edit_image);
        imageView.setTag(String.valueOf(position));
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               checkBox.setChecked(!checkBox.isChecked());
            }
        });
        Drawable cachedImage = asyncImageLoader.loadDrawable(imageUrl, new AsyncImageLoader.ImageCallback() {
            public void imageLoaded(Drawable imageDrawable, String imageUrl) {
                ImageView imageViewByTag = (ImageView) gridView.findViewWithTag(String.valueOf(position));
                if (imageViewByTag != null) {
                    imageViewByTag.setImageDrawable(imageDrawable);
                }
            }
        });
        if (cachedImage == null) {
            imageView.setImageResource(R.drawable.ic_backward_w);
        }else{
            imageView.setImageDrawable(cachedImage);
        }
        return rowView;
    }

}