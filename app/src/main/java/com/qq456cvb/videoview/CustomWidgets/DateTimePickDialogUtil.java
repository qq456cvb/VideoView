package com.qq456cvb.videoview.CustomWidgets;

/**
 * Created by qq456cvb on 9/14/15.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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

    /**
     * 日期时间弹出选择框构造函数
     *
     * @param activity
     *            ：调用的父activity
     * @param initDateTime
     *            初始日期时间值，作为弹出窗口的标题和日期时间初始值
     */
    public DateTimePickDialogUtil(Activity activity, String initDateTime) {
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
    public AlertDialog dateTimePickDialog(final TextView inputDate) {
        LinearLayout dateTimeLayout = (LinearLayout) activity
                .getLayoutInflater().inflate(R.layout.common_datetime, null);
        datePicker = (DatePicker) dateTimeLayout.findViewById(R.id.datePicker);
        timePicker = (TimePicker) dateTimeLayout.findViewById(R.id.timePicker);
        init(datePicker, timePicker);
        timePicker.setIs24HourView(true);
        timePicker.setOnTimeChangedListener(this);

        ad = new AlertDialog.Builder(activity)
                .setTitle(initDateTime)
                .setView(dateTimeLayout)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        onDateChanged(null, 0, 0, 0);
                        inputDate.setText(dateTime);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        inputDate.setText(dateTime);
                    }
                }).show();

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

        int currentYear = Integer.valueOf(yearStr.trim()).intValue();
        int currentMonth = Integer.valueOf(monthStr.trim()).intValue() - 1;
        int currentDay = Integer.valueOf(dayStr.trim()).intValue();
        int currentHour = Integer.valueOf(hourStr.trim()).intValue();
        int currentMinute = Integer.valueOf(minuteStr.trim()).intValue();

        calendar.set(currentYear, currentMonth, currentDay, currentHour,
                currentMinute);
        return calendar;
    }

    /**
     * 截取子串
     *
     * @param srcStr
     *            源串
     * @param pattern
     *            匹配模式
     * @param indexOrLast
     * @param frontOrBack
     * @return
     */
    public static String spliteString(String srcStr, String pattern,
                                      String indexOrLast, String frontOrBack) {
        String result = "";
        int loc = -1;
        if (indexOrLast.equalsIgnoreCase("index")) {
            loc = srcStr.indexOf(pattern); // 取得字符串第一次出现的位置
        } else {
            loc = srcStr.lastIndexOf(pattern); // 最后一个匹配串的位置
        }
        if (frontOrBack.equalsIgnoreCase("front")) {
            if (loc != -1)
                result = srcStr.substring(0, loc); // 截取子串
        } else {
            if (loc != -1)
                result = srcStr.substring(loc + 1, srcStr.length()); // 截取子串
        }
        return result;
    }

}