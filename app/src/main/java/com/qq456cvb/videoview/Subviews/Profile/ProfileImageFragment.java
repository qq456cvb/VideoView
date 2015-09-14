package com.qq456cvb.videoview.Subviews.Profile;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.qq456cvb.videoview.Adapters.Profile.ProfileImageAdapter;
import com.qq456cvb.videoview.Adapters.Profile.ProfileImageSetAdapter;
import com.qq456cvb.videoview.R;
import com.qq456cvb.videoview.Tools.ImageLoader;
import com.qq456cvb.videoview.Utils.UserImage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qq456cvb on 8/22/15.
 */

// ugly code due to the test, wait for JDBC beans
public class ProfileImageFragment extends Fragment implements ImageLoader.OnLoadedListener {
    private GridView image_grid;
    private View view;
    public static Handler handler;
    private ImageLoader imageLoader = new ImageLoader(this);
    private List<UserImage> list = new ArrayList<UserImage>();
    private List<UserImage> detailList = new ArrayList<UserImage>();
    private ProfileImageAdapter profileImageAdapter;
    private ProfileImageSetAdapter profileImageSetAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {


        view = inflater.inflate(R.layout.profile_image, container, false);
        image_grid = (GridView)view.findViewById(R.id.image_grid);

        profileImageAdapter = new ProfileImageAdapter(this.getActivity(), list, image_grid);
        profileImageSetAdapter = new ProfileImageSetAdapter(this.getActivity(), detailList, image_grid);
        image_grid.setAdapter(profileImageAdapter);

        handler = new Handler() {
            public void handleMessage(Message message) {
                switch (message.arg1) {
                    case 0:
                        imageLoader.getImagesByReviewId((String)message.obj);
                        break;
                    case 1:
                        imageLoader.getImagesByReviewId((String)message.obj);
                        break;
                }
            }
        };

        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden && image_grid != null) {

            imageLoader.getImages();
            image_grid.setAdapter(profileImageAdapter);
        }
    }

    public void onLoaded(ArrayList<UserImage> images, int type) {

        if (type == 0) {
            list.clear();
            list.addAll(images);
            image_grid.setAdapter(profileImageAdapter);
            profileImageAdapter.notifyDataSetChanged();
        } else {
            detailList.clear();
            detailList.addAll(images);
            image_grid.setAdapter(profileImageSetAdapter);
            profileImageSetAdapter.notifyDataSetChanged();
        }
    }

}
