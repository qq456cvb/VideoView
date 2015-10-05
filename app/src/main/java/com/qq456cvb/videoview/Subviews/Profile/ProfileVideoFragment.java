package com.qq456cvb.videoview.Subviews.Profile;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.qq456cvb.videoview.Adapters.Profile.ProfileVideoAdapter;
import com.qq456cvb.videoview.R;
import com.qq456cvb.videoview.Subviews.VideoFragment;
import com.qq456cvb.videoview.Tools.VideoLoader;

import java.util.ArrayList;

/**
 * Created by qq456cvb on 9/26/15.
 */
public class ProfileVideoFragment extends Fragment implements VideoLoader.OnLoadedListener {
    private View view;
    private ListView listView;
    private ProfileVideoAdapter profileVideoAdapter;
    private ArrayList<String> videos;
    private VideoFragment videoFragment;
    private VideoLoader videoLoader = new VideoLoader(this);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {

        videos = new ArrayList<>();
        view = inflater.inflate(R.layout.profile_video, container, false);
        listView = (ListView)view.findViewById(R.id.video_list);

        bindOnClickListeners();

        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        videoFragment = new VideoFragment();
//        videoFragment.changeSrc("");
        Handler handlerSeekbar = new Handler();
        Runnable runnableSeekbar = new Runnable() {
            @Override
            public void run() {
                videoFragment.changeSrc("");
            }
        };
        handlerSeekbar.postDelayed(runnableSeekbar, 1000);
        transaction.add(R.id.profile_video_fragment, videoFragment);
        transaction.commit();
        profileVideoAdapter = new ProfileVideoAdapter(this, videos, listView);
        listView.setAdapter(profileVideoAdapter);
        videoLoader.getVideos();

        return view;
    }

    public void changeSrc(String url) {
        videoFragment.changeSrc(url);
    }

    public void onDestroy() {
        videoFragment.onDestroy();
        super.onDestroy();
//
    }

    public void onLoaded(ArrayList<String> videos) {
        this.videos.clear();
        this.videos.addAll(videos);
        profileVideoAdapter.notifyDataSetChanged();
    }

    private void bindOnClickListeners() {
//        btnDelete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                counter = 0;
//                final TextHttpResponseHandler handler = new TextHttpResponseHandler() {
//                    public void onSuccess(int statusCode, Header[] headers, String response) {
//                        if (++counter == deleteList.size()) {
//                            profileImageLoader.getImagesByReviewId(reviewId);
//                        }
//                    }
//
//                    public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
//                        Log.d("test", "sssss");
//                    }
//                };
//                new Thread() {
//                    @Override
//                    public void run() {
//                        try {
//                            for (int i = 0; i < deleteList.size(); i++) {
//                                RequestParams params = new RequestParams();
//                                params.put("provincialBox", deleteList.get(i).getProvincial());
//                                params.put("reviewid", deleteList.get(i).getReviewId());
//                                UserClient.post(IMAGE_DELETE_URL, params, handler);
//                            }
//                        }
//                        catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }.start();
//            }
//        });
    }
}
