package com.astro.slidekeyboard;

import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

import com.android.inputmethodcommon.InputTypeUtils;

import java.util.List;

public class SlideKeyboard extends InputMethodService implements KeyboardView.OnKeyboardActionListener {

    private LatinKeyboardView mInputView;
    
    private final StringBuilder mComposing = new StringBuilder();

    private KeyboardService keyboardService;
    private LatinKeyboard mCurKeyboard;
    private String mWordSeparators;

    private static final int EMOJI_KEYCODE = -69;

    @Override
    public void onCreate() {
        super.onCreate();
        mWordSeparators = getResources().getString(R.string.word_separators);
    }

    @Override
    public void onInitializeInterface() {
        this.keyboardService = new KeyboardService(this);
    }

    @Override
    public View onCreateInputView() {
        mInputView = (LatinKeyboardView) getLayoutInflater().inflate(
                R.layout.input, null);
        mInputView.setOnKeyboardActionListener(this);
        mInputView.setPreviewEnabled(false);
        setLatinKeyboard(keyboardService.getQwertyKeyboard());
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
            case InputType.TYPE_CLASS_PHONE:
                mCurKeyboard = keyboardService.getSymbolKeyboard();
                break;
            default:
                mCurKeyboard = keyboardService.getQwertyKeyboard();
                updateShiftKeyState(attribute);
        }
        // Update the label on the enter key
        mCurKeyboard.setImeOptions(getResources(), attribute.imeOptions);
    }

    @Override
    public void onFinishInput() {
        super.onFinishInput();
        mComposing.setLength(0);
        mCurKeyboard = keyboardService.getQwertyKeyboard();
        if (mInputView != null)
            mInputView.closing();
    }
    
    @Override
    public void onStartInputView(EditorInfo attribute, boolean restarting) {
        super.onStartInputView(attribute, restarting);
        setLatinKeyboard(mCurKeyboard);
        mInputView.closing();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
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
                // Let the text editor always handle these.
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
        if (attr == null || mInputView == null || keyboardService.getQwertyKeyboard() != mInputView.getKeyboard())
            return;

        int caps = 0;
        EditorInfo ei = getCurrentInputEditorInfo();
        if (ei != null && ei.inputType != InputType.TYPE_NULL)
            caps = getCurrentInputConnection().getCursorCapsMode(attr.inputType);
        mInputView.setShifted(caps == 1);
        mCurKeyboard.changeToggleShiftIcon(getResources(), keyboardService.getQwertyKeyboard().isShifted());
    }

    private void keyDownUp(int keyEventCode) {
        getCurrentInputConnection().sendKeyEvent(
                new KeyEvent(KeyEvent.ACTION_DOWN, keyEventCode));
        getCurrentInputConnection().sendKeyEvent(
                new KeyEvent(KeyEvent.ACTION_UP, keyEventCode));
    }

    private void sendKey(int keyCode) {
        final EditorInfo editorInfo = getCurrentInputEditorInfo();
        final int imeOptionsActionId = InputTypeUtils.getImeOptionsActionIdFromEditorInfo(editorInfo);
        if (keyCode == '\n') { // Special case handling new line as well as input submit
            if (imeOptionsActionId != EditorInfo.IME_ACTION_NONE)
                getCurrentInputConnection().performEditorAction(imeOptionsActionId);
            else
                keyDownUp(KeyEvent.KEYCODE_ENTER);
        } else {
            if (keyCode >= '0' && keyCode <= '9')
                keyDownUp(keyCode - '0' + KeyEvent.KEYCODE_0);
            else
                getCurrentInputConnection().commitText(String.valueOf((char) keyCode), 1);}
    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        switch(primaryCode) {
            case Keyboard.KEYCODE_DELETE:
                handleBackspace();
                return;
            case EMOJI_KEYCODE:
                handleEmojiKey();
                return;
            case Keyboard.KEYCODE_SHIFT:
                handleShift();
                return;
            case Keyboard.KEYCODE_CANCEL:
                handleClose();
                return;
        }
        if (Keys.isKeyCodeWithinMultikeys(primaryCode)) {
            handleSwipedCharacter(primaryCode);
            return;
        }
        if (isWordSeparator(primaryCode) && mComposing.length() > 0)
            commitTyped(getCurrentInputConnection());
        sendKey(mInputView.isShifted() ? Character.toUpperCase(primaryCode) : primaryCode);
        updateShiftKeyState(getCurrentInputEditorInfo());
    }

    private void handleSwipedCharacter(int primaryCode) {
        String retrievedText = Keys.getKeyForCode(primaryCode).getCodeFor(mInputView.getSwipedDirection());
        if (isInputViewShown() && mInputView.isShifted())
            retrievedText = retrievedText.toUpperCase();
        getCurrentInputConnection().commitText(retrievedText, 1);
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
        var swipedDirection = mInputView.getSwipedDirection();
        if (List.of(Keys.DIRECTION_UP, Keys.DIRECTION_DOWN).contains(swipedDirection))
            mInputView.setShifted(swipedDirection == Keys.DIRECTION_UP);
        if (swipedDirection == Keys.NO_DIRECTION) {
            if (mInputView.getKeyboard() == keyboardService.getSymbolKeyboard())
                setLatinKeyboard(keyboardService.getQwertyKeyboard());
            else
                setLatinKeyboard(keyboardService.getSymbolKeyboard());
        }
        mCurKeyboard.changeToggleShiftIcon(getResources(), keyboardService.getQwertyKeyboard().isShifted());
    }

    private void handleEmojiKey() {
        if (mInputView.getKeyboard() == keyboardService.getEmojiKeyboard())
            setLatinKeyboard(keyboardService.getQwertyKeyboard());
        else
            setLatinKeyboard(keyboardService.getEmojiKeyboard());
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
    public void onRelease(int primaryCode) {}

}
