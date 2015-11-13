package com.qq456cvb.videoview.Adapters.Profile;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.qq456cvb.videoview.R;
import com.qq456cvb.videoview.Subviews.Profile.ProfileVideoFragment;
import com.qq456cvb.videoview.Utils.UserVideo;

import java.util.HashMap;
import java.util.List;

/**
 * Created by qq456cvb on 9/26/15.
 */
public class ProfileVideoAdapter extends ArrayAdapter<UserVideo> {

    private ProfileVideoFragment profileVideoFragment;
    private boolean online;
    public HashMap<Integer, CheckBox> checkBoxHashMap = new HashMap<>();

    private LayoutInflater mInflater;
    public ProfileVideoAdapter(ProfileVideoFragment profileVideoFragment, List<UserVideo> videos, ListView listView, boolean online) {
        super(profileVideoFragment.getActivity(), 0, videos);
        this.online = online;
        this.profileVideoFragment = profileVideoFragment;
//        this.gridView = gridView1;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        final Activity activity = (Activity) getContext();
        final UserVideo userVideo = getItem(position);
        String url = userVideo.url;
        // Load the image and set it on the ImageView
        if (online) {
            url = url.replaceAll(" ", "%20");
            url = url.replaceAll("&amp;", "&");
        }
        final String innerUrl = url;
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            // Inflate the views from XML
            LayoutInflater inflater = activity.getLayoutInflater();
            convertView = inflater.inflate(R.layout.profile_video_item, null);
            holder.textView = (TextView)convertView.findViewById(R.id.video_url);
            holder.delete = (CheckBox)convertView.findViewById(R.id.video_delete);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.textView.setText(convertUrl(url));
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(activity, "你点击了" + innerUrl, Toast.LENGTH_SHORT).show();
                profileVideoFragment.changeSrc(innerUrl, 0);
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.delete.isChecked()) {
                    profileVideoFragment.addDelete(userVideo);
                } else {
                    profileVideoFragment.popDelete(userVideo);
                }
            }
        });
//        if (online) {
        checkBoxHashMap.put(position, holder.delete);
//        } else {
//            holder.delete.setVisibility(View.GONE);
//        }
        return convertView;
    }

    public final class ViewHolder {
        public TextView textView;
        public CheckBox delete;
    }

    private String convertUrl(String url) {
        if (online) {
            String result = url.substring(url.lastIndexOf("/") + 1);
            String year = result.substring(0, 4);
            String month = result.substring(4, 6);
            String day = result.substring(6, 8);
            String hour = result.substring(8, 10);
            String minute = result.substring(10, 12);
            String second = result.substring(12, 14);
            result = year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second;
            return result;
        } else {
            String result = url.substring(url.indexOf("video")+6, url.lastIndexOf("."));
//            result = result.replaceAll("_", " ");
            return result;
        }
    }
}