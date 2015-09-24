package com.qq456cvb.videoview.CustomWidgets;

/**
 * Created by qq456cvb on 9/14/15.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.qq456cvb.videoview.R;
import com.qq456cvb.videoview.Subviews.DownloadAndChannelListFragment;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * 日期时间选择控件 使用方法： private EditText inputDate;//需要设置的日期时间文本编辑框 private String
 * initDateTime=2012年9月3日 14:44,//初始日期时间值 在点击事件中使用：
 * inputDate.setOnClickListener(new OnClickListener() {
 *
 * @Override public void onClick(View v) { DateTimePickDialogUtil
 *           dateTimePicKDialog=new
 *           DateTimePickDialogUtil(SinvestigateActivity.this,initDateTime);
 *           dateTimePicKDialog.dateTimePicKDialog(inputDate);
 *
 *           } });
 *
 * @author
 */
public class DatePickDialog implements DatePicker.OnDateChangedListener {
    private DatePicker datePicker;
    private DownloadAndChannelListFragment downloadAndChannelListFragment;
    private ArrayList<String> dates = new ArrayList<>();
    private ListView listView;
    private AlertDialog ad;
    private String dateTime;
    private String initDateTime;
    private Activity activity;

    private class DateBaseAdapter extends BaseAdapter {

        private LayoutInflater mInflater;

        public DateBaseAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        public int getCount() {
            // TODO Auto-generated method stub
            return dates.size();
        }

        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return dates.get(position);
        }

        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            ViewHolder holder = new ViewHolder();
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.date_item, null);
                holder.btn = (Button) convertView
                        .findViewById(R.id.btn_delete);
                holder.textView = (TextView) convertView
                        .findViewById(R.id.text_date);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.textView.setText(dates.get(position));
            holder.btn.setOnClickListener(new View.OnClickListener() {// 添加按钮

                public void onClick(View v) {
                    dates.remove(dates.get(position));// 删除按钮
                    notifyDataSetChanged();
                }
            });

            return convertView;
        }

        public final class ViewHolder {
            public TextView textView;
            public Button btn;
        }
    }


    /**
     * 日期时间弹出选择框构造函数
     *
     * @param activity
     *            ：调用的父activity
     * @param initDateTime
     *            初始日期时间值，作为弹出窗口的标题和日期时间初始值
     */
    public DatePickDialog(Activity activity, String initDateTime) {
        this.activity = activity;
        this.initDateTime = initDateTime;

    }

    public void init(DatePicker datePicker) {
        Calendar calendar = Calendar.getInstance();
        if (!(null == initDateTime || "".equals(initDateTime))) {
            calendar = this.getCalendarByInitData(initDateTime);
        } else {
            initDateTime = calendar.get(Calendar.YEAR) + "-"
                    + calendar.get(Calendar.MONTH) + "-"
                    + calendar.get(Calendar.DAY_OF_MONTH);
        }

        datePicker.init(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH), this);
    }

    /**
     * 弹出日期时间选择框方法
     *
     * @param inputDate
     *            :为需要设置的日期时间文本编辑框
     * @return
     */
    public AlertDialog dateTimePickDialog(DownloadAndChannelListFragment fragment) {
        downloadAndChannelListFragment = fragment;
        LinearLayout dateTimeLayout = (LinearLayout) activity
                .getLayoutInflater().inflate(R.layout.multi_datetime, null);
        datePicker = (DatePicker) dateTimeLayout.findViewById(R.id.datePicker2);
        listView = (ListView) dateTimeLayout.findViewById(R.id.date_choose);
        final DateBaseAdapter dateBaseAdapter = new DateBaseAdapter(fragment.getActivity());
        listView.setAdapter(dateBaseAdapter);
        init(datePicker);


        ad = new AlertDialog.Builder(activity)
                .setTitle(initDateTime)
                .setView(dateTimeLayout)
                .setPositiveButton("下载", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        try
                        {
                            Field field = dialog.getClass().getSuperclass().getDeclaredField( "mShowing" );
                            field.setAccessible( true );
                            field.set(dialog,
                                    true); // false - 使之不能关闭(此为机关所在，其它语句相同)
                            String result = "";
                            int i = 0;
                            for (i = 0; i < dates.size() - 1; i ++) {
                                result += dates.get(i) + ";";
                            }
                            result += dates.get(i);
                            downloadAndChannelListFragment.chooseDates = result;
                            downloadAndChannelListFragment.manyDownload();
                        }
                        catch ( Exception e )
                        {
                            e.printStackTrace();
                        }
                    }
                })
                .setNeutralButton("添加日期", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        try
                        {
                            Field field = dialog.getClass().getSuperclass().getDeclaredField( "mShowing" );
                            field.setAccessible( true );
                            field.set( dialog,
                                    false ); // false - 使之不能关闭(此为机关所在，其它语句相同)
                        }
                        catch ( Exception e )
                        {
                            e.printStackTrace();
                        }
                        onDateChanged(null, 0, 0, 0);
                        dates.add(dateTime);
                        dateBaseAdapter.notifyDataSetChanged();
                        onDateChanged(null, 0, 0, 0);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        try
                        {
                            Field field = dialog.getClass().getSuperclass().getDeclaredField( "mShowing" );
                            field.setAccessible( true );
                            field.set( dialog,
                                    true ); // false - 使之不能关闭(此为机关所在，其它语句相同)
                        }
                        catch ( Exception e )
                        {
                            e.printStackTrace();
                        }
                    }
                }).show();

        onDateChanged(null, 0, 0, 0);
        return ad;
    }

    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
        // 获得日历实例
        Calendar calendar = Calendar.getInstance();

        calendar.set(datePicker.getYear(), datePicker.getMonth(),
                datePicker.getDayOfMonth());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        dateTime = sdf.format(calendar.getTime());
        ad.setTitle(dateTime);
    }

    /**
     * 实现将初始日期时间2012年07月02日 16:45 拆分成年 月 日 时 分 秒,并赋值给calendar
     *
     * @param initDateTime
     *            初始日期时间值 字符串型
     * @return Calendar
     */
    private Calendar getCalendarByInitData(String initDateTime) {
        Calendar calendar = Calendar.getInstance();

        // 将初始日期时间2012年07月02日 16:45 拆分成年 月 日 时 分 秒
        String yearStr = initDateTime.substring(0, initDateTime.indexOf("-")); // 年份
        String monthStr = initDateTime.substring(initDateTime.indexOf("-") + 1, initDateTime.lastIndexOf("-")); // 月
        String dayStr = initDateTime.substring(initDateTime.lastIndexOf("-") + 1);

        int currentYear = Integer.valueOf(yearStr.trim());
        int currentMonth = Integer.valueOf(monthStr.trim()) - 1;
        int currentDay = Integer.valueOf(dayStr.trim());

        calendar.set(currentYear, currentMonth, currentDay);
        return calendar;
    }

}