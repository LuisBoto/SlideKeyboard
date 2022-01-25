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
    // TODO: Move this into android.inputmethodservice.Keyboard
    static final int KEYCODE_LANGUAGE_SWITCH = -101;
    private float pressedOnX, pressedOnY;
    private int swipedDirection;
    private int lastDirection;

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
                    getDisplayWidth(), getDisplayHeight());
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

    private double getDisplayWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    private double getDisplayHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    public int getSwipedDirection() {
        return this.swipedDirection;
    }

    private Keys getKey(float x, float y){
        for(Keyboard.Key k:getKeyboard().getKeys())
            if((x>=k.x && x<=k.x+ k.width) && (y>=k.y && y<=k.y + k.height))
                if (k.codes[0]<Keys.values().length)
                    return Keys.values()[k.codes[0]];
        return Keys.SPECIAL_RIGHT;
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
        paint.setTextSize(28);
        paint.setColor(Color.LTGRAY);

        List<Key> keys = getKeyboard().getKeys();
        for(Key key: keys) {
            if(key.label != null) {
                if (key.label.equals("q")) {
                    canvas.drawText("1", key.x + (key.width - 25), key.y + 40, paint);
                } else if (key.label.equals("w")) {
                    canvas.drawText("2", key.x + (key.width - 25), key.y + 40, paint);
                } else if (key.label.equals("e")) {
                    canvas.drawText("3", key.x + (key.width - 25), key.y + 40, paint);
                } else if (key.label.equals("r")) {
                    canvas.drawText("4", key.x + (key.width - 25), key.y + 40, paint);
                } else if (key.label.equals("t")) {
                    canvas.drawText("5", key.x + (key.width - 25), key.y + 40, paint);
                }
            }

        }
    }

}
