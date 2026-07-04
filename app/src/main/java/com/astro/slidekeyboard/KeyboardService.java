package com.astro.slidekeyboard;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;

public class KeyboardService {

    private Context context;
    private LatinKeyboard mQwertyKeyboard;
    private LatinKeyboard mLandscapeQwertyKeyboard;

    private LatinKeyboard mSymbolsKeyboard;
    private LatinKeyboard mEmojiKeyboard;

    public KeyboardService(Context context) {
        this.context = context;
        mQwertyKeyboard = new LatinKeyboard(context, R.xml.qwerty);
        mSymbolsKeyboard = new LatinKeyboard(context, R.xml.symbols);
        mEmojiKeyboard = new LatinKeyboard(context, R.xml.emoji);

        mLandscapeQwertyKeyboard = new LatinKeyboard(context, R.xml.qwerty_landscape);
    }

    public LatinKeyboard getQwertyKeyboard() {
        return isLandscape() ? this.mLandscapeQwertyKeyboard : this.mQwertyKeyboard;
    }

    public LatinKeyboard getEmojiKeyboard() {
        return this.mEmojiKeyboard;
    }

    public LatinKeyboard getSymbolKeyboard() {
        return this.mSymbolsKeyboard;
    }

    private boolean isLandscape() {
        return this.context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }
}
