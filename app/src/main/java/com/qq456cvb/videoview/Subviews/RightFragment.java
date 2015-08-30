package com.qq456cvb.videoview.Subviews;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qq456cvb.videoview.R;

/**
 * Created by qq456cvb on 8/27/15.
 */
public class RightFragment extends Fragment {

    private RightChannelFragment rightChannelFragment = new RightChannelFragment();
    private View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.right_layout, container, false);
        try {
            setDefaultFragment();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    public void setDefaultFragment() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.add(R.id.right_content, rightChannelFragment);
        transaction.commit();
    }

    public void changeCategory(String category) {
        rightChannelFragment.changeCategory(category);
    }
}
