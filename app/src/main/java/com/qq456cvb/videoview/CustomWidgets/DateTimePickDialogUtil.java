package com.qq456cvb.videoview.CustomWidgets;

/**
 * Created by qq456cvb on 9/14/15.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.qq456cvb.videoview.R;

import java.text.SimpleDateFormat;
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
public class DateTimePickDialogUtil implements DatePicker.OnDateChangedListener,
        TimePicker.OnTimeChangedListener {
    private DatePicker datePicker;
    private TimePicker timePicker;
    private AlertDialog ad;
    private String dateTime;
    private String initDateTime;
    private Activity activity;
    private OnDateSelectListener onDateSelectListener;
    public int tag = 0;

    public interface OnDateSelectListener {
        void OnDateComplete(String dateTime);
    }
    /**
     * 日期时间弹出选择框构造函数
     *
     * @param activity
     *            ：调用的父activity
     * @param initDateTime
     *            初始日期时间值，作为弹出窗口的标题和日期时间初始值
     */
    public DateTimePickDialogUtil(Activity activity, OnDateSelectListener listener, String initDateTime) {
        this.onDateSelectListener = listener;
        this.activity = activity;
        this.initDateTime = initDateTime;

    }

    public void init(DatePicker datePicker, TimePicker timePicker) {
        Calendar calendar = Calendar.getInstance();
        if (!(null == initDateTime || "".equals(initDateTime))) {
            calendar = this.getCalendarByInitData(initDateTime);
        } else {
            initDateTime = calendar.get(Calendar.YEAR) + "-"
                    + calendar.get(Calendar.MONTH) + "-"
                    + calendar.get(Calendar.DAY_OF_MONTH) + " "
                    + calendar.get(Calendar.HOUR_OF_DAY) + ":"
            + calendar.get(Calendar.MINUTE);
        }

        datePicker.init(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH), this);
        timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
        timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
    }

    /**
     * 弹出日期时间选择框方法
     *
     * @param inputDate
     *            :为需要设置的日期时间文本编辑框
     * @return
     */
    public AlertDialog dateTimePickDialog(final TextView inputDate, boolean showWarning) {
        LinearLayout dateTimeLayout = (LinearLayout) activity
                .getLayoutInflater().inflate(R.layout.common_datetime, null);
        datePicker = (DatePicker) dateTimeLayout.findViewById(R.id.datePicker);
        timePicker = (TimePicker) dateTimeLayout.findViewById(R.id.timePicker);
        TextView txtWarning = (TextView)dateTimeLayout.findViewById(R.id.txt_warning);
        if (!showWarning) {
            txtWarning.setVisibility(View.GONE);
        }
        init(datePicker, timePicker);
        timePicker.setIs24HourView(true);
        timePicker.setOnTimeChangedListener(this);

        ad = new AlertDialog.Builder(activity)
                .setTitle(initDateTime)
                .setView(dateTimeLayout)
                .show();
        Button btn_confirm = (Button)dateTimeLayout.findViewById(R.id.common_confirm);
        Button btn_cancel = (Button)dateTimeLayout.findViewById(R.id.common_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputDate.setText(dateTime);
                ad.dismiss();
            }
        });
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDateChanged(null, 0, 0, 0);
                if (tag == 0) {
                    onDateSelectListener.OnDateComplete(dateTime);
                }
                inputDate.setText(dateTime);
                ad.dismiss();
            }
        });
        onDateChanged(null, 0, 0, 0);
        return ad;
    }

    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        onDateChanged(null, 0, 0, 0);
    }

    public void onDateChanged(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
        // 获得日历实例
        Calendar calendar = Calendar.getInstance();

        calendar.set(datePicker.getYear(), datePicker.getMonth(),
                datePicker.getDayOfMonth(), timePicker.getCurrentHour(),
                timePicker.getCurrentMinute());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

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
        String dayStr = initDateTime.substring(initDateTime.lastIndexOf("-") + 1, initDateTime.lastIndexOf(" ")); // 日

        String hourStr = initDateTime.substring(initDateTime.lastIndexOf(" ") + 1, initDateTime.lastIndexOf(":")); // 时
        String minuteStr = initDateTime.substring(initDateTime.lastIndexOf(":") + 1); // 分

        int currentYear = Integer.valueOf(yearStr.trim());
        int currentMonth = Integer.valueOf(monthStr.trim()) - 1;
        int currentDay = Integer.valueOf(dayStr.trim());
        int currentHour = Integer.valueOf(hourStr.trim());
        int currentMinute = Integer.valueOf(minuteStr.trim());

        calendar.set(currentYear, currentMonth, currentDay, currentHour,
                currentMinute);
        return calendar;
    }

}