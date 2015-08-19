package com.qq456cvb.videoview.Subviews;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.qq456cvb.videoview.R;

/**
 * Created by qq456cvb on 8/19/15.
 */
public class ProfileFragment extends Fragment {

    public interface OnProfileListener {
        void clearFragments();
    }

    private ImageView profileVideoButton;
    private ImageView profileArticleButton;
    private ImageView profileConfigButton;
    private ImageView profilePictureButton;
    private OnProfileListener onProfileListener;
    private ProfileArticleFragment profileArticleFragment;
    private View mView;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        onProfileListener = (OnProfileListener) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        mView = inflater.inflate(R.layout.profile, container, false);
        profileArticleFragment = new ProfileArticleFragment();
        findViews();
        bindOnClickListeners();
        setDefaultFragment();
        return mView;
    }

    private void setDefaultFragment()
    {
        // add all the fragments
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.add(R.id.content_right, profileArticleFragment);
        transaction.hide(profileArticleFragment);
        transaction.commit();
    }

    private void findViews() {
        profileVideoButton = (ImageView)mView.findViewById(R.id.profile_video_img);
        profileArticleButton = (ImageView)mView.findViewById(R.id.profile_article_img);
        profileConfigButton = (ImageView)mView.findViewById(R.id.profile_config_img);
        profilePictureButton = (ImageView)mView.findViewById(R.id.profile_picture_img);
    }

    private void bindOnClickListeners() {
        profileVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        profileArticleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onProfileListener.clearFragments();
                FragmentManager fm = getFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                transaction.show(profileArticleFragment);
                transaction.commit();
            }
        });

        profileConfigButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        profilePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public void clearFragments() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.hide(profileArticleFragment);
        transaction.commit();
    }
}