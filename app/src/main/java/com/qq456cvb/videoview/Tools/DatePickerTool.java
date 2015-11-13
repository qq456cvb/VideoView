package com.qq456cvb.videoview.Tools;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.reflect.Field;

/**
 * Created by qq456cvb on 10/31/15.
 */
public class DatePickerTool {

        static Typeface typeface;
        public static void setFont(Activity activity, DatePicker datepicker) {
            if (android.os.Build.VERSION.SDK_INT >= 20)
                initLollipop(activity, datepicker);
            else if (android.os.Build.VERSION.SDK_INT >= 14)
                initKitkat(activity, datepicker);
        }

        private static void initLollipop(Activity activity, DatePicker datepicker) {

            try {

                Field delegateField = datepicker.getClass().getDeclaredField(
                        "mDelegate");
                delegateField.setAccessible(true);
                Object delegate = new Object();
                delegate = delegateField.get(datepicker);

                Field field = delegate.getClass().getDeclaredField("mDaySpinner");
                setFont(activity, delegate, field);
                field = delegate.getClass().getDeclaredField("mMonthSpinner");
                setFont(activity, delegate, field);
                field = delegate.getClass().getDeclaredField("mYearSpinner");
                setFont(activity, delegate, field);
                setFontEditText(activity, delegate,
                        delegate.getClass().getDeclaredField("mDaySpinnerInput"));
                setFontEditText(activity, delegate,
                        delegate.getClass().getDeclaredField("mMonthSpinnerInput"));
                setFontEditText(activity, delegate,
                        delegate.getClass().getDeclaredField("mYearSpinnerInput"));

            } catch (SecurityException e) {
                Log.d("ERROR", e.getMessage());
            } catch (IllegalArgumentException e) {
                Log.d("ERROR", e.getMessage());
            } catch (IllegalAccessException e) {
                Log.d("ERROR", e.getMessage());
            } catch (Exception e) {
                Log.d("ERROR", e.getMessage());
            }
        }

        private static void initKitkat(Activity activity, DatePicker datepicker) {
            try {
                Field field = datepicker.getClass().getDeclaredField("mDaySpinner");
                setFont(activity, datepicker, field);
                field = datepicker.getClass().getDeclaredField("mMonthSpinner");
                setFont(activity, datepicker, field);
                field = datepicker.getClass().getDeclaredField("mYearSpinner");
                setFont(activity, datepicker, field);
                setFontEditText(activity, datepicker,
                        datepicker.getClass().getDeclaredField("mDaySpinnerInput"));
                setFontEditText(activity, datepicker,
                        datepicker.getClass()
                                .getDeclaredField("mMonthSpinnerInput"));
                setFontEditText(activity, datepicker,
                        datepicker.getClass().getDeclaredField("mYearSpinnerInput"));

            } catch (SecurityException e) {
                Log.d("ERROR", e.getMessage());
            } catch (IllegalArgumentException e) {
                Log.d("ERROR", e.getMessage());
            } catch (IllegalAccessException e) {
                Log.d("ERROR", e.getMessage());
            } catch (Exception e) {
                Log.d("ERROR", e.getMessage());
            }
        }

        private static void setFont(Activity activity, Object datepicker, Field field)
                throws Exception {
            field.setAccessible(true);
            Object yearPicker = new Object();
            yearPicker = field.get(datepicker);
            ((View) yearPicker).setVisibility(View.VISIBLE);
            ((View) yearPicker).setPadding(-2, 0, -2, 0);

            View childpicker;
            childpicker = (View) activity.findViewById(Resources.getSystem().getIdentifier(
                    "month", "id", "android"));
            Field field1 = childpicker.getClass().getDeclaredField("mInputText");
            field1.setAccessible(true);
            Object edittext = new Object();
            edittext = field1.get(yearPicker);


            Field field2 = childpicker.getClass().getDeclaredField(
                    "mSelectorWheelPaint");
            field2.setAccessible(true);
            Object paint = new Object();
            paint = field2.get(yearPicker);
            if(typeface== null)
                typeface = Typeface.createFromAsset(activity.getAssets(), "customfont.ttf");
            ((Paint) paint).setTypeface(Typeface.create(typeface, Typeface.BOLD));
            ((Paint) paint).setTextSize(30);
            ((Paint) paint).setColor(Color.RED);

            ((TextView) edittext).setTypeface(typeface, Typeface.BOLD);
            ((TextView) edittext).setTextColor(Color.RED);
            ((TextView) edittext).setTextSize(30);
        }

        private static void setFontEditText(Activity activity,Object datePicker, Field field)
                throws Exception {
            field.setAccessible(true);
            Object paint = new Object();
            paint = field.get(datePicker);
            if(typeface== null)
                typeface = Typeface.createFromAsset(activity.getAssets(), "customfont.ttf");
            ((EditText) paint).setTypeface(typeface, Typeface.BOLD);
            ((EditText) paint).setTextColor(Color.RED);
            ((EditText) paint).setTextSize(30);
        }


}
