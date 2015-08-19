package com.qq456cvb.videoview.CustomWidgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageButton;

/**
 * Created by qq456cvb on 8/17/15.
 */
public class ImageTextButton extends ImageButton{
    private String _text = "";
    private int _color = 0;
    private float _textsize = 0f;

    public ImageTextButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setText(String text){
        this._text = text;
    }

    public void setColor(int color){
        this._color = color;
    }

    public void setTextSize(float textsize){
        this._textsize = textsize;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(_color);
        paint.setTextSize(_textsize);
        canvas.drawText(_text, 10, 5, paint);
    }
}
