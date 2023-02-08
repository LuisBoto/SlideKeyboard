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
    private float pressedOnX, pressedOnY;
    private int swipedDirection, lastDirection;

    public LatinKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
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

        Paint paint = getNormalCharacterPaint();
        Paint bgPaint = getBackgroundCharacterPaint();
        List<Key> keys = getKeyboard().getKeys();
        float centerX, centerY;
        for(Key key: keys) {
            if (Keys.isKeyCodeWithinMultikeys(key.codes[0])) {
                Keys actualKey = Keys.getKeyForCode(key.codes[0]).setShifted(isShifted());
                centerX = key.x + key.width/2F;
                centerY = key.y + key.height/2F;
                canvas.drawText(actualKey.getBackSymbol(), centerX, centerY + (key.height/7F), bgPaint);
                canvas.drawText(actualKey.getNorth(), centerX, centerY - (key.height/4F), paint);

                centerY = centerY + key.height/8F;
                canvas.drawText(actualKey.getWest(), centerX - (key.width/4F), centerY, paint);
                canvas.drawText(actualKey.getEast(), centerX + (key.width/4F), centerY, paint);

                canvas.drawText(actualKey.getSouth(), centerX, centerY + (5*key.height/16F), paint);
            }
        }
    }

    private Paint getNormalCharacterPaint() {
        Paint paint = new Paint();
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(65);
        paint.setColor(Color.WHITE);
        return paint;
    }

    private Paint getBackgroundCharacterPaint() {
        Paint bgPaint = new Paint();
        bgPaint.setTextAlign(Paint.Align.CENTER);
        bgPaint.setTextSize(65);
        bgPaint.setTypeface(Typeface.DEFAULT_BOLD);
        bgPaint.setColor(Color.rgb(180, 175, 69));
        return bgPaint;
    }

}
