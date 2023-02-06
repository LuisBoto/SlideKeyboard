package com.astro.slidekeyboard;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.inputmethodservice.Keyboard.Key;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import java.util.List;

public class LatinKeyboardView extends KeyboardView {

    private final Paint paint;
    private float pressedOnX, pressedOnY;
    private int swipedDirection, lastDirection;

    public LatinKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.paint = new Paint();
    }

    public LatinKeyboardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.paint = new Paint();
    }

    public boolean onTouchEvent(MotionEvent me) {
        int action = me.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            this.lastDirection = this.swipedDirection;
            this.pressedOnX = me.getX();
            this.pressedOnY = me.getY();
        }
        if (action == MotionEvent.ACTION_MOVE || action == MotionEvent.ACTION_UP) {
            this.swipedDirection = Keys.determineSwipedDirection(
                    this.pressedOnX, this.pressedOnY,
                    me.getX(), me.getY(),
                    getSwipeThreshold());
            if (action == MotionEvent.ACTION_MOVE) {
                if (this.swipedDirection != this.lastDirection) {
                    this.lastDirection = this.swipedDirection;
                    me.setLocation(this.pressedOnX, this.pressedOnY);
                } else
                    return true;
            }
        }
        return super.onTouchEvent(me);
    }

    private double getSwipeThreshold() {
        return Math.min(
                Resources.getSystem().getDisplayMetrics().heightPixels,
                Resources.getSystem().getDisplayMetrics().widthPixels)*0.02;
    }

    public int getSwipedDirection() {
        return this.swipedDirection;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        this.paint.setTextAlign(Paint.Align.CENTER);
        this.paint.setTextSize(65);
        this.paint.setColor(Color.WHITE);

        Paint bgPaint = new Paint();
        bgPaint.setTextAlign(Paint.Align.CENTER);
        bgPaint.setTextSize(100);
        bgPaint.setTypeface(Typeface.DEFAULT_BOLD);
        bgPaint.setColor(Color.rgb(180, 175, 69));

        List<Key> keys = getKeyboard().getKeys();
        float centerX, centerY;
        for(Key key: keys) {
            if (Keys.isKeyCodeWithinMultikeys(key.codes[0])) {
                Keys actualKey = Keys.getKeyForCode(key.codes[0]).setShifted(isShifted());
                centerX = key.x + key.width/2F;
                centerY = key.y + key.height/2F;
                canvas.drawText(actualKey.getBackSymbol(), centerX, centerY + (key.height/7F), bgPaint);

                canvas.drawText(actualKey.getNorth(), centerX, centerY - (key.height/4F), this.paint);

                centerY = centerY + key.height/8F;
                canvas.drawText(actualKey.getWest(), centerX - (key.width/4F), centerY, this.paint);
                canvas.drawText(actualKey.getEast(), centerX + (key.width/4F), centerY, this.paint);

                centerY = centerY + key.height/16F;
                canvas.drawText(actualKey.getSouth(), centerX, centerY + (key.height/4F), this.paint);
            }
        }
    }

}
