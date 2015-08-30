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
import com.qq456cvb.videoview.CustomWidgets.ImageGridWithText;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qq456cvb on 8/22/15.
 */

// ugly code due to the test, wait for JDBC beans
public class ProfileImageFragment extends Fragment {
    private GridView image_grid;
    private View view;
    public static Handler handler;
    private List<ImageGridWithText> list = new ArrayList<ImageGridWithText>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {


        view = inflater.inflate(R.layout.profile_image, container, false);
        image_grid = (GridView)view.findViewById(R.id.image_grid);


        String[] paths=new String[30];
        for(int i=0;i<15;i++) {
            paths[2*i] = "/storage/emulated/0/test/" + String.valueOf(1) + ".jpg";
            paths[2*i+1] = "/storage/emulated/0/test/" + String.valueOf(2) + ".jpg";
        }
        for(int i=0;i<30;i++){
            list.add(new ImageGridWithText(paths[i], String.valueOf(i)));
        }
        image_grid.setAdapter(new ProfileImageAdapter(this.getActivity(), list, image_grid));

        handler = new Handler() {
            public void handleMessage(Message message) {
                switch (message.arg1) {
                    case 0:
                        list.clear();
                        for(int i=0;i<15;i++){
                            list.add(new ImageGridWithText("/storage/emulated/0/test/" + String.valueOf(1) + ".jpg", String.valueOf(i)));
                        }
                        image_grid.setAdapter(new ProfileImageSetAdapter(ProfileImageFragment.this.getActivity(), list, image_grid));
                        break;
                    case 1:
                        list.clear();
                        for(int i=0;i<15;i++){
                            list.add(new ImageGridWithText("/storage/emulated/0/test/" + String.valueOf(2) + ".jpg", String.valueOf(i)));
                        }
                        image_grid.setAdapter(new ProfileImageSetAdapter(ProfileImageFragment.this.getActivity(), list, image_grid));
                        break;
                }
            }
        };

        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (hidden && list.size() > 0 && image_grid != null) {
            list.clear();
            String[] paths=new String[30];
            for(int i=0;i<15;i++) {
                paths[2*i] = "/storage/emulated/0/test/" + String.valueOf(1) + ".jpg";
                paths[2*i+1] = "/storage/emulated/0/test/" + String.valueOf(2) + ".jpg";
            }
            for(int i=0;i<30;i++){
                list.add(new ImageGridWithText(paths[i], String.valueOf(i)));
            }
            image_grid.setAdapter(new ProfileImageAdapter(this.getActivity(), list, image_grid));
        }
    }

}
