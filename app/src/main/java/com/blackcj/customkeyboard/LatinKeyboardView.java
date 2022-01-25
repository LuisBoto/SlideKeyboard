/*
 * Copyright (C) 2008-2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.blackcj.customkeyboard;

import static java.security.AccessController.getContext;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.Keyboard.Key;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodSubtype;

import java.util.List;

public class LatinKeyboardView extends KeyboardView {

    static final int KEYCODE_OPTIONS = -100;
    private float pressedOnX, pressedOnY;
    private int swipedDirection, lastDirection;

    public LatinKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LatinKeyboardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
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
    protected boolean onLongPress(Key key) {
        if (key.codes[0] == Keyboard.KEYCODE_CANCEL) {
            getOnKeyboardActionListener().onKey(KEYCODE_OPTIONS, null);
            return true;
        }
        return super.onLongPress(key);
    }

    void setSubtypeOnSpaceKey(final InputMethodSubtype subtype) {
        final LatinKeyboard keyboard = (LatinKeyboard)getKeyboard();
        //keyboard.setSpaceIcon(getResources().getDrawable(subtype.getIconResId()));
        invalidateAllKeys();
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint paint = new Paint();
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(80);
        paint.setColor(Color.WHITE);

        List<Key> keys = getKeyboard().getKeys();
        int centerX, centerY;
        for(Key key: keys) {
            if (key.codes[0] >= Keys.KEYCODE_START) { // Draw multi character key
                centerX = key.x + key.width/2;
                centerY = key.y + key.height/2;
                Keys actualKey = Keys.getKeyForCode(key.codes[0]).setShifted(isShifted());
                canvas.drawText(String.valueOf(actualKey.getNorth()), centerX, centerY - (key.height/4), paint);
                centerY = centerY + key.height/8;
                canvas.drawText(String.valueOf(actualKey.getWest()), centerX - (key.width/3), centerY, paint);
                canvas.drawText(String.valueOf(actualKey.getEast()), centerX + (key.width/3), centerY, paint);
                centerY = centerY + key.height/16;
                canvas.drawText(String.valueOf(actualKey.getSouth()), centerX, centerY + (key.height/4), paint);
                //canvas.drawText(String.valueOf(actualKey.getBackSymbol()), centerX, centerY, paint);
            }
        }
    }

}
