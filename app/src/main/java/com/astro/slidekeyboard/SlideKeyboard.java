package com.astro.slidekeyboard;

import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;

public class SlideKeyboard extends InputMethodService implements KeyboardView.OnKeyboardActionListener {

    private InputMethodManager mInputMethodManager;
    private LatinKeyboardView mInputView;
    
    private StringBuilder mComposing = new StringBuilder();
    private int mLastDisplayWidth;

    private LatinKeyboard mSymbolsKeyboard;
    private LatinKeyboard mQwertyKeyboard;
    private LatinKeyboard mCurKeyboard;
    private String mWordSeparators;

    @Override
    public void onCreate() {
        super.onCreate();
        mInputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        mWordSeparators = getResources().getString(R.string.word_separators);
    }
    
    /**
     * This is the point where you can do all of your UI initialization.  It
     * is called after creation and any configuration change.
     */
    @Override
    public void onInitializeInterface() {
        if (mQwertyKeyboard != null) {
            int displayWidth = getMaxWidth();
            if (displayWidth == mLastDisplayWidth) return;
            mLastDisplayWidth = displayWidth;
        }
        mQwertyKeyboard = new LatinKeyboard(this, R.xml.qwerty);
        mSymbolsKeyboard = new LatinKeyboard(this, R.xml.symbols);
    }
    
    /**
     * Called by the framework when your view for creating input needs to
     * be generated.  This will be called the first time your input method
     * is displayed, and every time it needs to be re-created such as due to
     * a configuration change.
     */
    @Override
    public View onCreateInputView() {
        mInputView = (LatinKeyboardView) getLayoutInflater().inflate(
                R.layout.input, null);
        mInputView.setOnKeyboardActionListener(this);
        mInputView.setPreviewEnabled(false);
        setLatinKeyboard(mQwertyKeyboard);
        return mInputView;
    }

    private void setLatinKeyboard(LatinKeyboard nextKeyboard) {
        mInputView.setKeyboard(nextKeyboard);
    }

    @Override
    public void onStartInput(EditorInfo attribute, boolean restarting) {
        super.onStartInput(attribute, restarting);
        mComposing.setLength(0);

        switch (attribute.inputType & InputType.TYPE_MASK_CLASS) {
            case InputType.TYPE_CLASS_NUMBER:
            case InputType.TYPE_CLASS_DATETIME:
                mCurKeyboard = mSymbolsKeyboard;
                break;
            case InputType.TYPE_CLASS_PHONE:
                mCurKeyboard = mSymbolsKeyboard;
                break;
            case InputType.TYPE_CLASS_TEXT:
                mCurKeyboard = mQwertyKeyboard;
                updateShiftKeyState(attribute);
                break;
            default:
                mCurKeyboard = mQwertyKeyboard;
                updateShiftKeyState(attribute);
        }
        // Update the label on the enter key
        mCurKeyboard.setImeOptions(getResources(), attribute.imeOptions);
    }

    @Override
    public void onFinishInput() {
        super.onFinishInput();
        mComposing.setLength(0);
        mCurKeyboard = mQwertyKeyboard;
        if (mInputView != null)
            mInputView.closing();
    }
    
    @Override
    public void onStartInputView(EditorInfo attribute, boolean restarting) {
        super.onStartInputView(attribute, restarting);
        setLatinKeyboard(mCurKeyboard);
        mInputView.closing();
        final InputMethodSubtype subtype = mInputMethodManager.getCurrentInputMethodSubtype();
        mInputView.setSubtypeOnSpaceKey(subtype);
    }

    @Override
    public void onCurrentInputMethodSubtypeChanged(InputMethodSubtype subtype) {
        mInputView.setSubtypeOnSpaceKey(subtype);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                // The InputMethodService already takes care of the back
                // key for us, to dismiss the input method if it is shown.
                // However, our keyboard could be showing a pop-up window
                // that back should dismiss, so we first allow it to do that.
                if (event.getRepeatCount() == 0 && mInputView != null) {
                    if (mInputView.handleBack())
                        return true;
                }
                break;
            case KeyEvent.KEYCODE_DEL:
                if (mComposing.length() > 0) {
                    onKey(Keyboard.KEYCODE_DELETE, null);
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_ENTER:
                // Let the underlying text editor always handle these.
                return false;
            default:
        }
        return super.onKeyDown(keyCode, event);
    }

    private void commitTyped(InputConnection inputConnection) {
        if (mComposing.length() > 0) {
            inputConnection.commitText(mComposing, mComposing.length());
            mComposing.setLength(0);
        }
    }

    private void updateShiftKeyState(EditorInfo attr) {
        if (attr == null || mInputView == null || mQwertyKeyboard != mInputView.getKeyboard())
            return;

        int caps = 0;
        EditorInfo ei = getCurrentInputEditorInfo();
        if (ei != null && ei.inputType != InputType.TYPE_NULL)
            caps = getCurrentInputConnection().getCursorCapsMode(attr.inputType);
        mInputView.setShifted(caps == 1);
    }

    private void keyDownUp(int keyEventCode) {
        getCurrentInputConnection().sendKeyEvent(
                new KeyEvent(KeyEvent.ACTION_DOWN, keyEventCode));
        getCurrentInputConnection().sendKeyEvent(
                new KeyEvent(KeyEvent.ACTION_UP, keyEventCode));
    }

    private void sendKey(int keyCode) {
        switch (keyCode) {
            case '\n':
                keyDownUp(KeyEvent.KEYCODE_ENTER);
                break;
            default:
                if (keyCode >= '0' && keyCode <= '9')
                    keyDownUp(keyCode - '0' + KeyEvent.KEYCODE_0);
                else
                    getCurrentInputConnection().commitText(String.valueOf((char) keyCode), 1);
                break;
        }
    }

    // *** Implementation of KeyboardViewListener *************************

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        Log.d("Test","KEYCODE: " + primaryCode);
        if (isWordSeparator(primaryCode)) {
            if (mComposing.length() > 0)
                commitTyped(getCurrentInputConnection());
            sendKey(primaryCode);
            updateShiftKeyState(getCurrentInputEditorInfo());
            return;
        }
        switch(primaryCode) {
            case Keyboard.KEYCODE_DELETE:
                handleBackspace();
                break;
            case Keyboard.KEYCODE_SHIFT:
                handleShift();
                break;
            case Keyboard.KEYCODE_CANCEL:
                handleClose();
                break;
            default:
                handleSwipedCharacter(primaryCode);
        }
    }

    private void handleSwipedCharacter(int primaryCode) {
        if (Keys.isKeyCodeWithinMultikeys(primaryCode))
            primaryCode = Keys.getKeyForCode(primaryCode).getCodeFor(mInputView.getSwipedDirection());
        if (isInputViewShown() && mInputView.isShifted())
            primaryCode = Character.toUpperCase(primaryCode);
        getCurrentInputConnection().commitText(String.valueOf((char) primaryCode), 1);
    }

    @Override
    public void onText(CharSequence text) {
        InputConnection ic = getCurrentInputConnection();
        if (ic == null) return;
        ic.beginBatchEdit();
        if (mComposing.length() > 0) {
            commitTyped(ic);
        }
        ic.commitText(text, 0);
        ic.endBatchEdit();
        updateShiftKeyState(getCurrentInputEditorInfo());
    }
    
    private void handleBackspace() {
        final int length = mComposing.length();
        if (length > 1) {
            mComposing.delete(length - 1, length);
            getCurrentInputConnection().setComposingText(mComposing, 1);
        } else if (length > 0) {
            mComposing.setLength(0);
            getCurrentInputConnection().commitText("", 0);
        } else
            keyDownUp(KeyEvent.KEYCODE_DEL);

        updateShiftKeyState(getCurrentInputEditorInfo());
    }

    private void handleShift() {
        if (mInputView == null)
            return;
        if (mInputView.getSwipedDirection() == Keys.DIRECTION_UP)
            mInputView.setShifted(true);
        if (mInputView.getSwipedDirection() == Keys.DIRECTION_DOWN)
            mInputView.setShifted(false);
        if (mInputView.getSwipedDirection() == Keys.NO_DIRECTION) {
            if (mInputView.getKeyboard() == mSymbolsKeyboard)
                setLatinKeyboard(mQwertyKeyboard);
            else
                setLatinKeyboard(mSymbolsKeyboard);
        }
    }

    private void handleClose() {
        commitTyped(getCurrentInputConnection());
        requestHideSelf(0);
        mInputView.closing();
    }
    
    public boolean isWordSeparator(int code) {
        return mWordSeparators.contains(String.valueOf((char)code));
    }

    @Override
    public void swipeRight() {
        onKey(this.pressedCode, null);
    }

    @Override
    public void swipeLeft() {
        onKey(this.pressedCode, null);
    }

    @Override
    public void swipeDown() {
        onKey(this.pressedCode, null);
    }

    @Override
    public void swipeUp() {
        onKey(this.pressedCode, null);
    }

    private int pressedCode;

    @Override
    public void onPress(int primaryCode) {
        this.pressedCode = primaryCode;
    }

    @Override
    public void onRelease(int primaryCode) {

    }

}
