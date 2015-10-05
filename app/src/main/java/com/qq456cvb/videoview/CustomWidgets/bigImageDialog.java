package com.qq456cvb.videoview.CustomWidgets;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.qq456cvb.videoview.Activities.MainActivity;
import com.qq456cvb.videoview.R;
import com.qq456cvb.videoview.Subviews.Profile.ProfileImageFragment;

/**
 * Created by qq456cvb on 9/26/15.
 */
public class BigImageDialog extends DialogFragment{

    private View view;
    private NetworkImageView imageView;
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        FragmentManager fm = getActivity().getFragmentManager();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        if(view!=null){
            //!fix the bug: reload a dialog will cause a crash
            ViewGroup parent=(ViewGroup)view.getParent();
            if(parent!=null){
                parent.removeView(view);
            }
        }
        try{
            view=inflater.inflate(R.layout.large_image,null);
        }catch(InflateException e){
//            Log.d(TAG, "in fact exception is here, but don't worry, just ignore it");
        }

        imageView = (NetworkImageView)view.findViewById(R.id.big_image_view);
        ImageLoader imLoader = new ImageLoader(((MainActivity)getActivity()).mProfileFragment.profileImageFragment.mQueue, ProfileImageFragment.bitmapLruCache);
        imageView.setDefaultImageResId(R.drawable.my_loading);
        imageView.setErrorImageResId(R.drawable.error);
        imageView.setImageUrl(getArguments().getString("url"), imLoader);
        builder.setView(view)
                // Add action buttons
                .setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
        Dialog dialog=builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

}
