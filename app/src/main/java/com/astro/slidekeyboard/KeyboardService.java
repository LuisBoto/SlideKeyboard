package com.astro.slidekeyboard;
import android.content.Context;
import android.content.res.Configuration;

import java.util.Map;

public class KeyboardService {

    private Context context;
    private Map<Keyboards, LatinKeyboard> portraitKeyboards;
    private Map<Keyboards, LatinKeyboard> landscapeKeyboards;

    public KeyboardService(Context context) {
        this.context = context;
        var mQwertyKeyboard = new LatinKeyboard(context, R.xml.qwerty);
        var mSymbolsKeyboard = new LatinKeyboard(context, R.xml.symbols);
        var mEmojiKeyboard = new LatinKeyboard(context, R.xml.emoji);

        var mLandscapeQwertyKeyboard = new LatinKeyboard(context, R.xml.qwerty_landscape);
        var mLandscapeSymbolsKeyboard = new LatinKeyboard(context, R.xml.symbols_landscape);
        var mLandscapeEmojiKeyboard = new LatinKeyboard(context, R.xml.emoji_landscape);

        portraitKeyboards = Map.of(
                Keyboards.QWERTY, mQwertyKeyboard,
                Keyboards.SYMBOL, mSymbolsKeyboard,
                Keyboards.EMOJI, mEmojiKeyboard);

        landscapeKeyboards = Map.of(
                Keyboards.QWERTY, mLandscapeQwertyKeyboard,
                Keyboards.SYMBOL, mLandscapeSymbolsKeyboard,
                Keyboards.EMOJI, mLandscapeEmojiKeyboard);
    }

    public LatinKeyboard getQwertyKeyboard() {
        return this.getKeyboards().get(Keyboards.QWERTY);
    }

    public LatinKeyboard getEmojiKeyboard() {
        return this.getKeyboards().get(Keyboards.EMOJI);
    }

    public LatinKeyboard getSymbolKeyboard() {
        return this.getKeyboards().get(Keyboards.SYMBOL);
    }

    private Map<Keyboards, LatinKeyboard> getKeyboards() {
        return this.isLandscape() ? this.landscapeKeyboards : this.portraitKeyboards;
    }

    private boolean isLandscape() {
        return this.context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    private enum Keyboards {
        QWERTY,
        SYMBOL,
        EMOJI;
    }
}
