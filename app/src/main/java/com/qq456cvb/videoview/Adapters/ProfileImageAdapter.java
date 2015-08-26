package com.qq456cvb.videoview.Adapters;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.qq456cvb.videoview.R;
import com.qq456cvb.videoview.Subviews.ProfileImageFragment;
import com.qq456cvb.videoview.Tools.AsyncImageLoader;
import com.qq456cvb.videoview.Utils.ImageGridWithText;

import java.util.List;

/**
 * Created by qq456cvb on 8/22/15.
 */
public class ProfileImageAdapter extends ArrayAdapter<ImageGridWithText> {

    private GridView gridView;
    private AsyncImageLoader asyncImageLoader;
    public ProfileImageAdapter(Activity activity, List<ImageGridWithText> imageGridWithTexts, GridView gridView1) {
        super(activity, 0, imageGridWithTexts);
        this.gridView = gridView1;
        asyncImageLoader = new AsyncImageLoader();
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        Activity activity = (Activity) getContext();

        // Inflate the views from XML
        View rowView;
        LayoutInflater inflater = activity.getLayoutInflater();
        rowView = inflater.inflate(R.layout.profile_image_grid_item, null);
        ImageGridWithText imageGridWithText = getItem(position);

        // Load the image and set it on the ImageView
        String imageUrl = imageGridWithText.getImageUrl();
        ImageView imageView = (ImageView)rowView.findViewById(R.id.grid_item_image);
        imageView.setTag(String.valueOf(position));
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message msg = Message.obtain(ProfileImageFragment.handler);
                msg.arg1 = position % 2;
                msg.sendToTarget();
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
        // Set the text on the TextView
        TextView textView = (TextView)rowView.findViewById(R.id.grid_item_text);
        textView.setText(imageGridWithText.getText());
        return rowView;
    }

}
