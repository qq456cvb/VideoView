package com.qq456cvb.videoview.Subviews.Profile;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageButton;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.qq456cvb.videoview.Adapters.Profile.ProfileImageAdapter;
import com.qq456cvb.videoview.Adapters.Profile.ProfileImageSetAdapter;
import com.qq456cvb.videoview.R;
import com.qq456cvb.videoview.Tools.BitmapLruCache;
import com.qq456cvb.videoview.Tools.ProfileImageLoader;
import com.qq456cvb.videoview.Utils.UserClient;
import com.qq456cvb.videoview.Utils.UserImage;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qq456cvb on 8/22/15.
 */

// ugly code due to the test, wait for JDBC beans
public class ProfileImageFragment extends Fragment implements ProfileImageLoader.OnLoadedListener {
    private static final String IMAGE_DELETE_URL = "/stpy/showImageAction!alldeleteImage.action";
    public RequestQueue mQueue;
    public static BitmapLruCache bitmapLruCache = new BitmapLruCache();
    private String reviewId;
    private int counter;

    private GridView image_grid;
    private View view;
    private ImageButton btnDelete;

    public static Handler handler;
    private ProfileImageLoader profileImageLoader = new ProfileImageLoader(this);
    private List<UserImage> list = new ArrayList<UserImage>();
    private List<UserImage> detailList = new ArrayList<UserImage>();
    private List<UserImage> deleteList = new ArrayList<>();
    private ProfileImageAdapter profileImageAdapter;
    private ProfileImageSetAdapter profileImageSetAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {


        view = inflater.inflate(R.layout.profile_image, container, false);
        image_grid = (GridView)view.findViewById(R.id.image_grid);
        btnDelete = (ImageButton)view.findViewById(R.id.btn_delete);

        bindOnClickListeners();

        profileImageAdapter = new ProfileImageAdapter(this, list, image_grid);
        profileImageSetAdapter = new ProfileImageSetAdapter(this, detailList, image_grid);

        handler = new Handler() {
            public void handleMessage(Message message) {
                btnDelete.setVisibility(View.VISIBLE);
                reviewId = (String)message.obj;
                profileImageLoader.getSecondImagesByReviewId((String)message.obj);
            }
        };
        mQueue = Volley.newRequestQueue(this.getActivity());
        return view;
    }


    private void bindOnClickListeners() {
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileImageFragment.this.getActivity());
                builder.setMessage("确认删除吗？");
                builder.setTitle("提示");
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        counter = 0;
                        final TextHttpResponseHandler handler = new TextHttpResponseHandler() {
                            public void onSuccess(int statusCode, Header[] headers, String response) {
                                if (++counter == deleteList.size()) {
                                    deleteList.clear();
                                    profileImageLoader.getSecondImagesByReviewId(reviewId);
                                }
                            }

                            public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                                Log.d("test", "sssss");
                            }
                        };
                        new Thread() {
                            @Override
                            public void run() {
                                try {
                                    for (int i = 0; i < deleteList.size(); i++) {
                                        RequestParams params = new RequestParams();
                                        params.put("provincialBox", deleteList.get(i).getProvincial());
                                        params.put("reviewid", deleteList.get(i).getReviewId());
                                        UserClient.post(IMAGE_DELETE_URL, params, handler);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();

            }
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden && image_grid != null) {
            profileImageLoader.getImages();
            btnDelete.setVisibility(View.INVISIBLE);
        }
    }

    public void addDelete(UserImage image) {
        deleteList.add(image);
    }

    public void popDelete(UserImage image) {
        deleteList.remove(image);
    }

    public void onLoaded(ArrayList<UserImage> images, int type) {
        if (type == 0) {
            list.clear();
            list.addAll(images);
            image_grid.setAdapter(profileImageAdapter);
            profileImageAdapter.notifyDataSetChanged();
        } else {
            deleteList.clear();
            detailList.clear();
            detailList.addAll(images);
            image_grid.setAdapter(profileImageSetAdapter);
            profileImageSetAdapter.notifyDataSetChanged();
        }
    }

}
