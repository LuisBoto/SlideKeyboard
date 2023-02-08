package com.astro.slidekeyboard;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.view.inputmethod.EditorInfo;

import androidx.core.content.res.ResourcesCompat;

public class LatinKeyboard extends Keyboard {

    private Key mEnterKey;
    private Key mShiftKey;
    private Key mSpaceKey;

    public LatinKeyboard(Context context, int xmlLayoutResId) {
        super(context, xmlLayoutResId);
    }

    @Override
    protected Key createKeyFromXml(Resources res, Row parent, int x, int y, 
            XmlResourceParser parser) {
        Key key = new LatinKey(res, parent, x, y, parser);
        if (key.codes[0] == 10)
            mEnterKey = key;
        if (key.codes[0] == KEYCODE_SHIFT)
            mShiftKey = key;
        if (key.codes[0] == ' ')
            mSpaceKey = key;
        return key;
    }

    public void changeToggleShiftIcon(Resources res, boolean toggled) {
        mShiftKey.icon = toggled ?
                ResourcesCompat.getDrawable(res, R.drawable.sym_keyboard_shift_toggled, null)
                : ResourcesCompat.getDrawable(res, R.drawable.sym_keyboard_shift, null);
    }

    void setImeOptions(Resources res, int options) {
        if (mEnterKey == null)
            return;

        switch (options&(EditorInfo.IME_MASK_ACTION|EditorInfo.IME_FLAG_NO_ENTER_ACTION)) {
            case EditorInfo.IME_ACTION_GO:
                mEnterKey.iconPreview = null;
                mEnterKey.icon = null;
                mEnterKey.label = res.getText(R.string.label_go_key);
                break;
            case EditorInfo.IME_ACTION_NEXT:
                mEnterKey.iconPreview = null;
                mEnterKey.icon = null;
                mEnterKey.label = res.getText(R.string.label_next_key);
                break;
            case EditorInfo.IME_ACTION_SEARCH:
                mEnterKey.icon = ResourcesCompat.getDrawable(res, R.drawable.sym_keyboard_search, null);
                mEnterKey.label = null;
                break;
            case EditorInfo.IME_ACTION_SEND:
                mEnterKey.iconPreview = null;
                mEnterKey.icon = null;
                mEnterKey.label = res.getText(R.string.label_send_key);
                break;
            default:
                mEnterKey.icon = ResourcesCompat.getDrawable(res, R.drawable.sym_keyboard_return, null);
                mEnterKey.label = null;
                break;
        }
    }

    static class LatinKey extends Keyboard.Key {

        public LatinKey(Resources res, Keyboard.Row parent, int x, int y,
                XmlResourceParser parser) {
            super(res, parent, x, y, parser);
        }

        @Override
        public boolean isInside(int x, int y) {
            return super.isInside(x, codes[0] == KEYCODE_CANCEL ? y - 10 : y);
        }
    }

}
