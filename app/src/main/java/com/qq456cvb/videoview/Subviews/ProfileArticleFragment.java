package com.qq456cvb.videoview.Subviews;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qq456cvb.videoview.R;

/**
 * Created by qq456cvb on 8/19/15.
 */
public class ProfileArticleFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.profile_article, container, false);
    }
}