package com.qq456cvb.videoview.Activities;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.qq456cvb.videoview.Application.GlobalApp;
import com.qq456cvb.videoview.R;
import com.qq456cvb.videoview.Subviews.CommentAndChannelListFragment;
import com.qq456cvb.videoview.Subviews.DownloadAndChannelListFragment;
import com.qq456cvb.videoview.Subviews.MiddleFragment;
import com.qq456cvb.videoview.Subviews.ProfileFragment;
import com.qq456cvb.videoview.Subviews.RightChannelFragment;
import com.qq456cvb.videoview.Subviews.RightFragment;
import com.qq456cvb.videoview.Subviews.VideoFragment;
import com.qq456cvb.videoview.Utils.Channel;
import com.qq456cvb.videoview.Utils.CommentHttpHelper;

import java.util.ArrayList;
import java.util.HashMap;

import jp.wasabeef.sample.CommentPanelRetSwitcher;
import jp.wasabeef.sample.commentPanelFragment;

public class MainActivity extends FragmentActivity implements ProfileFragment.OnProfileListener, CommentPanelRetSwitcher {

    public final static String TAG = "MainActivity";
    public final static int CHANNEL = 1;
    public final static int MYCOMMENT = 2;
    public final static int EDITCOMMENT = 3;
    public final static int PROGRAMME = 4;
    public final static int TOGGLE_FULLSCREEN = 5;
    private long mExitTime = 0;
    private boolean isFullscreen = false;

    private RightFragment mRightFragment = new RightFragment();
    private MiddleFragment mMiddleFragment;
    public ProfileFragment mProfileFragment = new ProfileFragment();
    private CommentAndChannelListFragment comchanFragment = new CommentAndChannelListFragment();
    private commentPanelFragment commentPanelFragment = new commentPanelFragment();
    private DownloadAndChannelListFragment downloadAndChannelListFragment = new DownloadAndChannelListFragment();
    private VideoFragment fullscreenFragment;
    private LinearLayout mWatchButton;
    private LinearLayout mDownloadButton;
    private LinearLayout mCommentButton;
    private LinearLayout mProfileButton;
    private LinearLayout mContentMiddle;
    private FrameLayout mContentRight;
    private FrameLayout mContentTop;
    private LinearLayout mContentLeft;
    private LinearLayout mContentMiddleAndRight;


    public static Handler handler;
    public ProgressDialog progress;

    private boolean lastPlaying = true;
    private LinearLayout lastButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        setHandler();
        findViews();
        lastButton = mWatchButton;
        bindOnClickListeners();

        // default layout
        setDefaultFragment();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!isFullscreen) {
                if ((System.currentTimeMillis() - mExitTime) > 2000) {
                    Object mHelperUtils;
                    Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                    mExitTime = System.currentTimeMillis();

                } else {
                    finish();
                }
                return true;
            } else {
                if (mMiddleFragment.videoFragment != null) {
                    mMiddleFragment.videoFragment.toggleFullscreen();
                }
                if (mProfileFragment.profileVideoFragment != null && mProfileFragment.profileVideoFragment.videoFragment != null) {
                    mProfileFragment.profileVideoFragment.videoFragment.toggleFullscreen();
                }
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public void findViews() {
        mWatchButton = (LinearLayout)findViewById(R.id.watchButton);
        mDownloadButton = (LinearLayout)findViewById(R.id.downloadButton);
        mCommentButton = (LinearLayout)findViewById(R.id.commentButton);
        mProfileButton = (LinearLayout)findViewById(R.id.profileButton);
        mContentMiddle = (LinearLayout)findViewById(R.id.content_middle);
        mContentRight = (FrameLayout)findViewById(R.id.content_right);
        mContentTop = (FrameLayout)findViewById(R.id.content_top);
        mContentLeft = (LinearLayout)findViewById(R.id.content_left);
        mContentMiddleAndRight = (LinearLayout)findViewById(R.id.content_middle_and_right);

    }

    public void bindOnClickListeners() {

        mWatchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastButton.setBackgroundColor(0x003091ff);

                lastButton = mWatchButton;
                resetCatagory();
                clearFragments();
//                if (lastPlaying && lastButton == mProfileButton) {
//                    mMiddleFragment.togglePlay(true);
//                }
                GlobalApp.currentChannel.resetChannel();
                mWatchButton.setBackgroundColor(0xff1945ff);
                mContentMiddle.setVisibility(View.VISIBLE);
                mContentRight.setVisibility(View.VISIBLE);
                mMiddleFragment = new MiddleFragment();
                FragmentManager fm = getFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                transaction.add(R.id.content_middle, mMiddleFragment);
                transaction.show(mRightFragment);
                transaction.commit();
            }
        });
        mDownloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastButton.setBackgroundColor(0x003091ff);

                lastButton = mDownloadButton;
                resetCatagory();
                clearFragments();
//                if (lastPlaying && lastButton == mProfileButton) {
//                    mMiddleFragment.togglePlay(true);
//                }
                GlobalApp.currentChannel.resetChannel();
                mDownloadButton.setBackgroundColor(0xff1945ff);
                mContentMiddle.setVisibility(View.VISIBLE);
                mContentRight.setVisibility(View.VISIBLE);
                mMiddleFragment = new MiddleFragment();
                FragmentManager fm = getFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                transaction.add(R.id.content_middle, mMiddleFragment);
                transaction.show(downloadAndChannelListFragment);
                transaction.commit();
            }
        });
        mCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastButton.setBackgroundColor(0x003091ff);

                lastButton = mCommentButton;
                resetCatagory();
                clearFragments();
//                if (lastPlaying && lastButton == mProfileButton) {
//                    mMiddleFragment.togglePlay(true);
//                }
                GlobalApp.currentChannel.resetChannel();
                mCommentButton.setBackgroundColor(0xff1945ff);
                mContentMiddle.setVisibility(View.VISIBLE);
                mContentRight.setVisibility(View.VISIBLE);
                mMiddleFragment = new MiddleFragment();
                FragmentManager fm = getFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                transaction.add(R.id.content_middle, mMiddleFragment);
                transaction.show(comchanFragment);
                transaction.commit();
            }
        });
        mProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastButton.setBackgroundColor(0x003091ff);
                lastButton = mProfileButton;
                clearFragments();
                mProfileFragment.clearFragments();
                mProfileButton.setBackgroundColor(0xff1945ff);
                FragmentManager fm = getFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                transaction.show(mProfileFragment);
                transaction.commit();
            }
        });


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return true;
    }

    private void setDefaultFragment()
    {
        mMiddleFragment = new MiddleFragment();
        // add all the fragments
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.add(R.id.content_middle, mMiddleFragment);
        transaction.add(R.id.content_right, mRightFragment);
        transaction.add(R.id.content_middle_and_right, mProfileFragment);
        transaction.add(R.id.content_right, commentPanelFragment);
        transaction.add(R.id.content_right, comchanFragment);
        transaction.add(R.id.content_right, downloadAndChannelListFragment);
        transaction.hide(mProfileFragment);
        transaction.hide(mMiddleFragment);
        transaction.hide(commentPanelFragment);
        transaction.hide(comchanFragment);
        transaction.hide(downloadAndChannelListFragment);
        transaction.show(mMiddleFragment);
        transaction.commit();

        mWatchButton.setBackgroundColor(0x003091ff);
        mCommentButton.setBackgroundColor(0x003091ff);
        mDownloadButton.setBackgroundColor(0x003091ff);
        mProfileButton.setBackgroundColor(0x003091ff);
        mWatchButton.setBackgroundColor(0xff1945ff);
    }

    public void clearFragments() {
        mProfileFragment.clearFragments();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        if (mMiddleFragment != null) {
            transaction.remove(mMiddleFragment);
        }
        transaction.hide(mRightFragment);
        transaction.hide(comchanFragment);
        transaction.hide(commentPanelFragment);
        transaction.hide(downloadAndChannelListFragment);
        transaction.hide(mProfileFragment);
        transaction.commit();


        // hide all the fragment container
        mContentMiddle.setVisibility(View.GONE);
        mContentRight.setVisibility(View.GONE);
    }

    public void resetCatagory() {
        if (lastButton == mWatchButton) {
            mRightFragment.getRightChannelFragment().changeCategory("yangshi", 3);
        } else if (lastButton == mDownloadButton) {
            downloadAndChannelListFragment.getRightChannelFragment().changeCategory("yangshi", 3);
        } else if (lastButton == mCommentButton) {
            comchanFragment.getRightChannelFragment().changeCategory("yangshi", 3);
        }
    }
    public RightChannelFragment getRightChannelFragment() {
        if (lastButton == mWatchButton) {
            return mRightFragment.getRightChannelFragment();
        } else if (lastButton == mDownloadButton) {
            return downloadAndChannelListFragment.getRightChannelFragment();
        } else if (lastButton == mCommentButton) {
            return comchanFragment.getRightChannelFragment();
        } else {
            return null;
        }
    }

    public void setHandler() {
        handler = new Handler() {
            public void handleMessage(Message message) {
                switch (message.what) {
                    case CHANNEL: {
                        String type = message.getData().getString("type");
                        if (type.equals("list")) {
                            getRightChannelFragment().changeCategory(message.getData().getString("value"), message.arg1);
                        }
                        else if (type.equals("play")) {
                            mMiddleFragment.changeSrc(message.getData().getString("value"), message.getData().getFloat("startTime"));
                            GlobalApp.currentChannel = (Channel)message.obj;
                        }
                        break;
                    }
                    case PROGRAMME: {
                        String type = message.getData().getString("type");
                        if (type.equals("list")) {
                            getRightChannelFragment().changeCategory(message.getData().getString("value"), message.arg1);
                        }
                        else if (type.equals("play")) {
                            mMiddleFragment.changeSrc(message.getData().getString("value"), message.getData().getFloat("startTime"));
                        }
                        break;
                    }
                    case MYCOMMENT:{
                        clearFragments();
                        FragmentManager fm = getFragmentManager();
                        FragmentTransaction transaction = fm.beginTransaction();
                        transaction.show(mProfileFragment.profileCommentFragment);
                        getCommentList();
                        transaction.commit();
                        break;
                    }
                    case EDITCOMMENT:{
                        mProfileFragment.clearFragments();
                        FragmentManager fm = getFragmentManager();
                        FragmentTransaction transaction = fm.beginTransaction();
                        transaction.hide(mRightFragment);
                        transaction.hide(comchanFragment);
                        transaction.hide(downloadAndChannelListFragment);
                        transaction.hide(mProfileFragment);
                        transaction.show(commentPanelFragment);
                        transaction.commit();
//                        mContentMiddle.setVisibility(View.VISIBLE);
//                        mContentRight.setVisibility(View.VISIBLE);

                        break;
                    }
                    case TOGGLE_FULLSCREEN: {
                        if (message.arg1 == 1) {
                            mContentRight.setVisibility(View.GONE);
                            mContentTop.setVisibility(View.GONE);
                            mContentLeft.setVisibility(View.GONE);
                            LinearLayout.LayoutParams p = (LinearLayout.LayoutParams)mContentMiddleAndRight.getLayoutParams();
                            p.setMargins(0, 0, 0, 0);
                            mContentMiddleAndRight.setLayoutParams(p);
                            mContentMiddleAndRight.requestLayout();
                            mMiddleFragment.toggleFullscreen(true);
                            mProfileFragment.toggleFullscreen(true);
                            isFullscreen = true;
                        } else if (message.arg1 == 0) {
                            mContentRight.setVisibility(View.VISIBLE);
                            mContentTop.setVisibility(View.VISIBLE);
                            mContentLeft.setVisibility(View.VISIBLE);
                            LinearLayout.LayoutParams p = (LinearLayout.LayoutParams)mContentMiddleAndRight.getLayoutParams();
                            p.setMargins(0, 20, 0, 20);
                            mContentMiddleAndRight.setLayoutParams(p);
                            mContentMiddleAndRight.requestLayout();
                            mMiddleFragment.toggleFullscreen(false);
                            mProfileFragment.toggleFullscreen(false);
                            isFullscreen = false;
                        }
                    }
                    default:
                        break;
                }
            }
        };
    }

    @Override
    public void switchRightPanel() {
        clearFragments();
        mContentMiddle.setVisibility(View.VISIBLE);
        mContentRight.setVisibility(View.VISIBLE);
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.show(mMiddleFragment);
        transaction.show(comchanFragment);
        transaction.commit();
    }
    @Override
    public void uploadTxtComment(String title, String content) {
        CommentHttpHelper.uploadTxtCommentHelper(title, content, this);
//        showProgressDialog("上传评论中");
    }

    @Override
    public void getCommentList() {
        CommentHttpHelper.getCommentHelper(MainActivity.this);
//        showProgressDialog("获取评论列表");
    }

    @Override
    public void notifyListChange(ArrayList<HashMap<String, String>> newlist, ArrayList<Integer> queryId) {
        mProfileFragment.profileCommentFragment.changeList(newlist, queryId);
    }

    @Override
    public void getContents(ArrayList<Integer> queryIds) {
        Log.d(TAG, "----getContents");
        CommentHttpHelper.editTxtCommentHelper(queryIds, this);
    }

    @Override
    public void setEditDialogContent(HashMap<Integer,String> map) {
        mProfileFragment.profileCommentFragment.setContents(map);
    }

    @Override
    public void updateComment(int queryid, String title, String content) {
        CommentHttpHelper.updateTxtCommentHelper(queryid, title, content, this);
//        showProgressDialog("更新评论中");
    }

    @Override
    public void deleteComment(int queryid) {
        CommentHttpHelper.deleteTxtCommentHelper(queryid, this);
    }

    @Override
    public void uploadPic(int queryid, String filepath, String fileName) {
        CommentHttpHelper.uploadCommentFileHelper(CommentHttpHelper.PICTURE,queryid, filepath, fileName, this);
    }

    @Override
    public void uploadVideo(int queryid, String filepath, String fileName) {
        CommentHttpHelper.uploadCommentFileHelper(CommentHttpHelper.VIDEO,queryid, filepath, fileName, this);
    }

    @Override
    public void uploadWord(String filepath, String fileName) {
        CommentHttpHelper.uploadWordCommentHelper(filepath, fileName, this);
    }

    @Override
    public void downWord(int queryid, String fileName) {
        CommentHttpHelper.downloadWordHelper(queryid, fileName,this, this);
    }

    @Override
    public void showProgressDialog(String message) {
        progress=new ProgressDialog(MainActivity.this);
        progress.setTitle(message);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.show();
    }

    @Override
    public void stopProgressDialog() {
//        if(progress==null){
            SystemClock.sleep(1000);
//        }
        progress.cancel();
        progress=null;
    }

    @Override
    public void makeToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }
}
