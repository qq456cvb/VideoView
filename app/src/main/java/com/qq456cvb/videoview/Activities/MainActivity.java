package com.qq456cvb.videoview.Activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.qq456cvb.videoview.Application.GlobalApp;
import com.qq456cvb.videoview.R;
import com.qq456cvb.videoview.Subviews.CommentAndChannelListFragment;
import com.qq456cvb.videoview.Subviews.MiddleFragment;
import com.qq456cvb.videoview.Subviews.ProfileFragment;
import com.qq456cvb.videoview.Subviews.RightFragment;
import com.qq456cvb.videoview.Utils.Channel;

import jp.wasabeef.sample.CommentPanelRetSwitcher;
import jp.wasabeef.sample.commentPanelFragment;

public class MainActivity extends FragmentActivity implements ProfileFragment.OnProfileListener, CommentPanelRetSwitcher{

    public final static String TAG = "MainActivity";
    public final static int CHANNEL = 1;
    public final static int MYCOMMENT = 2;
    public final static int EDITCOMMENT = 3;

    private RightFragment mRightFragment = new RightFragment();
    private MiddleFragment mMiddleFragment = new MiddleFragment();
    private ProfileFragment mProfileFragment = new ProfileFragment();
    private CommentAndChannelListFragment comchanFragment = new CommentAndChannelListFragment();
    private commentPanelFragment commentPanelFragment = new commentPanelFragment();
    private LinearLayout mWatchButton;
    private LinearLayout mCommentButton;
    private LinearLayout mProfileButton;
    private LinearLayout mContentMain;
    private LinearLayout mContentMiddle;
    private FrameLayout mContentRight;

    public static Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        setHandler();
        findViews();
        bindOnClickListeners();

        // default layout
        setDefaultFragment();
    }

    public void findViews() {
        mWatchButton = (LinearLayout)findViewById(R.id.watchButton);
        mCommentButton = (LinearLayout)findViewById(R.id.commentButton);
        mProfileButton = (LinearLayout)findViewById(R.id.profileButton);
        mContentMain = (LinearLayout)findViewById(R.id.content_main);
        mContentMiddle = (LinearLayout)findViewById(R.id.content_middle);
        mContentRight = (FrameLayout)findViewById(R.id.content_right);
    }

    public void bindOnClickListeners() {
        mWatchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearFragments();
                mContentMiddle.setVisibility(View.VISIBLE);
                mContentRight.setVisibility(View.VISIBLE);
                FragmentManager fm = getFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                transaction.show(mMiddleFragment);
                transaction.show(mRightFragment);
                transaction.commit();
            }
        });
        mCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearFragments();
                mContentMiddle.setVisibility(View.VISIBLE);
                mContentRight.setVisibility(View.VISIBLE);
                FragmentManager fm = getFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                transaction.show(mMiddleFragment);
                transaction.show(comchanFragment);
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
        transaction.add(R.id.content_middle, mMiddleFragment);
        transaction.add(R.id.content_right, mRightFragment);
        transaction.add(R.id.content_main, mProfileFragment);
        transaction.add(R.id.content_right,commentPanelFragment);
        transaction.add(R.id.content_right,comchanFragment);
        transaction.hide(mProfileFragment);
        transaction.hide(mMiddleFragment);
        transaction.hide(mRightFragment);
        transaction.hide(commentPanelFragment);
        transaction.hide(comchanFragment);
        transaction.show(mMiddleFragment);
        transaction.show(mRightFragment);
        transaction.commit();
    }

    public void clearFragments()
    {
        mProfileFragment.clearFragments();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.hide(mMiddleFragment);
        transaction.hide(mRightFragment);
        transaction.hide(comchanFragment);
        transaction.hide(commentPanelFragment);
        transaction.hide(mProfileFragment);
        transaction.commit();

        // hide all the fragment container
        mContentMiddle.setVisibility(View.GONE);
        mContentRight.setVisibility(View.GONE);
    }

    public void setHandler() {
        handler = new Handler() {
            public void handleMessage(Message message) {
                switch (message.what) {
                    case CHANNEL: {
                        String type = message.getData().getString("type");
                        if (type.equals("list")) {
                            mRightFragment.changeCategory(message.getData().getString("value"));
                        }
                        else if (type.equals("play")) {
                            mMiddleFragment.changeSrc(message.getData().getString("value"));
                            GlobalApp.currentChannel = (Channel)message.obj;
                        }
                        break;
                    }
                    case MYCOMMENT:{
                        clearFragments();
                        FragmentManager fm = getFragmentManager();
                        FragmentTransaction transaction = fm.beginTransaction();
                        transaction.show(mProfileFragment.profileCommentFragment);
                        transaction.commit();
                        break;
                    }
                    case EDITCOMMENT:{
                        clearFragments();
                        mContentMiddle.setVisibility(View.VISIBLE);
                        mContentRight.setVisibility(View.VISIBLE);
                        FragmentManager fm = getFragmentManager();
                        FragmentTransaction transaction = fm.beginTransaction();
                        transaction.show(mMiddleFragment);
                        transaction.show(commentPanelFragment);
                        transaction.commit();
                        break;
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
}
