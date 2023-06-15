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
    SYMBOL_TOP_RIGHT(79, "`", "'", "´", "\"", "3"),
    SYMBOL_CENTER_LEFT(80, "\\", ":", "/", ";", "4"),
    SYMBOL_CENTER_CENTER(81, "~", "±", "×", "÷", "5"),
    SYMBOL_CENTER_RIGHT(82, "©","•","®","°","6"),
    SYMBOL_BOTTOM_LEFT(83, "¥","£","€","¢","7"),
    SYMBOL_BOTTOM_CENTER(84, "<","^",">","$","8"),
    SYMBOL_BOTTOM_RIGHT(85, "|", "¡", "¿", "%", "9"),
    SYMBOL_SPECIAL_RIGHT(86, "{", " ", "}", " "),

    EMOJI_TOP_LEFT(87,"\uD83D\uDE14", "\uD83E\uDD23", "\uD83D\uDE02","\uD83D\uDE0A", "\uD83D\uDE03"),
    EMOJI_TOP_CENTER(88,"\uD83D\uDE07","\uD83E\uDD70","\uD83D\uDE0D","\uD83E\uDD17","\uD83D\uDE18"),
    EMOJI_TOP_RIGHT(89,"\uD83E\uDD14","\uD83E\uDD28","\uD83D\uDE0F","☺️","\uD83E\uDD72"),
    EMOJI_CENTER_LEFT(90,"\uD83D\uDE0C", "\uD83D\uDE22", "\uD83D\uDE0E", "\uD83D\uDE1A", "\uD83D\uDE12"),
    EMOJI_CENTER_CENTER(91,"\uD83E\uDD7A","\uD83D\uDE28","\uD83D\uDE31","\uD83D\uDE1E", "\uD83D\uDE2D"),
    EMOJI_CENTER_RIGHT(92,"\uD83D\uDC80","\uD83E\uDD21","\uD83D\uDC4C","\uD83D\uDC4D","\uD83D\uDE44"),
    EMOJI_BOTTOM_LEFT(93,"\uD83D\uDC4F","\uD83D\uDE4C","\uD83D\uDE4F","\uD83D\uDC40","\uD83D\uDC8B"),
    EMOJI_BOTTOM_CENTER(94,"\uD83D\uDCA5","\uD83D\uDCA6","\uD83C\uDF1A","✨","\uD83E\uDD1D"),
    EMOJI_BOTTOM_RIGHT(95,"\uD83D\uDC49","\uD83C\uDF08","\uD83D\uDC48","\uD83E\uDEC2","\uD83D\uDDFF"),
    EMOJI_SPECIAL_LEFT(96,"\uD83D\uDD2A","\uD83D\uDCB8","\uD83D\uDD2B","\uD83D\uDCAF","\uD83E\uDDE0"),
    EMOJI_SPECIAL_CENTER(97,"\uD83D\uDC9B","\uD83D\uDC96","\uD83D\uDC9D","\uD83D\uDC95","❤️"),
    EMOJI_SPECIAL_RIGHT(98,"✅","\uD83D\uDD1D","\uD83D\uDCA9"," "),

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
