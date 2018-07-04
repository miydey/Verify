package com.chuangber.verify.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ProgressBar;

/**
 * Created by Administrator on 2018/3/20.
 */

public class MyProgress extends ProgressBar {

    Paint mPaint;
    String text;

    public MyProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MyProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyProgress(Context context) {
        super(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.rgb(255,255,255));
        mPaint.setTextSize(25f);

    }

    public void setText(String text){
        if (text!=null){
            this.text = text;
            invalidate();
        }
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Rect rect = new Rect();
        if (this.text!=null ){
            mPaint.getTextBounds(this.text, 0, this.text.length(), rect);
            int y = (getHeight() /2) - rect.centerY(); //最低点在进度条的中间偏上方
            canvas.drawText(this.text, 15, y, this.mPaint);//在进度条上画上自定义文本
        }
    }

    @Override
    public synchronized void setProgress(int progress) {
        super.setProgress(progress);
    }
}
