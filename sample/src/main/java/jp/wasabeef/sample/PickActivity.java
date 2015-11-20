package jp.wasabeef.sample;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by qq456cvb on 11/20/15.
 */
public class PickActivity extends Activity{

    private ListView pickList = null;
    private ArrayList<Uri> videos = new ArrayList<>();
    private Button cancelBtn;
    private class PickAdapter extends BaseAdapter {

        public int getCount() {
            // TODO Auto-generated method stub
            return videos.size();
        }

        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return videos.get(position);
        }

        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            ViewHolder holder = new ViewHolder();
            if (convertView == null) {
                convertView = PickActivity.this.getLayoutInflater().inflate(R.layout.pick_item, null);
                holder.textView = (TextView) convertView
                        .findViewById(R.id.video_path_txt);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.textView.setText(videos.get(position).getPath().substring(videos.get(position).getPath().lastIndexOf("/")+1));
            holder.textView.setOnClickListener(new View.OnClickListener() {// 添加按钮

                public void onClick(View v) {
                    Intent data = new Intent();
                    data.setData(videos.get(position));
                    setResult(RESULT_OK, data);
                    finish();
                }
            });

            return convertView;
        }

        public final class ViewHolder {
            public TextView textView;
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_video);

        getLocalVideos();

        cancelBtn = (Button) findViewById(R.id.pick_cancel_btn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        pickList = (ListView) findViewById(R.id.file_pick_listview);
        pickList.setAdapter(new PickAdapter());
    }

    private void getLocalVideos() {
        videos.clear();
        File sdDir = null, videoPath = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(android.os.Environment.MEDIA_MOUNTED); //判断sd卡是否存在
        if (sdCardExist)
        {
            sdDir = Environment.getExternalStorageDirectory();//获取跟目录
            videoPath = new File(sdDir.toString() + "/stpy/video");
            if (!videoPath.exists()) {
                videoPath.mkdirs();
            }
            File[] files = videoPath.listFiles();
            if (files.length > 0)
            {
                for (File file : files)
                {
                    if (file.isDirectory())
                    {
                        //TODO
                    }
                    else {
                        //判断是文件，则进行文件名判断
                        try {
                            if (file.getName().contains("mp4")||file.getName().contains("mp4".toUpperCase()))
                            {
                                videos.add(Uri.parse(file.getAbsolutePath()));
                            }
                        } catch(Exception e) {
                            // TODO
                        }
                    }
                }
            }
        } else {
            //TODO
        }
    }
}
