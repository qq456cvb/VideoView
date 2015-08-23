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

import com.example.littlebeanfang.comment.CommentListFragment;
import com.qq456cvb.videoview.R;

/**
 * Created by qq456cvb on 8/19/15.
 */
public class ProfileFragment extends Fragment {

    public interface OnProfileListener {
        void clearFragments();
    }

    private ImageView profileVideoButton;
    private ImageView profileReviewButton;
    private ImageView profileConfigButton;
    private ImageView profilePictureButton;
    private OnProfileListener onProfileListener;
//    private ProfileReviewFragment profileReviewFragment;
    private CommentListFragment profileReviewFragment;
    private ProfileImageFragment profileImageFragment;
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
        profileReviewFragment = new CommentListFragment();
        profileImageFragment = new ProfileImageFragment();
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
        transaction.add(R.id.content_right, profileReviewFragment);
        transaction.add(R.id.content_right, profileImageFragment);
        transaction.hide(profileReviewFragment);
        transaction.hide(profileImageFragment);
        transaction.commit();
    }

    private void findViews() {
        profileVideoButton = (ImageView)mView.findViewById(R.id.profile_video_img);
        profileReviewButton = (ImageView)mView.findViewById(R.id.profile_article_img);
        profileConfigButton = (ImageView)mView.findViewById(R.id.profile_config_img);
        profilePictureButton = (ImageView)mView.findViewById(R.id.profile_picture_img);
    }

    private void bindOnClickListeners() {
        profileVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        profileReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onProfileListener.clearFragments();
                clearFragments();
                FragmentManager fm = getFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                transaction.show(profileReviewFragment);
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
                onProfileListener.clearFragments();
                clearFragments();
                FragmentManager fm = getFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                transaction.show(profileImageFragment);
                transaction.commit();
            }
        });
    }

    public void clearFragments() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.hide(profileReviewFragment);
        transaction.hide(profileImageFragment);
        transaction.commit();
    }
}