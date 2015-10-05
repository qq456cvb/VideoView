package com.qq456cvb.videoview.Adapters.Profile;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.qq456cvb.videoview.R;
import com.qq456cvb.videoview.Subviews.Profile.ProfileVideoFragment;

import java.util.List;

/**
 * Created by qq456cvb on 9/26/15.
 */
public class ProfileVideoAdapter extends ArrayAdapter<String> {

    private ProfileVideoFragment profileVideoFragment;

    private LayoutInflater mInflater;
    public ProfileVideoAdapter(ProfileVideoFragment profileVideoFragment, List<String> videos, ListView listView) {
        super(profileVideoFragment.getActivity(), 0, videos);
        this.profileVideoFragment = profileVideoFragment;
//        this.gridView = gridView1;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        Activity activity = (Activity) getContext();
        String url = getItem(position);
        // Load the image and set it on the ImageView
        url = url.replaceAll(" ", "%20");
        url = url.replaceAll("&amp;", "&");
        final String innerUrl = url;
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            // Inflate the views from XML
            LayoutInflater inflater = activity.getLayoutInflater();
            convertView = inflater.inflate(R.layout.profile_video_item, null);
            holder.textView = (TextView)convertView.findViewById(R.id.video_url);
            holder.btn = (Button)convertView.findViewById(R.id.video_delete);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.textView.setText(convertUrl(url));
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileVideoFragment.changeSrc(innerUrl);
            }
        });
        holder.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return convertView;
    }

    public final class ViewHolder {
        public TextView textView;
        public Button btn;
    }

    private String convertUrl(String url) {
        String result = url.substring(url.lastIndexOf("/") + 1);
        String year = result.substring(0, 4);
        String month = result.substring(4, 6);
        String day = result.substring(6, 8);
        String hour = result.substring(8, 10);
        String minute = result.substring(10, 12);
        String second = result.substring(12, 14);
        result = year+"-"+month+"-"+day+" "+hour+":"+minute+":"+second;
        return result;
    }
}