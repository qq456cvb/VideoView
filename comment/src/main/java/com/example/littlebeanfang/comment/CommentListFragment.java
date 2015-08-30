package com.example.littlebeanfang.comment;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.ListFragment;
import android.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by littlebeanfang on 2015/8/18.
 */
public class CommentListFragment extends ListFragment {
    private String TAG="CommentListFragment";
    private List<? extends Map<String,?>> dataList;
    private ListView cmList;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "--------onCreate");
//        adapter = new SimpleAdapter(getActivity(), getData(), R.layout.listitem, new String[]{"type",
//                "title","docname","relfile","date"}, new int[]{R.id.type,R.id.title,R.id.docname,
//                R.id.relfile,R.id.date
//        });
//        setListAdapter(adapter);
    }



    private List<? extends Map<String,?>> getData() {
        //TODO
        Log.d(TAG,"-------getData");
        ArrayList<HashMap<String,String>> list=new ArrayList<HashMap<String, String>>();
        HashMap<String, String> map1=new HashMap<String, String>();
        HashMap<String, String> map2=new HashMap<String, String>();
        HashMap<String, String> map3=new HashMap<String, String>();
        map1.put("type","type1");
        map1.put("title","title1");
        map1.put("docname","docname1");
        map1.put("relfile","relfile1");
        map1.put("date","date1");
        map2.put("type","type2");
        map2.put("title","title2");
        map2.put("docname","docname2");
        map2.put("relfile", "relfile2");
        map2.put("date","date2");
        map3.put("type","type3");
        map3.put("title","title3");
        map3.put("docname", "docname3");
        map3.put("relfile","relfile3");
        map3.put("date","date3");
        list.add(map1);
        list.add(map2);
        list.add(map3);
        dataList=list;
        return list;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "--------onCreateView");
        Log.d(TAG, "container:"+container);
        View view = inflater.inflate(R.layout.commentlist, container,false);
        cmList=(ListView) view.findViewById(android.R.id.list);
        Log.d(TAG,"cmList:"+cmList);
        dataList=getData();
        cmList.setAdapter(new CommentAdapter(this.getActivity()));
        return view;
    }
    class CommentAdapter extends BaseAdapter {

        private LayoutInflater mInflater;
        private Context fcontext;

        public CommentAdapter(Context context) {
            this.fcontext=context;
            this.mInflater = LayoutInflater.from(context);
        }
        @Override
        public int getCount() {
            return dataList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {

                holder=new ViewHolder();

                //可以理解为从vlist获取view  之后把view返回给ListView
                convertView = mInflater.inflate(R.layout.listitem, null);
                holder.edit = (Button)convertView.findViewById(R.id.cm_edit);
                holder.delete = (Button)convertView.findViewById(R.id.cm_delete);
                holder.upload = (Button)convertView.findViewById(R.id.cm_upload);
                holder.type = (TextView)convertView.findViewById(R.id.type);
                holder.title = (TextView)convertView.findViewById(R.id.title);
                holder.docname = (TextView)convertView.findViewById(R.id.docname);
                holder.relfile = (TextView)convertView.findViewById(R.id.relfile);
                holder.date = (TextView)convertView.findViewById(R.id.date);

                convertView.setTag(holder);
            }else {
                holder = (ViewHolder)convertView.getTag();
            }
//            Log.d(TAG,"holder:"+holder);
//            Log.d(TAG,"dataList:"+dataList);
//            Log.d(TAG,"holder.type:"+holder.type);
            holder.type.setText((String)dataList.get(position).get("type"));
            holder.title.setText((String)dataList.get(position).get("title"));
            holder.docname.setText((String)dataList.get(position).get("docname"));
            holder.relfile.setText((String)dataList.get(position).get("relfile"));
            holder.date.setText((String) dataList.get(position).get("date"));
            holder.edit.setTag(position);
            holder.delete.setTag(position);
            holder.upload.setTag(position);
            holder.edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO: upload change to database
                    Log.d(TAG, "EditClickPosition:" + position);
                    AlertDialog.Builder builder = new AlertDialog.Builder(fcontext);
                    View layout = mInflater.inflate(R.layout.editdialog,null);
                    Log.d(TAG, "layout" + layout);
                    builder.setView(layout);
//                    builder.setTitle("修改信息");
//                    final EditText tv_title = (EditText) layout.findViewById(R.id.edit_title);
//                    final EditText tv_content = (EditText) layout.findViewById(R.id.edit_content);
                    final HashMap<String, String> map = (HashMap<String, String>) dataList.get(position);
//                    tv_title.setText(map.get("title"));
//                    tv_content.setText(map.get("docname"));
                    builder.setPositiveButton("保存", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
//                            String title = tv_title.getText().toString();
//                            String content = tv_content.getText().toString();
//                            map.put("title", title);
//                            map.put("docname", content);
                            dialog.dismiss();
                            notifyDataSetChanged();
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    Dialog d=builder.create();
//                    d.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    d.show();
                }
            });
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO:upload change to database
                    Log.d(TAG,"DeleteClickPosition:"+position);
                    AlertDialog.Builder builder = new AlertDialog.Builder(fcontext);
                    builder.setMessage("确认删除吗？");
                    builder.setTitle("提示");builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dataList.remove(position);
                            notifyDataSetChanged();
                            dialog.dismiss();
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                }
            });
            holder.upload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO:upload change to database

                    Log.d(TAG,"UploadClickPostion:"+position);
                }
            });
            return convertView;
        }
    }
    public final class ViewHolder {
        public Button edit;
        public Button delete;
        public Button upload;
        public TextView type;
        public TextView title;
        public TextView docname;
        public TextView relfile;
        public TextView date;
    }
}
