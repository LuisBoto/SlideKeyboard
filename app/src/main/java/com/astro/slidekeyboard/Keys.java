package com.astro.slidekeyboard;

public enum Keys {

    TOP_LEFT(65," ", " ", " "," ", "1"),
    TOP_CENTER(66,"a","b","c","ç","2"),
    TOP_RIGHT(67,"d","e","f"," ","3"),
    CENTER_LEFT(68,"g", "h", "i", " ", "4"),
    CENTER_CENTER(69,"j","k","l"," ", "5"),
    CENTER_RIGHT(70,"m","n","o","ñ","6"),
    BOTTOM_LEFT(71,"p","q","r","s","7"),
    BOTTOM_CENTER(72,"t","u","v"," ","8"),
    BOTTOM_RIGHT(73,"w","x","y","z","9"),
    SPECIAL_LEFT(74,"-","/","_","@","*"),
    SPECIAL_CENTER(75,".","!",",","?","0"),
    SPECIAL_RIGHT(76," "," "," "," "),

    SYMBOL_TOP_LEFT(77, "[", "#", "]", "&", "1"),
    SYMBOL_TOP_CENTER(78, "(", "=", ")", "+", "2"),
    SYMBOL_TOP_RIGHT(79, "`", "\"", "´", "'", "3"),
    SYMBOL_CENTER_LEFT(80, "\\", ":", "/", ";", "4"),
    SYMBOL_CENTER_CENTER(81, "~", "±", "×", "÷", "5"),
    SYMBOL_CENTER_RIGHT(82, "©","•","®","°","6"),
    SYMBOL_BOTTOM_LEFT(83, "¥","£","€","¢","7"),
    SYMBOL_BOTTOM_CENTER(84, "<","^",">","$","8"),
    SYMBOL_BOTTOM_RIGHT(85, "|", "¡", "¿", "%", "9"),
    SYMBOL_SPECIAL_RIGHT(86, "{", " ", "}", " "),

    EMOJI_TOP_LEFT(87,"\uD83D\uDE03", "\uD83E\uDD23", "\uD83D\uDE02","\uD83D\uDE0A", "1"),
    EMOJI_TOP_CENTER(88,"a","b","c","ç","2"),
    EMOJI_TOP_RIGHT(89,"d","e","f"," ","3"),
    EMOJI_CENTER_LEFT(90,"g", "h", "i", " ", "4"),
    EMOJI_CENTER_CENTER(91,"j","k","l"," ", "5"),
    EMOJI_CENTER_RIGHT(92,"m","n","o","ñ","6"),
    EMOJI_BOTTOM_LEFT(93,"p","q","r","s","7"),
    EMOJI_BOTTOM_CENTER(94,"t","u","v"," ","8"),
    EMOJI_BOTTOM_RIGHT(95,"w","x","y","z","9"),
    EMOJI_SPECIAL_LEFT(96,"-","/","_","@","*"),
    EMOJI_SPECIAL_CENTER(97,".","!",",","?","0"),
    EMOJI_SPECIAL_RIGHT(98," "," "," "," "),

    NO_CHARACTER(0, " "," "," ", " ", " ");

    public static int KEYCODE_START = 65;
    public static int KEYCODE_FINISH = Keys.values().length + KEYCODE_START;
    private boolean shifted = false;
    private int keyCode;
    private String west, north, east, south, backSymbol;

    private Keys(int keyCode, String west, String north, String east, String south, String backSymbol) {
        this(keyCode, west, north, east, south);
        this.backSymbol = backSymbol;
    }

    private Keys(int keyCode, String west, String north, String east, String south) {
        this.keyCode = keyCode;
        this.west = west;
        this.north = north;
        this.east = east;
        this.south = south;
        this.backSymbol = " ";
    }

    public Keys setShifted(boolean shift) {
        this.shifted = shift;
        return this;
    }

    public int getKeyCode() {
        return this.keyCode;
    }

    public String getWest() {
        return this.shifted ? this.west.toUpperCase() : this.west;
    }

    public String getNorth() {
        return this.shifted ? this.north.toUpperCase() : this.north;
    }

    public String getEast() {
        return this.shifted ? this.east.toUpperCase() : this.east;
    }

    public String getSouth() {
        return this.shifted ? this.south.toUpperCase() : this.south;
    }

    public String getBackSymbol() {
        return this.backSymbol;
    }

    protected static final int DIRECTION_UP = 0;
    protected static final int DIRECTION_LEFT = 1;
    protected static final int DIRECTION_RIGHT = 2;
    protected static final int DIRECTION_DOWN = 3;
    protected static final int NO_DIRECTION = 4;

    public String getCodeFor(int direction) {
        switch (direction) {
            case DIRECTION_UP:
                return this.getNorth();
            case DIRECTION_DOWN:
                return this.getSouth();
            case DIRECTION_LEFT:
                return this.getWest();
            case DIRECTION_RIGHT:
                return this.getEast();
        }
        return this.getBackSymbol();
    }

    public static int determineSwipedDirection(float initialX, float initialY, float finalX, float finalY, double threshold) {
        float deltaX = Math.abs(finalX - initialX);
        float deltaY = Math.abs(finalY - initialY);

        if (deltaX > threshold || deltaY > threshold) {
            if (deltaX > deltaY) {
                if (initialX < finalX)
                    return DIRECTION_RIGHT;
                return DIRECTION_LEFT;
            } if (deltaY > deltaX) {
                if (initialY < finalY)
                    return DIRECTION_DOWN;
                return DIRECTION_UP;
            }
        }
        return NO_DIRECTION;
    }

    public static Keys getKeyForCode(int keyCode) {
        for (Keys key : Keys.values())
            if (key.getKeyCode() == keyCode)
                return key;
        return Keys.NO_CHARACTER;
    }

    public static boolean isKeyCodeWithinMultikeys(int keycode) {
        return keycode >= KEYCODE_START && keycode <= KEYCODE_FINISH;
    }
}
