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
import android.widget.Toast;

import com.qq456cvb.videoview.R;
import com.qq456cvb.videoview.Subviews.ListFragment;
import com.qq456cvb.videoview.Subviews.ProfileFragment;
import com.qq456cvb.videoview.Subviews.VideoFragment;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MainActivity extends Activity implements ProfileFragment.OnProfileListener{
    public final static String driver = "oracle.jdbc.driver.OracleDriver";
    String serverName = "220.250.58.250";
    String portNumber = "1521";
    String db = "callsc";
    String url = "jdbc:oracle:thin:@" + serverName + ":" + portNumber + ":" + db;
    String user = "fjstpy";
    String password = "fjstpy";

    public final static String TAG = "MainActivity";

    private ListFragment mListFragment = new ListFragment();
    private VideoFragment mVideoFragment = new VideoFragment();
    private ProfileFragment mProfileFragment = new ProfileFragment();
    private LinearLayout mWatchButton;
    private LinearLayout mCommentButton;
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

        try {
            test();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // default layout
        setDefaultFragment();
    }

    public void findViews() {
        mWatchButton = (LinearLayout)findViewById(R.id.watchButton);
        mCommentButton = (LinearLayout)findViewById(R.id.commentButton);
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
                transaction.show(mVideoFragment);
                transaction.show(mListFragment);
                transaction.commit();
            }
        });
        mCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearFragments();
                FragmentManager fm = getFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                transaction.show(mProfileFragment.profileCommentFragment);
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
        transaction.add(R.id.video_fragment, mVideoFragment);
        transaction.add(R.id.list_fragment, mListFragment);
        transaction.add(R.id.content_right, mProfileFragment);
        transaction.hide(mProfileFragment);
        transaction.hide(mVideoFragment);
        transaction.hide(mListFragment);
        transaction.show(mVideoFragment);
        transaction.commit();
    }

    public void clearFragments()
    {
        mProfileFragment.clearFragments();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.hide(mVideoFragment);
        transaction.hide(mListFragment);
        transaction.hide(mProfileFragment);
        transaction.commit();

        // hide all the fragment container
        mContentVideo.setVisibility(View.GONE);
        mContentList.setVisibility(View.GONE);
    }

    public void test() throws ClassNotFoundException {
        Class.forName(driver);
        new Thread() {
            @Override
            public void run() {
                Connection con = null;
                try {
                    con = DriverManager.getConnection(url, user, password);
                    Statement stmt = con.createStatement();
                    String sql = "SELECT * FROM Tbl_User";
                    ResultSet rs = stmt.executeQuery(sql);

                    while (rs.next()) {
                        Toast.makeText(MainActivity.this, rs.getString("username"), Toast.LENGTH_SHORT).show();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

}
