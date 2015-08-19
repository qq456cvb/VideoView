package com.qq456cvb.videoview.Activities;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.qq456cvb.videoview.R;
import com.qq456cvb.videoview.Subviews.CommentFragment;
import com.qq456cvb.videoview.Subviews.ListFragment;
import com.qq456cvb.videoview.Subviews.ProfileFragment;
import com.qq456cvb.videoview.Subviews.VideoFragment;

public class MainActivity extends Activity implements ProfileFragment.OnProfileListener{
    public final static String TAG = "MainActivity";

    private ListFragment mList = new ListFragment();
    private VideoFragment mVideo = new VideoFragment();
    private CommentFragment mComment = new CommentFragment();
    private ProfileFragment mProfileFragment = new ProfileFragment();
    private LinearLayout mWatchButton;
    private LinearLayout mProfileButton;
    private LinearLayout mContentRight;
    private FrameLayout mContentVideo;
    private FrameLayout mContentList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        findViews();
        bindOnClickListeners();

        // default layout
        setDefaultFragment();
    }

    public void findViews() {
        mWatchButton = (LinearLayout)findViewById(R.id.watchButton);
        mProfileButton = (LinearLayout)findViewById(R.id.profileButton);
        mContentRight = (LinearLayout)findViewById(R.id.content_right);
        mContentVideo = (FrameLayout)findViewById(R.id.video_fragment);
        mContentList = (FrameLayout)findViewById(R.id.list_fragment);
    }

    public void bindOnClickListeners() {
        mWatchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearFragments();
                mContentVideo.setVisibility(View.VISIBLE);
                mContentList.setVisibility(View.VISIBLE);
                FragmentManager fm = getFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                transaction.show(mVideo);
                transaction.show(mList);
                transaction.commit();
            }
        });
        mProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearFragments();
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
        // add all the fragments
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.add(R.id.video_fragment, mVideo);
        transaction.add(R.id.list_fragment, mList);
        transaction.add(R.id.content_right, mProfileFragment);
        transaction.hide(mProfileFragment);
        transaction.commit();
    }

    public void clearFragments()
    {
        mProfileFragment.clearFragments();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.hide(mVideo);
        transaction.hide(mList);
        transaction.hide(mProfileFragment);
        transaction.commit();

        // hide all the fragment container
        mContentVideo.setVisibility(View.GONE);
        mContentList.setVisibility(View.GONE);
    }
}
