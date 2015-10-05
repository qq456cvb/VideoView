package com.qq456cvb.videoview.Adapters.Profile;

import android.app.Activity;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.qq456cvb.videoview.R;
import com.qq456cvb.videoview.Subviews.Profile.ProfileImageFragment;
import com.qq456cvb.videoview.Utils.UserImage;

import java.util.List;

/**
 * Created by qq456cvb on 8/22/15.
 */
public class ProfileImageAdapter extends ArrayAdapter<UserImage> {

    private GridView gridView;
    ProfileImageFragment profileImageFragment;
    public ProfileImageAdapter(ProfileImageFragment profileImageFragment, List<UserImage> imageGridWithTexts, GridView gridView1) {
        super(profileImageFragment.getActivity(), 0, imageGridWithTexts);
        this.profileImageFragment = profileImageFragment;
        this.gridView = gridView1;
    }

    class ViewHolder{
        public NetworkImageView imageView;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        Activity activity = (Activity) getContext();
        final UserImage image = getItem(position);
        // Load the image and set it on the ImageView
        String imageUrl = image.getURL();
        imageUrl = imageUrl.replaceAll(" ", "%20");
        imageUrl = imageUrl.replaceAll("&amp;", "&");
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            // Inflate the views from XML
            LayoutInflater inflater = activity.getLayoutInflater();
            convertView = inflater.inflate(R.layout.profile_image_grid_item, null);
            holder.imageView = (NetworkImageView) convertView.findViewById(R.id.network_image_view);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ImageLoader imLoader = new ImageLoader(profileImageFragment.mQueue, profileImageFragment.bitmapLruCache);
        holder.imageView.setDefaultImageResId(R.drawable.my_loading);
        holder.imageView.setErrorImageResId(R.drawable.error);
        holder.imageView.setImageUrl(imageUrl, imLoader);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message msg = Message.obtain(ProfileImageFragment.handler);
                msg.arg1 = position % 2;
                msg.obj = image.getReviewId();
                msg.sendToTarget();
            }
        });

        return convertView;
    }

}
