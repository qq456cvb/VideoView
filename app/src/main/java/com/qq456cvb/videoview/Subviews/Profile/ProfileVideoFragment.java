package com.qq456cvb.videoview.Subviews.Profile;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;

import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.qq456cvb.videoview.Adapters.Profile.ProfileVideoAdapter;
import com.qq456cvb.videoview.R;
import com.qq456cvb.videoview.Subviews.VideoFragment;
import com.qq456cvb.videoview.Tools.VideoLoader;
import com.qq456cvb.videoview.Utils.UserClient;
import com.qq456cvb.videoview.Utils.UserVideo;

import org.apache.http.Header;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by qq456cvb on 9/26/15.
 */
public class ProfileVideoFragment extends Fragment implements VideoLoader.OnLoadedListener {
    private View view;
    private ListView listView;
    private ProfileVideoAdapter profileVideoAdapter;
    private ArrayList<UserVideo> videos;
    private ArrayList<UserVideo> deleteList = new ArrayList<>();
    private ImageButton btnDelete;
    private Button checkAll;
    public VideoFragment videoFragment;
    private VideoLoader videoLoader = new VideoLoader(this);
    private boolean online;
    private int counter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        online = getArguments().getBoolean("online");
        videos = new ArrayList<>();
        view = inflater.inflate(R.layout.profile_video, container, false);
        listView = (ListView)view.findViewById(R.id.video_list);
        btnDelete = (ImageButton)view.findViewById(R.id.btn_video_delete);
        checkAll = (Button)view.findViewById(R.id.btn_video_all);

        bindOnClickListeners();

        if (!online) {
            btnDelete.setVisibility(View.GONE);
            checkAll.setVisibility(View.GONE);
        }

        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        videoFragment = new VideoFragment();
//        videoFragment.changeSrc("");
//        Handler handlerSeekbar = new Handler();
//        Runnable runnableSeekbar = new Runnable() {
//            @Override
//            public void run() {
//                videoFragment.changeSrc("", 0);
//            }
//        };
//        handlerSeekbar.postDelayed(runnableSeekbar, 1000);
        transaction.add(R.id.profile_video_fragment, videoFragment);
        transaction.commit();

        if (!online) {
            getLocalVideos();
        } else {
            videoLoader.getVideos();
        }
        profileVideoAdapter = new ProfileVideoAdapter(this, videos, listView, online);
        listView.setAdapter(profileVideoAdapter);

        return view;
    }

    public void changeSrc(String url, float startTime) {
        videoFragment.changeSrc(url, startTime);
    }

    public void onDestroy() {
        videoFragment.onDestroy();
        super.onDestroy();
//
    }

    public void onLoaded(ArrayList<UserVideo> videos) {
        this.videos.clear();
        this.videos.addAll(videos);
        profileVideoAdapter.notifyDataSetChanged();
    }

    private void getLocalVideos() {
        File sdDir = null, videoPath = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(android.os.Environment.MEDIA_MOUNTED); //判断sd卡是否存在
        if (sdCardExist)
        {
            sdDir = Environment.getExternalStorageDirectory();//获取跟目录
            videoPath = new File(sdDir.toString() + "/stpy/video");
            if (!videoPath.exists()) {
                videoPath.mkdirs();
            }
            File[] files = videoPath.listFiles();
            if (files.length > 0)
            {
                for (File file : files)
                {
                    if (file.isDirectory())
                    {
                        //TODO
                    }
                    else {
                        //判断是文件，则进行文件名判断
                        try {
                            if (file.getName().contains("mp4")||file.getName().contains("mp4".toUpperCase()) ||
                                    file.getName().contains("mp3")||file.getName().contains("mp3".toUpperCase()))
                            {
                                UserVideo userVideo = new UserVideo();
                                userVideo.url = file.getAbsolutePath();
                                videos.add(userVideo);
                            }
                        } catch(Exception e) {
                            // TODO
                        }
                    }
                }
            }
        } else {
            //TODO
        }
    }

    public void toggleFullscreen(boolean fullscreen) {
        if (fullscreen) {
            listView.setVisibility(View.GONE);
            Message msg = Message.obtain(videoFragment.mHandler, VideoFragment.VideoSizeChanged, videoFragment.getView().getWidth(), videoFragment.getView().getHeight());
            msg.sendToTarget();
        } else {
            listView.setVisibility(View.VISIBLE);
            Message msg = Message.obtain(videoFragment.mHandler, VideoFragment.VideoSizeChanged, videoFragment.getView().getWidth(), videoFragment.getView().getHeight());
            msg.sendToTarget();
        }
    }

    public void addDelete(UserVideo video) {
        deleteList.add(video);
    }

    public void popDelete(UserVideo video) {
        deleteList.remove(video);
    }

    private void bindOnClickListeners() {
        checkAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = true;
                Iterator iter = profileVideoAdapter.checkBoxHashMap.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    Object key = entry.getKey();
                    CheckBox box = (CheckBox)entry.getValue();
                    if (!box.isChecked()) {
                        checked = false;
                        break;
                    }
                }
                if (!checked) {
                    Iterator iter2 = profileVideoAdapter.checkBoxHashMap.entrySet().iterator();
                    while (iter2.hasNext()) {
                        Map.Entry entry = (Map.Entry) iter2.next();
                        Object key = entry.getKey();
                        CheckBox box = (CheckBox)entry.getValue();
                        box.setChecked(true);
                    }
                    deleteList.clear();
                    deleteList.addAll(videos);
                } else {
                    Iterator iter3 = profileVideoAdapter.checkBoxHashMap.entrySet().iterator();
                    while (iter3.hasNext()) {
                        Map.Entry entry = (Map.Entry) iter3.next();
                        Object key = entry.getKey();
                        CheckBox box = (CheckBox)entry.getValue();
                        box.setChecked(false);
                    }
                    deleteList.clear();
                }
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileVideoFragment.this.getActivity());
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
                                    videoLoader.getVideos();
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
                                        params.put("provincialBox", deleteList.get(i).id);
                                        UserClient.post("/stpy/videoMainAction!alldeleteVideo.action", params, handler);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();
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
}
